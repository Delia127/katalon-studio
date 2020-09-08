package com.kms.katalon.core.util.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;

public class TestOpsUtil {

    public static final String TESTOPS_VISION_FILE_NAME_PREFIX = "keyes-";

    public static final String TESTOPS_VISION_REPORT_FOLDER = "keyes";

    public static final String DEFAULT_IMAGE_EXTENSION = "png";

    public static String replaceTestOpsVisionFileName(String originalFilePath) {
        if (originalFilePath == null) {
            return null;
        }

        String fullPath = addDefaultImageExtension(originalFilePath);
        int nameIndex = originalFilePath.lastIndexOf(File.separatorChar);
        if (nameIndex < 0) {
            return Paths.get(RunConfiguration.getReportFolder(), TESTOPS_VISION_REPORT_FOLDER,
                    (TESTOPS_VISION_FILE_NAME_PREFIX + fullPath)).toString();
        }

        String path = fullPath.substring(0, nameIndex);
        String fileName = fullPath.substring(nameIndex + 1);
        Path p = Paths.get(path, TESTOPS_VISION_REPORT_FOLDER, (TESTOPS_VISION_FILE_NAME_PREFIX + fileName));
        if (!p.isAbsolute()) {
            p = Paths.get(RunConfiguration.getReportFolder(), p.toString());
        }

        return p.toString();
    }
    
    
    /**
     * Create path for file if not existed. If <b>isFile</b> create path to parent directory of the file.
     * Or else create directory for the path.
     * @param file File whose parent need to be created.
     * @param isFile <b>true</b> if the path in <b><i>file</i></b> file. <b>false</b> if path is directory.
     * @return File whose path has been created.
     * @throws IOException 
     */
    public static File ensureDirectory(File file, boolean isFile) throws IOException, SecurityException {
        if (file == null) {
            throw new IOException(StringConstants.UTIL_EXC_FILE_NOT_NULL);
        }

        if (file.exists()) {
            return file;
        }
        
        if (!isValidFile(file)) {
            throw new IOException(
                    MessageFormat.format(StringConstants.UTIL_EXC_FILE_PATH_INVALID, file.getAbsolutePath()));
        }
        
        if (isFile) {
            File parent = file.getParentFile();
            if (parent == null || parent.exists()) {
                return file;
            }
            parent.mkdirs();
        } else {
            file.mkdirs();
        }

        return file;
    }

    private static String addDefaultImageExtension(String fileName) {
        if (!FilenameUtils.isExtension(fileName, DEFAULT_IMAGE_EXTENSION)) {
            return fileName + "." + DEFAULT_IMAGE_EXTENSION;
        }
        return fileName;
    }
    
    
    private static boolean isValidFile(File file) {
        try {
            Paths.get(file.getAbsolutePath());
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }
}
