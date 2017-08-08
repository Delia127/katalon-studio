package com.kms.katalon.internal.jasper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

public class ResourceUtil {
    public static InputStream getResourceAsInputStream(Class<?> clazz, String filePath) throws IOException {
        String projectPath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        projectPath = URLDecoder.decode(projectPath, "utf-8");

        File jarFile = new File(projectPath);
        if (jarFile.isDirectory()) { // built by IDE
            return new FileInputStream(new File(jarFile, filePath).getAbsolutePath());
        } else {
            return clazz.getClassLoader().getResourceAsStream(filePath);
        }
    }
}