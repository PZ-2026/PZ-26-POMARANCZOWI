package pl.pomaranczowi.backend.controller;

import com.example.reports.dto.UserActivityReportDto;
import com.example.reports.generator.UserActivityPdfGenerator;
import com.example.reports.service.PdfDocumentService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportController {

    @GetMapping("/reports/test")
    public ResponseEntity<byte[]> generatePdf() {

        UserActivityReportDto dto =
                new UserActivityReportDto();

        dto.setUsername("Jan");

        dto.setActivities(
                List.of(
                        "Logowanie",
                        "Dodanie wizyty",
                        "Wylogowanie"
                )
        );

        UserActivityPdfGenerator generator =
                new UserActivityPdfGenerator(
                        new PdfDocumentService()
                );

        byte[] pdf = generator.generate(dto);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=report.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}