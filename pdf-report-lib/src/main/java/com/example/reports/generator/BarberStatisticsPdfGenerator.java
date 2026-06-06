package com.example.reports.generator;

import com.example.reports.dto.BarberStatisticsReportDto;
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

import com.itextpdf.layout.element.Text;

public class BarberStatisticsPdfGenerator {

    private final PdfDocumentService pdfDocumentService;

    public BarberStatisticsPdfGenerator(
            PdfDocumentService pdfDocumentService
    ) {
        this.pdfDocumentService =
                pdfDocumentService;
    }

    public byte[] generate(
            BarberStatisticsReportDto dto
    ) {

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document =
                pdfDocumentService.createDocument(
                        outputStream
                );

        try {

            PdfFont font =
                    PdfFontFactory.createFont(
                            BarberStatisticsPdfGenerator.class
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

        addBarberInfo(document, dto);

        addSeparator(document);

        addKeyMetrics(document, dto);

        addSeparator(document);

        addSummary(document, dto);

        addFooter(document);

        document.close();

        return outputStream.toByteArray();
    }

    private void addHeader(Document document) {

        Paragraph headerText =
                new Paragraph("RAPORT BARBERA")
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
                new Paragraph(
                        "RAPORT STATYSTYK BARBERA"
                )
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

    private void addBarberInfo(
            Document document,
            BarberStatisticsReportDto dto
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
                createLabelCell("Barber:")
        );

        infoTable.addCell(
                createValueCell(
                        dto.getBarberName()
                )
        );

        String generationDate =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd.MM.yyyy HH:mm"
                                )
                        );

        infoTable.addCell(
                createLabelCell(
                        "Data generowania:"
                )
        );

        infoTable.addCell(
                createValueCell(
                        generationDate
                )
        );

        document.add(infoTable);
    }

    private void addSeparator(
            Document document
    ) {

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
            BarberStatisticsReportDto dto
    ) {

        Table metricsTable =
                new Table(
                        UnitValue.createPercentArray(
                                new float[]{1, 1, 1}
                        )
                );

        metricsTable.setWidth(
                UnitValue.createPercentValue(100)
        );

        metricsTable.setMarginTop(10);

        metricsTable.setMarginBottom(20);

        Cell visitsCard =
                createMetricCard(
                        "LICZBA WIZYT",
                        String.valueOf(
                                dto.getAppointmentsCount()
                        ),
                        new DeviceRgb(
                                52,
                                73,
                                94
                        )
                );

        Cell revenueCard =
                createMetricCard(
                        "ŁĄCZNY PRZYCHÓD",
                        dto.getTotalRevenue()
                                + " PLN",
                        new DeviceRgb(
                                46,
                                204,
                                113
                        )
                );

        Cell averageCard =
                createMetricCard(
                        "ŚREDNIA NA WIZYTĘ",
                        dto.getAverageRevenuePerVisit()
                                + " PLN",
                        new DeviceRgb(
                                52,
                                152,
                                219
                        )
                );

        metricsTable.addCell(visitsCard);
        metricsTable.addCell(revenueCard);
        metricsTable.addCell(averageCard);

        document.add(metricsTable);
    }

    private Cell createMetricCard(
            String title,
            String value,
            DeviceRgb color
    ) {

        Cell card =
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

        card.add(
                new Paragraph(title)
                        .setFontSize(10)
                        .setFontColor(
                                new DeviceRgb(
                                        100,
                                        100,
                                        100
                                )
                        )
        );

        card.add(
                new Paragraph(value)
                        .setBold()
                        .setFontSize(24)
                        .setFontColor(color)
        );

        return card;
    }

   private void addSummary(
        Document document,
        BarberStatisticsReportDto dto
) {

    Paragraph summary =
            new Paragraph()
                    .setFontSize(11)
                    .setMarginTop(20)
                    .setTextAlignment(
                            TextAlignment.JUSTIFIED
                    );

    summary.add(
            new Text("Barber ")
    );

    summary.add(
            new Text(
                    dto.getBarberName()
            ).setBold()
    );

    summary.add(
            new Text(
                    " wykonał "
                            + dto.getAppointmentsCount()
                            + " wizyt/wizytę i wygenerował przychód "
            )
    );

    summary.add(
            new Text(
                    dto.getTotalRevenue()
                            + " PLN"
            ).setBold()
    );

    summary.add(
            new Text(
                    ". Średni przychód na wizytę wyniósł "
            )
    );

    summary.add(
            new Text(
                    dto.getAverageRevenuePerVisit()
                            + " PLN"
            ).setBold()
    );

    summary.add(
            new Text(".")
    );

    document.add(summary);
}
    

    private void addFooter(
            Document document
    ) {

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

    private Cell createLabelCell(
            String text
    ) {

        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(
                        new Paragraph(text)
                                .setBold()
                                .setFontSize(10)
                );
    }

    private Cell createValueCell(
            String text
    ) {

        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(
                        new Paragraph(text)
                                .setFontSize(10)
                );
    }
}