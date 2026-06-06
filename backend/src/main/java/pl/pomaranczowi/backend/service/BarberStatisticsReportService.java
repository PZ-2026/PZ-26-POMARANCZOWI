package pl.pomaranczowi.backend.service;

import com.example.reports.dto.BarberStatisticsReportDto;

import org.springframework.stereotype.Service;

import pl.pomaranczowi.backend.entity.AppointmentService;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.repository.AppointmentServiceRepository;
import pl.pomaranczowi.backend.repository.BarberRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BarberStatisticsReportService {

    private final BarberRepository barberRepository;

    private final AppointmentServiceRepository
            appointmentServiceRepository;

    public BarberStatisticsReportService(
            BarberRepository barberRepository,
            AppointmentServiceRepository appointmentServiceRepository
    ) {
        this.barberRepository =
                barberRepository;

        this.appointmentServiceRepository =
                appointmentServiceRepository;
    }

    public BarberStatisticsReportDto generateReport(
            Long barberId
    ) {

        Barber barber =
                barberRepository
                        .findById(barberId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Barber not found"
                                )
                        );

        List<AppointmentService> services =
                appointmentServiceRepository
                        .findByAppointmentBarberBarberId(
                                barberId
                        );

        BigDecimal totalRevenue =
                services.stream()
                        .map(s ->
                                BigDecimal.valueOf(
                                        s.getService()
                                                .getPrice()
                                )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int visitsCount =
                services.size();

        BigDecimal averageRevenue =
                visitsCount == 0
                        ? BigDecimal.ZERO
                        : totalRevenue.divide(
                                BigDecimal.valueOf(
                                        visitsCount
                                ),
                                2,
                                RoundingMode.HALF_UP
                        );

        BarberStatisticsReportDto dto =
                new BarberStatisticsReportDto();

        dto.setBarberName(
                barber.getUser().getName()
        );

        dto.setAppointmentsCount(
                visitsCount
        );

        dto.setTotalRevenue(
                totalRevenue
        );

        dto.setAverageRevenuePerVisit(
                averageRevenue
        );

        return dto;
    }
}