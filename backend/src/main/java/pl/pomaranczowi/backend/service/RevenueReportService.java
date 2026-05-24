package pl.pomaranczowi.backend.service;

import com.example.reports.dto.RevenueReportDto;

import org.springframework.stereotype.Service;

import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class RevenueReportService {

    private final AppointmentServiceRepository
            appointmentServiceRepository;

    public RevenueReportService(
            AppointmentServiceRepository
                    appointmentServiceRepository
    ) {
        this.appointmentServiceRepository =
                appointmentServiceRepository;
    }

    public RevenueReportDto generateRevenueReport(
            String period
    ) {

        LocalDateTime dateFrom;

        String periodLabel;

        if (period.equalsIgnoreCase("week")) {

            dateFrom =
                    LocalDateTime.now().minusWeeks(1);

            periodLabel = "Ostatni tydzień";

        } else {

            dateFrom =
                    LocalDateTime.now().minusMonths(1);

            periodLabel = "Ostatni miesiąc";
        }

        List<AppointmentService> appointmentServices =
                appointmentServiceRepository
                        .findByAppointmentStartTimeAfter(
                                dateFrom
                        );

        BigDecimal totalRevenue =
                appointmentServices.stream()
                        .map(appointmentService ->
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

        RevenueReportDto dto =
                new RevenueReportDto();

        dto.setPeriod(periodLabel);

        dto.setAppointmentsCount(
                appointmentServices.size()
        );

        dto.setTotalRevenue(totalRevenue);

        return dto;
    }
}