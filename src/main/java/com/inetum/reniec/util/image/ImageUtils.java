package com.inetum.reniec.util.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.StreamSupport;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.inetum.reniec.util.common.exceptions.ImageUtilsException;

public final class ImageUtils {
    ImageUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static SerialBlob toBlob(String filename, byte[] file) throws ImageUtilsException, SQLException {
        int dpi = 0;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file);
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            if (StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                    .anyMatch(x -> x.getTags().stream()
                            .anyMatch(t -> t.getTagName().contains("Resolution") && t.getDescription().contains("200")))
                    || StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                            .anyMatch(x -> x.getTags().stream().anyMatch(t -> t.getTagName().equals("Resolution Info")
                                    && t.getDescription().contains("200")))) {
                dpi = 200;
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }

        if (dpi != 200) {
            throw new ImageUtilsException(String
                    .format("Resolución DPI (%d DPI) de la imagen %s está fuera de lo permitido.", dpi, filename));
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(file);
            BufferedImage image = ImageIO.read(bais);

            if (image.getType() != BufferedImage.TYPE_BYTE_GRAY && image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
                switch (image.getType()) {
                    case BufferedImage.TYPE_3BYTE_BGR:
                    case BufferedImage.TYPE_4BYTE_ABGR:
                        throw new ImageUtilsException(String.format("La imagen %s está a color.", filename));
                    default:
                        throw new ImageUtilsException(
                                String.format("El tipo de la imagen %s no se puede determinar.", filename));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Boolean.FALSE.equals(isSizeOk(file))) {
            throw new ImageUtilsException(String
                    .format("El tamaño (%f Kb) de la imagen %s está fuera del rango permitido.", file.length / 1024f, filename));
        }
        return new SerialBlob(file);
    }

    public static Boolean isDPIOk(byte[] file) {
        int dpi = 0;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file);
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            if (StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                    .anyMatch(x -> x.getTags().stream()
                            .anyMatch(t -> t.getTagName().equals("X Resolution") && t.getDescription().contains("200")
                                    && t.getDescription().contains("dots")))
                    || StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                            .anyMatch(x -> x.getTags().stream().anyMatch(t -> t.getTagName().equals("Resolution Info")
                                    && t.getDescription().contains("200") && t.getDescription().contains("DPI")))) {
                dpi = 200;
            }
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }

        return dpi == 200;
    }

    public static Boolean isGreyScaleOk(byte[] file) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(file);
            BufferedImage image = ImageIO.read(bais);

            return image.getType() == BufferedImage.TYPE_BYTE_GRAY || image.getType() == BufferedImage.TYPE_BYTE_BINARY;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean isSizeOk(byte[] file) {
        double kilobytes = file.length / 1024f;
        return kilobytes >= 50d && kilobytes <= 900d;
    }
}
