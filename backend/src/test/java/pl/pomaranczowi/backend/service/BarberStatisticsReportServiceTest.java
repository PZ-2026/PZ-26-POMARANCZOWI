package pl.pomaranczowi.backend.service;

import com.example.reports.dto.BarberStatisticsReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.entity.Appointment;
import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.Service;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;
import pl.pomaranczowi.backend.repository.BarberRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarberStatisticsReportServiceTest {

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private AppointmentServiceRepository appointmentServiceRepository;

    @InjectMocks
    private BarberStatisticsReportService barberStatisticsReportService;

    private Barber testBarber;

    @BeforeEach
    void setUp() {
        User barberUser = new User();
        barberUser.setName("John Barber");

        testBarber = new Barber();
        testBarber.setBarberId(1L);
        testBarber.setUser(barberUser);
        testBarber.setSpecialization("Fade");
    }

    private AppointmentService createAppointmentService(double price) {
        Service service = new Service();
        service.setPrice(price);

        Appointment appointment = new Appointment();
        appointment.setBarber(testBarber);

        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setService(service);
        appointmentService.setAppointment(appointment);

        return appointmentService;
    }

    @Test
    void generateReport_WithVisits_ReturnsCorrectStatistics() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(appointmentServiceRepository.findByAppointmentBarberBarberId(1L))
            .thenReturn(Arrays.asList(
                createAppointmentService(100.0),
                createAppointmentService(50.0),
                createAppointmentService(150.0)
            ));

        BarberStatisticsReportDto result = barberStatisticsReportService.generateReport(1L);

        assertAll("barber statistics with visits",
            () -> assertEquals("John Barber", result.getBarberName()),
            () -> assertEquals(3, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.valueOf(300.0).compareTo(result.getTotalRevenue())),
            () -> assertEquals(0, BigDecimal.valueOf(100.0).compareTo(result.getAverageRevenuePerVisit()))
        );
    }

    @Test
    void generateReport_NoVisits_ReturnsZeroRevenue() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(appointmentServiceRepository.findByAppointmentBarberBarberId(1L))
            .thenReturn(Collections.emptyList());

        BarberStatisticsReportDto result = barberStatisticsReportService.generateReport(1L);

        assertAll("barber statistics with no visits",
            () -> assertEquals("John Barber", result.getBarberName()),
            () -> assertEquals(0, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalRevenue())),
            () -> assertEquals(0, BigDecimal.ZERO.compareTo(result.getAverageRevenuePerVisit()))
        );
    }

    @Test
    void generateReport_SingleVisit_RevenueEqualsAverage() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(appointmentServiceRepository.findByAppointmentBarberBarberId(1L))
            .thenReturn(Arrays.asList(createAppointmentService(75.0)));

        BarberStatisticsReportDto result = barberStatisticsReportService.generateReport(1L);

        assertAll("barber statistics single visit",
            () -> assertEquals(1, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.valueOf(75.0).compareTo(result.getTotalRevenue())),
            () -> assertEquals(0, BigDecimal.valueOf(75.0).compareTo(result.getAverageRevenuePerVisit()))
        );
    }

    @Test
    void generateReport_BarberNotFound_ThrowsException() {
        when(barberRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> barberStatisticsReportService.generateReport(999L));

        assertEquals("Barber not found", exception.getMessage());
        verify(appointmentServiceRepository, never()).findByAppointmentBarberBarberId(any());
    }
}
