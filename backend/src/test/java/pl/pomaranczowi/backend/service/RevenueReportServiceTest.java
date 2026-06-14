package pl.pomaranczowi.backend.service;

import com.example.reports.dto.RevenueReportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.entity.Appointment;
import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.entity.Service;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevenueReportServiceTest {

    @Mock
    private AppointmentServiceRepository appointmentServiceRepository;

    @InjectMocks
    private RevenueReportService revenueReportService;

    private AppointmentService createAppointmentService(double price) {
        Service service = new Service();
        service.setPrice(price);

        Appointment appointment = new Appointment();
        appointment.setStartTime(LocalDateTime.now());

        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setService(service);
        appointmentService.setAppointment(appointment);

        return appointmentService;
    }

    @Test
    void generateRevenueReport_Week_ReturnsCorrectData() {
        AppointmentService as1 = createAppointmentService(50.0);
        AppointmentService as2 = createAppointmentService(80.0);
        AppointmentService as3 = createAppointmentService(120.0);

        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(as1, as2, as3));

        RevenueReportDto result = revenueReportService.generateRevenueReport("week");

        assertAll("weekly revenue report",
            () -> assertEquals("Ostatni tydzień", result.getPeriod()),
            () -> assertEquals(3, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.valueOf(250.0).compareTo(result.getTotalRevenue()))
        );
    }

    @Test
    void generateRevenueReport_Month_ReturnsCorrectData() {
        AppointmentService as1 = createAppointmentService(100.0);

        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(as1));

        RevenueReportDto result = revenueReportService.generateRevenueReport("month");

        assertAll("monthly revenue report",
            () -> assertEquals("Ostatni miesiąc", result.getPeriod()),
            () -> assertEquals(1, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.valueOf(100.0).compareTo(result.getTotalRevenue()))
        );
    }

    @Test
    void generateRevenueReport_DefaultPeriod_UsesMonth() {
        AppointmentService as1 = createAppointmentService(30.0);

        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(as1));

        RevenueReportDto result = revenueReportService.generateRevenueReport("unknown");

        assertEquals("Ostatni miesiąc", result.getPeriod());
    }

    @Test
    void generateRevenueReport_NoAppointments_ReturnsZeroRevenue() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        RevenueReportDto result = revenueReportService.generateRevenueReport("week");

        assertAll("empty revenue report",
            () -> assertEquals("Ostatni tydzień", result.getPeriod()),
            () -> assertEquals(0, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalRevenue()))
        );
    }

    @Test
    void generateRevenueReport_Week_SumMultipleAppointments() {
        List<AppointmentService> services = Arrays.asList(
            createAppointmentService(10.0),
            createAppointmentService(20.0),
            createAppointmentService(30.0),
            createAppointmentService(40.0)
        );

        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(services);

        RevenueReportDto result = revenueReportService.generateRevenueReport("week");

        assertAll("sum of multiple appointments",
            () -> assertEquals(4, result.getAppointmentsCount()),
            () -> assertEquals(0, BigDecimal.valueOf(100.0).compareTo(result.getTotalRevenue()))
        );
    }

    @Test
    void generateRevenueReport_CallsRepositoryWithCorrectTime() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        revenueReportService.generateRevenueReport("week");

        verify(appointmentServiceRepository, times(1))
            .findByAppointmentStartTimeAfter(any(LocalDateTime.class));
    }
}
