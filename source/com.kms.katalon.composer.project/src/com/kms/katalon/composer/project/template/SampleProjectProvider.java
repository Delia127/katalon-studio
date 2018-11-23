package com.kms.katalon.composer.project.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.sample.SampleLocalProject;
import com.kms.katalon.composer.project.sample.SampleProjectType;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class SampleProjectProvider {
    private static final String RESOURCES_SAMPLE_PROJECTS_FOLDER = "resources" + File.separator + "samples";

    public static final String SAMPLE_WEB_UI = StringConstants.SAMPLE_WEB_UI_PROJECT;

    public static final String SAMPLE_MOBILE = StringConstants.SAMPLE_MOBILE_PROJECT;

    public static final String SAMPLE_WEB_SERVICE = StringConstants.SAMPLE_WEB_SERVICE_PROJECT;
    
    private static final List<SampleLocalProject> SAMPLE_PROJECTS = Arrays.asList(
        createSampleProjectInfo(SampleProjectType.WEBUI, SAMPLE_WEB_UI, "WebUI"),
        createSampleProjectInfo(SampleProjectType.MOBILE, SAMPLE_MOBILE, "Mobile"),
        createSampleProjectInfo(SampleProjectType.WS, SAMPLE_WEB_SERVICE, "WebService")
    );

    private static SampleProjectProvider instance;

    private SampleProjectProvider() {
        // Disable default constructor
    }

    public static SampleProjectProvider getInstance() {
        if (instance == null) {
            instance = new SampleProjectProvider();
        }
        return instance;
    }
    
    public List<SampleLocalProject> getSampleProjects() {
        return SAMPLE_PROJECTS;
    }
    
    private static SampleLocalProject createSampleProjectInfo(SampleProjectType type, String name, String resourceFileName) {
        SampleLocalProject sampleProject = new SampleLocalProject();
        sampleProject.setType(type);
        sampleProject.setName(name);
        sampleProject.setResourceName(resourceFileName);
        return sampleProject;
    }
    
    private String getResourcesLocation(String projectName) {
        return RESOURCES_SAMPLE_PROJECTS_FOLDER + File.separator + projectName;
    }

    private void extractProjectSource(String projectName, String unzipedLocation) throws IOException {
        processZipFileInputStream(getResourceAsInputStream(getClass(), getResourcesLocation(projectName) + ".zip"),
                unzipedLocation);
    }

    private void processZipFileInputStream(InputStream is, String location) throws IOException {
        try (ZipInputStream stream = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                File file = new File(location, entry.getName());
                if (entry.isDirectory()) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    writeZipEntryToFile(stream, file);
                }
            }
        }
    }

    private void writeZipEntryToFile(ZipInputStream zis, File outputFile) throws IOException {
        byte[] buffer = new byte[2048];
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(outputFile);
            int len = 0;
            while ((len = zis.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private InputStream getResourceAsInputStream(Class<?> clazz, String filePath) throws IOException {
        String projectPath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        projectPath = URLDecoder.decode(projectPath, "utf-8");

        File jarFile = new File(projectPath);
        if (jarFile.isDirectory()) { // built by IDE
            return new FileInputStream(new File(jarFile, filePath).getAbsolutePath());
        } else {
            try (JarFile jar = new JarFile(jarFile)) {
                final Enumeration<JarEntry> entries = jar.entries();
                String relativePath = filePath.replace(File.separator, "/");
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().equals(relativePath)) {
                        File extractedFile = new File(ClassPathResolver.getConfigurationFolder(), relativePath);
                        FileUtils.copyInputStreamToFile(jar.getInputStream(jarEntry), extractedFile);
                        return new FileInputStream(extractedFile);
                    }
                }
                return null;
            }
        }
    }

    public void extractSampleWebUIProject(SampleLocalProject sampleProject, String location) throws IOException {
        extractProjectSource(sampleProject.getResourceName(), location);
    }

}
