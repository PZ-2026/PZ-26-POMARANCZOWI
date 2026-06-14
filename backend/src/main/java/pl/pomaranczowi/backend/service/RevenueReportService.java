package pl.pomaranczowi.backend.service;

import com.example.reports.dto.RevenueReportDto;
import com.example.reports.dto.ServiceRevenueDto;

import org.springframework.stereotype.Service;

import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;

import com.example.reports.dto.ServiceRevenueDto;
import java.util.Comparator;

/**
 * Service for generating revenue reports over a specified period (week or month).
 * Calculates total revenue from all appointment-service records within the period.
 */
@Service
public class RevenueReportService {

    private final AppointmentServiceRepository appointmentServiceRepository;

    public RevenueReportService(AppointmentServiceRepository appointmentServiceRepository) {
        this.appointmentServiceRepository = appointmentServiceRepository;
    }

    /**
     * Generates a revenue report for the given period.
     * <ul>
     *   <li>"week" - last 7 days</li>
     *   <li>Any other value - last month (default)</li>
     * </ul>
     *
     * @param period the report period ("week" or "month")
     * @return the revenue report DTO containing period label, appointment count, and total revenue
     */
    public RevenueReportDto generateRevenueReport(
        String period
) {

    LocalDateTime dateFrom;
    String periodLabel;

    if (period.equalsIgnoreCase("week")) {

        dateFrom =
                LocalDateTime.now()
                        .minusWeeks(1);

        periodLabel =
                "Ostatni tydzień";

    } else {

        dateFrom =
                LocalDateTime.now()
                        .minusMonths(1);

        periodLabel =
                "Ostatni miesiąc";
    }

    List<AppointmentService> appointmentServices =
            appointmentServiceRepository
                    .findByAppointmentStartTimeAfter(
                            dateFrom
                    );

    BigDecimal totalRevenue =
            appointmentServices.stream()
                    .map(
                            appointmentService ->
                                    BigDecimal.valueOf(
                                            appointmentService
                                                    .getService()
                                                    .getPrice()
                                    )
                    )
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );

    List<ServiceRevenueDto> servicesRevenue =
            appointmentServices.stream()
                    .collect(
                            java.util.stream.Collectors
                                    .groupingBy(
                                            appointmentService ->
                                                    appointmentService
                                                            .getService()
                                                            .getName(),
                                            java.util.stream.Collectors
                                                    .mapping(
                                                            appointmentService ->
                                                                    BigDecimal.valueOf(
                                                                            appointmentService
                                                                                    .getService()
                                                                                    .getPrice()
                                                                    ),
                                                            java.util.stream.Collectors
                                                                    .reducing(
                                                                            BigDecimal.ZERO,
                                                                            BigDecimal::add
                                                                    )
                                                    )
                                    )
                    )
                    .entrySet()
                    .stream()
                    .map(entry -> {

                        ServiceRevenueDto dto =
                                new ServiceRevenueDto();

                        dto.setServiceName(
                                entry.getKey()
                        );

                        dto.setRevenue(
                                entry.getValue()
                        );

                        return dto;
                    })
                    .sorted(
                            java.util.Comparator
                                    .comparing(
                                            ServiceRevenueDto::getRevenue
                                    )
                                    .reversed()
                    )
                    .toList();

    RevenueReportDto dto =
            new RevenueReportDto();

    dto.setPeriod(
            periodLabel
    );

    dto.setAppointmentsCount(
            appointmentServices.size()
    );

    dto.setTotalRevenue(
            totalRevenue
    );

    dto.setServicesRevenue(
            servicesRevenue
    );

    return dto;
}
}
