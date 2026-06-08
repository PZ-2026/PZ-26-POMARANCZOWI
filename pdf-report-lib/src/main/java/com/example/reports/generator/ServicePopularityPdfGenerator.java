package com.example.reports.generator;

import com.example.reports.dto.PopularServiceDto;
import com.example.reports.dto.ServicePopularityReportDto;
import com.example.reports.service.PdfDocumentService;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import org.jfree.data.category.DefaultCategoryDataset;

public class ServicePopularityPdfGenerator {

    private final PdfDocumentService pdfDocumentService;

    public ServicePopularityPdfGenerator(
            PdfDocumentService pdfDocumentService
    ) {
        this.pdfDocumentService = pdfDocumentService;
    }

    public byte[] generate(
            ServicePopularityReportDto dto
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
                            ServicePopularityPdfGenerator.class
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

        document.add(
                new Paragraph("RAPORT ANALITYCZNY")
                        .setFontSize(8)
                        .setFontColor(
                                ColorConstants.LIGHT_GRAY
                        )
                        .setTextAlignment(
                                TextAlignment.RIGHT
                        )
        );

        document.add(
                new Paragraph(
                        "RAPORT POPULARNOŚCI USŁUG - OSTATNI MIESIĄC"
                )
                        .setBold()
                        .setFontSize(28)
                        .setFontColor(
                                new DeviceRgb(
                                        44,
                                        62,
                                        80
                                )
                        )
                        .setTextAlignment(
                                TextAlignment.CENTER
                        )
                        .setMarginTop(20)
                        .setMarginBottom(30)
        );

        document.add(
                new Paragraph(
                        "Data generowania: "
                                + LocalDateTime.now()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "dd.MM.yyyy HH:mm"
                                        )
                                )
                )
        );

        document.add(
                new Paragraph("")
        );

        Table metrics =
                new Table(
                        UnitValue.createPercentArray(
                                new float[]{1, 1, 1}
                        )
                );

        metrics.setWidth(
                UnitValue.createPercentValue(100)
        );

        metrics.addCell(
                createMetricCell(
                        "Wszystkie usługi",
                        dto.getTotalServices().toString()
                )
        );

        metrics.addCell(
                createMetricCell(
                        "Różne usługi",
                        dto.getUniqueServices().toString()
                )
        );

        metrics.addCell(
                createMetricCell(
                        "Najpopularniejsza",
                        dto.getMostPopularService()
                )
        );

        document.add(metrics);

        document.add(
                new Paragraph("\nTOP 3 USŁUGI")
                        .setBold()
                        .setFontSize(16)
        );

        for (int i = 0;
             i < Math.min(
                     3,
                     dto.getServices().size()
             );
             i++) {

            PopularServiceDto service =
                    dto.getServices().get(i);

            document.add(
                    new Paragraph(
                            (i + 1)
                                    + ". "
                                    + service.getServiceName()
                                    + " ("
                                    + service.getCount()
                                    + " wykonań)"
                    )
            );
        }

        document.add(
                new Paragraph("\nPOPULARNOŚĆ USŁUG")
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
                        new Paragraph(
                                "Liczba wykonań"
                        ).setBold()
                )
        );

        for (PopularServiceDto service
                : dto.getServices()) {

            table.addCell(
                    service.getServiceName()
            );

            table.addCell(
                    String.valueOf(
                            service.getCount()
                    )
            );
        }

        document.add(table);



document.add(
        new Paragraph("\nWYKRES POPULARNOŚCI")
                .setBold()
                .setFontSize(16)
);

Image chart =
        createPopularityChart(dto);

chart.setAutoScale(true);

document.add(chart);

        document.add(
                new Paragraph(
                        "\nDokument wygenerowany automatycznie • "
                                + LocalDateTime.now()
                                .format(
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
        );

        document.close();

        return outputStream.toByteArray();
    }

    private Cell createMetricCell(
            String title,
            String value
    ) {

        Cell cell =
                new Cell()
                        .setBackgroundColor(
                                new DeviceRgb(
                                        240,
                                        248,
                                        255
                                )
                        )
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(
                                TextAlignment.CENTER
                        )
                        .setPadding(15);

        cell.add(
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

        cell.add(
                new Paragraph(value)
                        .setBold()
                        .setFontSize(26)
        );

        return cell;
    }

    private Image createPopularityChart(
        ServicePopularityReportDto dto
) {

    try {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (PopularServiceDto service
                : dto.getServices()) {

            dataset.addValue(
                    service.getCount(),
                    "Wykonania",
                    service.getServiceName()
            );
        }

        JFreeChart chart =
                ChartFactory.createBarChart(
                        "Popularność usług",
                        "Usługa",
                        "Liczba wykonań",
                        dataset
                );

        ByteArrayOutputStream chartOutput =
                new ByteArrayOutputStream();

        ChartUtils.writeChartAsPNG(
                chartOutput,
                chart,
                700,
                400
        );

        return new Image(
                ImageDataFactory.create(
                        chartOutput.toByteArray()
                )
        );

    } catch (Exception e) {

        throw new RuntimeException(e);
    }
}
}