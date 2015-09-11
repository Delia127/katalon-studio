package com.kms.katalon.integration.qtest.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.qas.api.internal.util.google.io.BaseEncoding;

import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.exception.QTestIOException;

/**
 * Provides a set of utility methods for zipping, encoding file
 *
 */
public class ZipUtil {
    public static ZipFile getZipFile(File zipFile, File folderToZip) throws QTestIOException {
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try {
            ZipFile returnedZipFile = new ZipFile(zipFile);

            ZipParameters parameters = new ZipParameters();

            // set compression method to store compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // Add folder to the zip file

            returnedZipFile.addFolder(folderToZip.getAbsolutePath(), parameters);

            return returnedZipFile;
        } catch (ZipException e) {
            throw new QTestIOException("Cannot zip folder: " + folderToZip.getAbsolutePath());
        }
    }

    public static String encodeFileContent(String filePath) throws IOException, QTestIOException {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
            return BaseEncoding.base64().encode(getBinaryFromInputStream(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static byte[] getBinaryFromInputStream(InputStream content) throws QTestIOException {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[8096];
            int length;
            while ((length = content.read(buffer)) > -1) {
                output.write(buffer, 0, length);
            }
            output.close();
            return output.toByteArray();
        } catch (IOException ex) {
            throw new QTestIOException(ex, MessageFormat.format(
                    QTestMessageConstants.QTEST_EXC_CANNOT_READ_INPUT_STREAM, ex.getMessage()));
        }
    }

}
