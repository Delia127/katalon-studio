package com.kms.katalon.integration.analytics.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.controller.ProjectController;

public class FileUtils {

    public static Path createPath(String path) {
        FileSystem fileSystem = FileSystems.getDefault();
        return fileSystem.getPath(path);
    }

    public static List<Path> scanFiles(String path, String extension) {
        List<Path> filePaths = new LinkedList<>();
        Path rootPath = createPath(path);
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes attrs) throws IOException {
                    String fileName = visitedFile.toFile().getName();
                    boolean matched = Pattern.matches(extension, fileName);
                    if (matched) {
                        filePaths.add(visitedFile);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            // do nothing
        }
        return filePaths;
    }

    public static List<Path> scanFiles(String path, String[] extensions) {
        File targetDir = new File(path);
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            return Collections.emptyList();
        }
        File[] childrenFiles = targetDir.listFiles((dir, name) -> Arrays.asList(extensions).contains(
                com.google.common.io.Files.getFileExtension(name)));
        if (childrenFiles == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(childrenFiles).sorted(Comparator.comparing(File::getName)).map(File::toPath).collect(
                        Collectors.toList());
    }

    public static String readFileToString(Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            return StringUtils.EMPTY;
        }
    }

    public static List<String> readLines(Path file) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        } catch (IOException e) {
            // do nothing
        }
        return lines;
    }

    public static StringBuilder readFileContent(Path file) {
        StringBuilder xmlContentBuilder = new StringBuilder();
        List<String> lines = readLines(file);
        lines.stream().forEach(l -> {
            xmlContentBuilder.append(l);
        });
        return xmlContentBuilder;
    }
    
    public static Path createTemporaryFile(String prefix, String suffix) throws IOException {
        return createTemporaryFile(ProjectController.getInstance().getTempDir(), prefix, suffix);
    }
    
    public static Path createTemporaryFile(String tempFolderPath, String prefix, String suffix) throws IOException {
        Path tempFolder = Paths.get(tempFolderPath);
        if (Files.notExists(tempFolder)) {
            Files.createDirectories(tempFolder);
        }
        Path file = Files.createTempFile(tempFolder, prefix, suffix);
        Files.deleteIfExists(file);
        return file;
    }

}
