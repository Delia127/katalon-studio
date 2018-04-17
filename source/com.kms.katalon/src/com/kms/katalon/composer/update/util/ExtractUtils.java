package com.kms.katalon.composer.update.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

public class ExtractUtils {
    public static final String TAR_EXT = ".tar.gz";

    public static void extract(final File inputFile, final File outputDir) throws IOException {
        try (TarArchiveInputStream tarArchiveInputStream =
                new TarArchiveInputStream(
                        new GzipCompressorInputStream(
                                (new BufferedInputStream(
                                        new FileInputStream(inputFile)))))) {


            TarArchiveEntry entry;
            while ((entry = tarArchiveInputStream.getNextTarEntry()) != null) {
                File outputFile = new File(outputDir, entry.getName());
                outputFile.getParentFile().mkdirs();
                if (!entry.isFile()) {
                    continue;
                }
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    IOUtils.copy(tarArchiveInputStream, fos);
                }
            }
        }
    }
}
