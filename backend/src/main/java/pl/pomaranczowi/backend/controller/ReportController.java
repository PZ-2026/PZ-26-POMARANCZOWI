package pl.pomaranczowi.backend.controller;

import com.example.reports.dto.BarberStatisticsReportDto;
import com.example.reports.dto.RevenueReportDto;
import com.example.reports.dto.ServicePopularityReportDto;

import com.example.reports.generator.BarberStatisticsPdfGenerator;
import com.example.reports.generator.RevenuePdfGenerator;
import com.example.reports.generator.ServicePopularityPdfGenerator;

import com.example.reports.service.PdfDocumentService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.pomaranczowi.backend.service.BarberStatisticsReportService;
import pl.pomaranczowi.backend.service.RevenueReportService;
import pl.pomaranczowi.backend.service.ServicePopularityReportService;

@RestController
public class ReportController {

    private final RevenueReportService
            revenueReportService;

    private final BarberStatisticsReportService
            barberStatisticsReportService;

    private final ServicePopularityReportService
            servicePopularityReportService;

    public ReportController(
            RevenueReportService revenueReportService,
            BarberStatisticsReportService barberStatisticsReportService,
            ServicePopularityReportService servicePopularityReportService
    ) {
        this.revenueReportService =
                revenueReportService;

        this.barberStatisticsReportService =
                barberStatisticsReportService;

        this.servicePopularityReportService =
                servicePopularityReportService;
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<byte[]> generateRevenueReport(
            @RequestParam(defaultValue = "month")
            String period
    ) {

        RevenueReportDto dto =
                revenueReportService
                        .generateRevenueReport(period);

        RevenuePdfGenerator generator =
                new RevenuePdfGenerator(
                        new PdfDocumentService()
                );

        byte[] pdf = generator.generate(dto);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=revenue-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/reports/barber/{barberId}")
    public ResponseEntity<byte[]> generateBarberReport(
            @PathVariable Long barberId
    ) {

        BarberStatisticsReportDto dto =
                barberStatisticsReportService
                        .generateReport(barberId);

        BarberStatisticsPdfGenerator generator =
                new BarberStatisticsPdfGenerator(
                        new PdfDocumentService()
                );

        byte[] pdf = generator.generate(dto);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=barber-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/reports/services-popularity")
    public ResponseEntity<byte[]> generateServicesPopularityReport() {

        ServicePopularityReportDto dto =
                servicePopularityReportService
                        .generateReport();

        ServicePopularityPdfGenerator generator =
                new ServicePopularityPdfGenerator(
                        new PdfDocumentService()
                );

        byte[] pdf =
                generator.generate(dto);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=services-popularity-report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}