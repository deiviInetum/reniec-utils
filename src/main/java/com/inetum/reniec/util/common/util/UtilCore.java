package com.inetum.reniec.util.common.util;

import java.io.InputStream;

public class UtilCore {

    public InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("No se encontro el archivo " + fileName);
        } else {
            return inputStream;
        }
    }
}
