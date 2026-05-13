package com.example.reports.generator;

import com.example.reports.dto.UserActivityReportDto;
import com.example.reports.service.PdfDocumentService;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;

public class UserActivityPdfGenerator
        implements PdfGenerator<UserActivityReportDto> {

    private final PdfDocumentService pdfService;

    public UserActivityPdfGenerator(
            PdfDocumentService pdfService) {

        this.pdfService = pdfService;
    }

    @Override
    public byte[] generate(
            UserActivityReportDto data) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document =
                    pdfService.createDocument(outputStream);

            document.add(
                    new Paragraph(
                            "Raport aktywności użytkownika"
                    )
            );

            document.add(
                    new Paragraph(
                            "Użytkownik: "
                                    + data.getUsername()
                    )
            );

            for (String activity : data.getActivities()) {

                document.add(
                        new Paragraph("- " + activity)
                );
            }

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
