package com.kms.katalon.application.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileUtil {
    private static final String ANY_EXTENSION = "*";

    public static File[] getFiles(File folder, String suffix, Date origin) throws IOException {
        final List<File> filePaths = new ArrayList<>();
        enumFiles(folder, suffix, origin, new FileProcessor() {

            @Override
            public void process(File f) {
                filePaths.add(f);
            }
        });
        return filePaths.toArray(new File[] {});
    }

    private static void enumFiles(File folder, String suffix, Date origin, FileProcessor fileProcessor) throws IOException {
        if (folder.isDirectory() == false) {
            return;
        }
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(folder.listFiles()));
        for (int i = 0; i < files.size(); ++i) {
            File file = files.get(i);
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
                continue;
            }
            if (isFileSatisfy(file, suffix, origin)) {
                fileProcessor.process(file);
            }
        }
    }

    private static boolean isFileSatisfy(File file, String suffix, Date origin) throws IOException {
        String fileName = file.getName();
        boolean satisfyName = suffix.equals(ANY_EXTENSION) ? true : fileName.endsWith(suffix);
        if (!satisfyName) {
            return false;
        }

        return isFileCreateAfter(file, origin);
    }
    
    public static boolean isFileCreateAfter(File file, Date time) throws IOException {
        return time.getTime() < Files.readAttributes(file.toPath(), BasicFileAttributes.class)
                .creationTime()
                .toMillis();
    }

    public static int countAllFiles(File folder, String suffix, Date origin) throws IOException {
        final int[] count = { 0 };
        enumFiles(folder, suffix, origin, new FileProcessor() {

            @Override
            public void process(File f) {
                count[0]++;
            }
        });

        return count[0];
    }

    private static interface FileProcessor {
        public void process(File f);
    }
}
