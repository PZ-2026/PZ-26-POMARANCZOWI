package pl.pomaranczowi.backend.service;

import com.example.reports.dto.PopularServiceDto;
import com.example.reports.dto.ServicePopularityReportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.entity.Appointment;
import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.entity.Service;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicePopularityReportServiceTest {

    @Mock
    private AppointmentServiceRepository appointmentServiceRepository;

    @InjectMocks
    private ServicePopularityReportService servicePopularityReportService;

    private AppointmentService createAppointmentService(String serviceName) {
        Service service = new Service();
        service.setName(serviceName);

        Appointment appointment = new Appointment();
        appointment.setStartTime(LocalDateTime.now());

        AppointmentService appointmentService = new AppointmentService();
        appointmentService.setService(service);
        appointmentService.setAppointment(appointment);

        return appointmentService;
    }

    @Test
    void generateReport_MultipleServices_SortedByPopularity() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(
                createAppointmentService("Haircut"),
                createAppointmentService("Beard Trim"),
                createAppointmentService("Haircut"),
                createAppointmentService("Haircut"),
                createAppointmentService("Beard Trim"),
                createAppointmentService("Shave")
            ));

        ServicePopularityReportDto result = servicePopularityReportService.generateReport();

        assertAll("popularity report sorted",
            () -> assertEquals(6, result.getTotalServices()),
            () -> assertEquals(3, result.getUniqueServices()),
            () -> assertEquals("Haircut", result.getMostPopularService()),
            () -> assertNotNull(result.getServices()),
            () -> assertEquals(3, result.getServices().size())
        );

        List<PopularServiceDto> services = result.getServices();
        assertEquals("Haircut", services.get(0).getServiceName());
        assertEquals(3, services.get(0).getCount());
        assertEquals("Beard Trim", services.get(1).getServiceName());
        assertEquals(2, services.get(1).getCount());
        assertEquals("Shave", services.get(2).getServiceName());
        assertEquals(1, services.get(2).getCount());
    }

    @Test
    void generateReport_SingleService_ReturnsOneEntry() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(
                createAppointmentService("Haircut"),
                createAppointmentService("Haircut")
            ));

        ServicePopularityReportDto result = servicePopularityReportService.generateReport();

        assertAll("single service report",
            () -> assertEquals(2, result.getTotalServices()),
            () -> assertEquals(1, result.getUniqueServices()),
            () -> assertEquals("Haircut", result.getMostPopularService()),
            () -> assertEquals(1, result.getServices().size()),
            () -> assertEquals(2, result.getServices().get(0).getCount())
        );
    }

    @Test
    void generateReport_NoData_ReturnsEmptyReport() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        ServicePopularityReportDto result = servicePopularityReportService.generateReport();

        assertAll("empty popularity report",
            () -> assertEquals(0, result.getTotalServices()),
            () -> assertEquals(0, result.getUniqueServices()),
            () -> assertEquals("Brak danych", result.getMostPopularService()),
            () -> assertTrue(result.getServices().isEmpty())
        );
    }

    @Test
    void generateReport_EqualPopularity_MaintainsOrder() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(
                createAppointmentService("Shave"),
                createAppointmentService("Haircut")
            ));

        ServicePopularityReportDto result = servicePopularityReportService.generateReport();

        assertEquals(2, result.getUniqueServices());
        assertEquals(2, result.getServices().size());
    }

    @Test
    void generateReport_CallsRepositoryWithCorrectTime() {
        when(appointmentServiceRepository.findByAppointmentStartTimeAfter(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        servicePopularityReportService.generateReport();

        verify(appointmentServiceRepository, times(1))
            .findByAppointmentStartTimeAfter(any(LocalDateTime.class));
    }
}
