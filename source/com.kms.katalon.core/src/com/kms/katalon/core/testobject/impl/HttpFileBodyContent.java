package com.kms.katalon.core.testobject.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import com.kms.katalon.core.testobject.HttpBodyContent;

public class HttpFileBodyContent implements HttpBodyContent {
    private static final int BUFFER_SIZE = 1024;

    private File file;

    public HttpFileBodyContent(String filePath) throws IllegalArgumentException, FileNotFoundException {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath cannot be null");
        }
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(MessageFormat.format("File {0} not found", filePath));
        }
        if (!file.isFile()) {
            throw new FileNotFoundException(MessageFormat.format("File {0} is not a file", filePath));
        }
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public long getContentLength() {
        return -1L;
    }

    @Override
    public String getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException, UnsupportedOperationException {
        return new FileInputStream(file.getAbsolutePath());
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        try (InputStream inputStream = getInputStream()) {

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                outstream.write(data, 0, nRead);
            }
            outstream.flush();
        }
    }
}
