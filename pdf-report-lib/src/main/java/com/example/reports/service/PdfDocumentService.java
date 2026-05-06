package com.example.reports.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.ByteArrayOutputStream;

public class PdfDocumentService {

    public Document createDocument(
            ByteArrayOutputStream outputStream) {

        PdfWriter writer =
                new PdfWriter(outputStream);

        PdfDocument pdfDocument =
                new PdfDocument(writer);

        return new Document(pdfDocument);
    }
}
