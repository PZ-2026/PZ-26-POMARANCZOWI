package pl.pomaranczowi.backend.service;

import com.example.reports.dto.PopularServiceDto;
import com.example.reports.dto.ServicePopularityReportDto;

import org.springframework.stereotype.Service;

import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Service
public class ServicePopularityReportService {

    private final AppointmentServiceRepository
            appointmentServiceRepository;

    public ServicePopularityReportService(
            AppointmentServiceRepository
                    appointmentServiceRepository
    ) {
        this.appointmentServiceRepository =
                appointmentServiceRepository;
    }

    public ServicePopularityReportDto generateReport() {

        LocalDateTime monthAgo =
                LocalDateTime.now().minusMonths(1);

        List<AppointmentService> appointments =
                appointmentServiceRepository
                        .findByAppointmentStartTimeAfter(
                                monthAgo
                        );

        Map<String, Long> grouped =
                appointments.stream()
                        .collect(
                                Collectors.groupingBy(
                                        a -> a.getService()
                                                .getName(),
                                        Collectors.counting()
                                )
                        );

        List<PopularServiceDto> services =
                grouped.entrySet()
                        .stream()
                        .sorted(
                                Map.Entry
                                        .<String, Long>comparingByValue()
                                        .reversed()
                        )
                        .map(entry -> {

                            PopularServiceDto dto =
                                    new PopularServiceDto();

                            dto.setServiceName(
                                    entry.getKey()
                            );

                            dto.setCount(
                                    entry.getValue()
                                            .intValue()
                            );

                            return dto;
                        })
                        .collect(Collectors.toList());

        ServicePopularityReportDto dto =
                new ServicePopularityReportDto();

        dto.setServices(services);

        dto.setTotalServices(
                appointments.size()
        );

        dto.setUniqueServices(
                services.size()
        );

        dto.setMostPopularService(
                services.isEmpty()
                        ? "Brak danych"
                        : services.get(0)
                                .getServiceName()
        );

        return dto;
    }
}