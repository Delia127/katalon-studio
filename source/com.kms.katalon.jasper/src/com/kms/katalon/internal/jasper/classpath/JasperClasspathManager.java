package com.kms.katalon.internal.jasper.classpath;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JarUtil;

public class JasperClasspathManager {
    private static JasperClasspathManager _instance;
    
    public static final String CLASSPATH_PROPERTY = "java.class.path";
    public static final String REPORT_TEMP_DIR_PROPERTY = "user.dir";

    private Map<String, String> jasperCompilerProperties;
    private boolean resolveClasspath;

    private JasperClasspathManager() {
        jasperCompilerProperties = new HashMap<String, String>();
        resolveClasspath = false;
    }
    
    public static JasperClasspathManager getInstance() {
        if (_instance == null) {
            _instance = new JasperClasspathManager();
        }
        return _instance;
    }
    

    public synchronized void restoreSystemProperties() {
        for (Entry<String, String> compilerProperty : jasperCompilerProperties.entrySet()) {
            System.setProperty(compilerProperty.getKey(), compilerProperty.getValue());
        }
    }

    public synchronized void modifySystemProperties() {
        if (!resolveClasspath) {
            modifyClasspathProperty();
            modifyTempDirProperty();
        }
        resolveClasspath = true;
    }

    private void modifyTempDirProperty() {
        jasperCompilerProperties.put(REPORT_TEMP_DIR_PROPERTY, System.getProperty(REPORT_TEMP_DIR_PROPERTY));

        String tempFolderLoc = ProjectController.getInstance().getTempDir();
        System.setProperty(REPORT_TEMP_DIR_PROPERTY, tempFolderLoc + "/generated/pdf");
    }
    
    private File getTempClasspathDir() {
        String nonRemovableSystemDir = ProjectController.getInstance().getNonremovableTempDir();
        
        return new File(nonRemovableSystemDir, "com.kms.katalon.jasper");
    }

    private void modifyClasspathProperty() {
        jasperCompilerProperties.put(CLASSPATH_PROPERTY, System.getProperty(CLASSPATH_PROPERTY));
        try {
            StringBuilder classPathBuilder = new StringBuilder();
            
            File tempClasspathDir = getTempClasspathDir();
            if (tempClasspathDir.exists()) {
                FileUtils.cleanDirectory(tempClasspathDir);
            }
            tempClasspathDir.mkdirs();

            for (File libFile : JarUtil.getFiles(getClass(), "resources/lib", tempClasspathDir)) {
                classPathBuilder.append(libFile.getAbsolutePath()).append(";");
            }

            System.setProperty(CLASSPATH_PROPERTY, classPathBuilder.toString());
        } catch (IOException | URISyntaxException ex) {
            restoreSystemProperties();
        }
    }
}
