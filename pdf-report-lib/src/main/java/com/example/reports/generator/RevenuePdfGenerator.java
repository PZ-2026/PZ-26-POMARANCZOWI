package com.example.reports.generator;

import com.example.reports.dto.RevenueReportDto;
import com.example.reports.service.PdfDocumentService;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import com.itextpdf.layout.Document;

import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.reports.dto.ServiceRevenueDto;



public class RevenuePdfGenerator {

    private final PdfDocumentService pdfDocumentService;

    public RevenuePdfGenerator(
            PdfDocumentService pdfDocumentService
    ) {
        this.pdfDocumentService =
                pdfDocumentService;
    }

    public byte[] generate(RevenueReportDto dto) {

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document =
                pdfDocumentService.createDocument(
                        outputStream
                );

        try {

            PdfFont font =
                    PdfFontFactory.createFont(
                            RevenuePdfGenerator.class
                                    .getClassLoader()
                                    .getResource(
                                            "fonts/NotoSans-Regular.ttf"
                                    )
                                    .toExternalForm(),
                            "Identity-H"
                    );

            document.setFont(font);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        document.setMargins(50, 50, 50, 50);

        addHeader(document);

        addTitle(document);

        addReportInfo(document, dto);

        addSeparator(document);

        addKeyMetrics(document, dto);

        addSeparator(document);

        addSummary(document, dto);

        addRevenueByService(document, dto);

        addFooter(document);

        document.close();

        return outputStream.toByteArray();
    }

    private void addHeader(Document document) {

        Paragraph headerText =
                new Paragraph("RAPORT FINANSOWY")
                        .setFontSize(8)
                        .setFontColor(
                                ColorConstants.LIGHT_GRAY
                        )
                        .setTextAlignment(
                                TextAlignment.RIGHT
                        );

        document.add(headerText);
    }

    private void addTitle(Document document) {

        Paragraph title =
                new Paragraph("RAPORT PRZYCHODÓW")
                        .setBold()
                        .setFontSize(28)
                        .setFontColor(
                                new DeviceRgb(44, 62, 80)
                        )
                        .setTextAlignment(
                                TextAlignment.CENTER
                        )
                        .setMarginTop(20)
                        .setMarginBottom(30);

        document.add(title);
    }

    private void addReportInfo(
            Document document,
            RevenueReportDto dto
    ) {

        Table infoTable =
                new Table(
                        UnitValue.createPercentArray(
                                new float[]{1, 2}
                        )
                );

        infoTable.setWidth(
                UnitValue.createPercentValue(60)
        );

        infoTable.setMarginBottom(20);

        infoTable.addCell(
                createLabelCell("Okres raportu:")
        );

        infoTable.addCell(
                createValueCell(dto.getPeriod())
        );

        String generationDate =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd.MM.yyyy HH:mm"
                                )
                        );

        infoTable.addCell(
                createLabelCell("Data generowania:")
        );

        infoTable.addCell(
                createValueCell(generationDate)
        );

        document.add(infoTable);
    }

    private void addSeparator(Document document) {

        Table separator =
                new Table(
                        UnitValue.createPercentArray(
                                new float[]{1}
                        )
                );

        separator.setWidth(
                UnitValue.createPercentValue(100)
        );

        separator.setMarginTop(15);

        separator.setMarginBottom(15);

        Cell lineCell =
                new Cell()
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(
                                new SolidBorder(
                                        ColorConstants.LIGHT_GRAY,
                                        1
                                )
                        )
                        .setPadding(0);

        separator.addCell(lineCell);

        document.add(separator);
    }

    private void addKeyMetrics(
            Document document,
            RevenueReportDto dto
    ) {

        Table metricsTable =
                new Table(
                        UnitValue.createPercentArray(
                                new float[]{1, 1}
                        )
                );

        metricsTable.setWidth(
                UnitValue.createPercentValue(80)
        );

        metricsTable.setMarginTop(10);

        metricsTable.setMarginBottom(20);

        Cell visitsCard =
                new Cell()
                        .setBackgroundColor(
                                new DeviceRgb(
                                        240,
                                        248,
                                        255
                                )
                        )
                        .setPadding(15)
                        .setTextAlignment(
                                TextAlignment.CENTER
                        );

        visitsCard.add(
                new Paragraph("LICZBA WIZYT")
                        .setFontSize(10)
                        .setFontColor(
                                new DeviceRgb(
                                        100,
                                        100,
                                        100
                                )
                        )
        );

        visitsCard.add(
                new Paragraph(
                        String.valueOf(
                                dto.getAppointmentsCount()
                        )
                )
                        .setBold()
                        .setFontSize(32)
                        .setFontColor(
                                new DeviceRgb(
                                        52,
                                        73,
                                        94
                                )
                        )
        );

        metricsTable.addCell(visitsCard);

        Cell revenueCard =
                new Cell()
                        .setBackgroundColor(
                                new DeviceRgb(
                                        240,
                                        248,
                                        255
                                )
                        )
                        .setPadding(15)
                        .setTextAlignment(
                                TextAlignment.CENTER
                        );

        revenueCard.add(
                new Paragraph("ŁĄCZNY PRZYCHÓD")
                        .setFontSize(10)
                        .setFontColor(
                                new DeviceRgb(
                                        100,
                                        100,
                                        100
                                )
                        )
        );

        revenueCard.add(
                new Paragraph(
                        dto.getTotalRevenue()
                                + " PLN"
                )
                        .setBold()
                        .setFontSize(32)
                        .setFontColor(
                                new DeviceRgb(
                                        46,
                                        204,
                                        113
                                )
                        )
        );

        metricsTable.addCell(revenueCard);

        document.add(metricsTable);
    }

    private void addSummary(
            Document document,
            RevenueReportDto dto
    ) {

        String summaryText =
                "W okresie "
                        + dto.getPeriod()
                        + " odnotowano "
                        + dto.getAppointmentsCount()
                        + " wizyt/ę, które wygenerowały przychód "
                        + dto.getTotalRevenue()
                        + " PLN.";

        Paragraph summary =
        new Paragraph()
                .setFontSize(11)
                .setMarginTop(20)
                .setTextAlignment(
                        TextAlignment.JUSTIFIED
                );

summary.add("W okresie ");

summary.add(
        new com.itextpdf.layout.element.Text(
                dto.getPeriod()
        ).setBold()
);

summary.add(
        " odnotowano "
                + dto.getAppointmentsCount()
                + " wizyt/ę, które wygenerowały przychód "
);

summary.add(
        new com.itextpdf.layout.element.Text(
                dto.getTotalRevenue()
                        + " PLN"
        ).setBold()
);

summary.add(".");

document.add(summary);
    }

    private void addFooter(Document document) {

        Paragraph footer =
                new Paragraph(
                        "Dokument wygenerowany automatycznie • "
                                + LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern(
                                        "dd.MM.yyyy"
                                )
                        )
                )
                        .setFontSize(8)
                        .setFontColor(
                                ColorConstants.LIGHT_GRAY
                        )
                        .setTextAlignment(
                                TextAlignment.CENTER
                        )
                        .setMarginTop(40);

        document.add(footer);
    }

    private Cell createLabelCell(String text) {

        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(
                        new Paragraph(text)
                                .setBold()
                                .setFontSize(10)
                );
    }

    private Cell createValueCell(String text) {

        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(
                        new Paragraph(text)
                                .setFontSize(10)
                );
    }

    private void addRevenueByService(
        Document document,
        RevenueReportDto dto
) {

    document.add(
            new Paragraph(
                    "\nPRZYCHÓD WG USŁUG"
            )
                    .setBold()
                    .setFontSize(16)
    );

    Table table =
            new Table(
                    UnitValue.createPercentArray(
                            new float[]{3, 1}
                    )
            );

    table.setWidth(
            UnitValue.createPercentValue(100)
    );

    table.addHeaderCell(
            new Cell().add(
                    new Paragraph("Usługa")
                            .setBold()
            )
    );

    table.addHeaderCell(
            new Cell().add(
                    new Paragraph("Przychód")
                            .setBold()
            )
    );

    for (ServiceRevenueDto service
            : dto.getServicesRevenue()) {

        table.addCell(
                service.getServiceName()
        );

        table.addCell(
                service.getRevenue()
                        + " PLN"
        );
    }

    document.add(table);
}
}