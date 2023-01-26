package com.inetum.reniec.util.pdf.usecase.imp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.pdfa.XMPSchemaPDFAId;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.inetum.reniec.util.common.constants.Constantes;
import com.inetum.reniec.util.common.exceptions.PdfUtilsException;
import com.inetum.reniec.util.common.util.UtilCore;
import com.inetum.reniec.util.pdf.usecase.DocumentPdfA;

public class DocumentPdfAImpl implements DocumentPdfA {
    private static final float POINTS_PER_MM = 1 / (10 * 2.54f) * 72;

    @Override
    public void addImgToPdfADocument(PDDocument doc, byte[] imageData)
            throws IOException {
        try (InputStream in = new ByteArrayInputStream(imageData)) {
            PDImageXObject img = JPEGFactory.createFromStream(doc, in);
            float w = (img.getWidth() * 25.4f / 200) * POINTS_PER_MM;
            float h = (img.getHeight() * 25.4f / 200) * POINTS_PER_MM;
            PDPage page = new PDPage(new PDRectangle(w, h));
            doc.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.drawImage(img, 0, 0, w, h);
            }
        }
    }

    @Override
    public void addSettingsToDocumentPdfA(PDDocument document) throws PdfUtilsException {
        try {
            PDDocumentCatalog cat = document.getDocumentCatalog();
            PDMetadata metadata = new PDMetadata(document);
            cat.setMetadata(metadata);

            XMPMetadata xmp = new XMPMetadata();
            XMPSchemaPDFAId pdfaid = new XMPSchemaPDFAId(xmp);
            xmp.addSchema(pdfaid);
            pdfaid.setConformance("B");
            pdfaid.setPart(2);
            pdfaid.setAbout("");
            metadata.importXMPMetadata(xmp.asByteArray());

            InputStream colorProfile = new UtilCore()
                    .getFileFromResourceAsStream(Constantes.DocumentoPdfA.FONT_RESOURCE);
            PDOutputIntent oi = new PDOutputIntent(document, colorProfile);
            oi.setInfo(Constantes.DocumentoPdfA.FONT_NAME);
            oi.setOutputCondition(Constantes.DocumentoPdfA.FONT_NAME);
            oi.setOutputConditionIdentifier(Constantes.DocumentoPdfA.FONT_NAME);
            oi.setRegistryName("http://www.color.org");
            cat.addOutputIntent(oi);
        } catch (Exception e) {
            throw new PdfUtilsException("Error de metadata cocumento Pdf/A.");
        }
    }
}
