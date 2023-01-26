package com.inetum.reniec.util.pdf;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.inetum.reniec.util.common.exceptions.PdfUtilsException;
import com.inetum.reniec.util.pdf.usecase.DocumentPdfA;
import com.inetum.reniec.util.pdf.usecase.imp.DocumentPdfAImpl;

public final class PdfUtils {
    PdfUtils() {
        throw new IllegalStateException("PdfUtils class");
    }

    public static byte[] convertImageToPdfA(List<byte[]> lstImages) throws PdfUtilsException {
        DocumentPdfA documentPdfA = new DocumentPdfAImpl();
        if (lstImages.isEmpty())
            return new byte[0];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            lstImages.forEach(fImagen -> {
                try {
                    documentPdfA.addImgToPdfADocument(document, fImagen);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error al convertir imagen a PdfA ");
                }
            });
            documentPdfA.addSettingsToDocumentPdfA(document);
            document.save(bos);
        } catch (Exception e) {
            throw new PdfUtilsException("No se pudo convertir la imagen a documento PdfA");
        }
        return bos.toByteArray();
    }

}
