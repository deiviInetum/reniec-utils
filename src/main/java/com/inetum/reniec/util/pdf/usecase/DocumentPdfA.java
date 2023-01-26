package com.inetum.reniec.util.pdf.usecase;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.inetum.reniec.util.common.exceptions.PdfUtilsException;

public interface DocumentPdfA {
    void addImgToPdfADocument(PDDocument doc, byte[] imageData)throws IOException;
    void addSettingsToDocumentPdfA(PDDocument document) throws PdfUtilsException;
}
