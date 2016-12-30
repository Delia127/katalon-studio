package com.kms.katalon.composer.project.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.handlers.OpenProjectHandler;
import com.kms.katalon.controller.ProjectController;

public class SampleProjectProvider {
    private static final String DEFAULT_SAMPLES_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME + File.separator + "Samples";

    private static final String RESOURCES_SAMPLE_PROJECTS_FOLDER = "resources" + File.separator + "samples";

    public static final String SAMPLE_WEB_UI = "WebUI";

    public static final String SAMPLE_MOBILE = "Mobile";

    public static final String SAMPLE_WEB_SERVICE = "WebService";

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

    private String getInstalledLocation(String projectName) {
        return DEFAULT_SAMPLES_PROJECT_LOCATION + File.separator + projectName;
    }

    private String getResourcesLocation(String projectName) {
        return RESOURCES_SAMPLE_PROJECTS_FOLDER + File.separator + projectName;
    }

    private void extractProjectSource(String projectName, String unzipedLocation) throws IOException {
        processZipFileInputStream(getResourceAsInputStream(getClass(), getResourcesLocation(projectName) + ".zip"),
                unzipedLocation);
    }

    private void processZipFileInputStream(InputStream is, String location) throws IOException {
        ZipInputStream stream = new ZipInputStream(is);
        try {
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
        } finally {
            stream.close();
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
            return clazz.getClassLoader().getResourceAsStream(filePath);
        }
    }

    private void openSampleProject(String projectName) throws Exception {
        String installedProjectFolder = getInstalledLocation(projectName);

        ProjectController instance = ProjectController.getInstance();
        if (instance.getProjectFile(installedProjectFolder) == null) {
            extractProjectSource(projectName, installedProjectFolder);
        }

        OpenProjectHandler.doOpenProject(null, instance.getProjectFile(installedProjectFolder).getAbsolutePath(),
                UISynchronizeService.getInstance().getSync(), EventBrokerSingleton.getInstance().getEventBroker(),
                PartServiceSingleton.getInstance().getPartService(),
                ModelServiceSingleton.getInstance().getModelService(),
                ApplicationSingleton.getInstance().getApplication());
    }

    public void openSampleWebUIProject() throws Exception {
        openSampleProject(SAMPLE_WEB_UI);
    }

    public void openSampleMobileProject() throws Exception {
        openSampleProject(SAMPLE_MOBILE);
    }

    public void openSampleWebServiceProject() throws Exception {
        openSampleProject(SAMPLE_WEB_SERVICE);
    }

}
