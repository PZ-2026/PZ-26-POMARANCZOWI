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

    public RevenueReportDto generateMonthlyRevenueReport() {

        LocalDateTime monthAgo =
                LocalDateTime.now().minusMonths(1);

        List<AppointmentService> appointmentServices =
                appointmentServiceRepository
                        .findByAppointmentStartTimeAfter(
                                monthAgo
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

        dto.setPeriod("Ostatni miesiąc");

        dto.setAppointmentsCount(
                appointmentServices.size()
        );

        dto.setTotalRevenue(totalRevenue);

        return dto;
    }
}