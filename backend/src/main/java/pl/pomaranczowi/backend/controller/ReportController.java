package pl.pomaranczowi.backend.controller;

import com.example.reports.dto.RevenueReportDto;
import com.example.reports.generator.RevenuePdfGenerator;
import com.example.reports.service.PdfDocumentService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.pomaranczowi.backend.service.RevenueReportService;

@RestController
public class ReportController {

    private final RevenueReportService
            revenueReportService;

    public ReportController(
            RevenueReportService revenueReportService
    ) {
        this.revenueReportService =
                revenueReportService;
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<byte[]> generateRevenueReport() {

        RevenueReportDto dto =
                revenueReportService
                        .generateMonthlyRevenueReport();

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
}