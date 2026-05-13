package com.example.reports.generator;

public interface PdfGenerator<T> {

    byte[] generate(T data);
}
