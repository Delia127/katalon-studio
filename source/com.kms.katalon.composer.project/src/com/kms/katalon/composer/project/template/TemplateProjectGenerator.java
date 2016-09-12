package com.kms.katalon.composer.project.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ClassUtils;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class TemplateProjectGenerator {

    private static final String TEMPL_PROJ_JAR_ENTRY = "resources/templates/Katalon Get Started Project.zip";

    private static final String WEB_UI_FOLDER = "Web UI";

    private static final String MOBILE_FOLDER = "Mobile";

    private static final String WEB_API_FOLDER = "Web API";

    private static final String DATA_FOLDER = "Data";

    private static final String CUSTOM_KEYWORD_PKG = "com.example";

    private static final String SAMPLE_ANDROID_APP = "androidapp";

    private String PROJECT_NAME_OPEN_TAG = "<name>";

    private String PROJECT_NAME_CLOSE_TAG = "</name>";

    private static final String[] WEB_UI_CUSTOM_KWS = new String[] { "WebUiCustomKeywords.groovy",
            "DatabaseUtils.groovy" };

    private static final String[] WEB_API_CUSTOM_KWS = new String[] { "WebApiCustomKeywords.groovy" };

    private static final String TEMPL_PROJECT_NAME = "Katalon Get Started Project";

    private String[] artifacts = new String[] { GlobalStringConstants.ROOT_FOLDER_NAME_TEST_CASE,
            GlobalStringConstants.TEST_CASE_SCRIPT_ROOT_FOLDER_NAME,
            GlobalStringConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY,
            GlobalStringConstants.ROOT_FOLDER_NAME_TEST_SUITE, GlobalStringConstants.ROOT_FOLDER_NAME_DATA_FILE,
            DATA_FOLDER };

    private String location;

    private String projectName;

    private File projectFolder;

    public TemplateProjectGenerator(String location, String projectName) {
        this.location = location;
        this.projectName = projectName;
        this.projectFolder = new File(location, projectName);
    }

    public void copyTemplates(List<String> templates) throws IOException {

        if (templates == null || templates.size() == 0) {
            return;
        }

        extractProjectZipFile();

        renameProject();

        deleteUnselectedComponents(templates);
    }

    private void renameProject() throws IOException {
        // Rename project folder
        File tmplProjectFolder = new File(location, TEMPL_PROJECT_NAME);
        tmplProjectFolder.renameTo(projectFolder);

        // Rename .prj file and its content
        File prjFile = new File(projectFolder, projectName + ProjectEntity.getProjectFileExtension());
        new File(projectFolder, TEMPL_PROJECT_NAME + ProjectEntity.getProjectFileExtension()).renameTo(prjFile);

        StringBuilder sb = new StringBuilder(FileUtils.readFileToString(prjFile));
        sb.replace(sb.indexOf(PROJECT_NAME_OPEN_TAG) + PROJECT_NAME_OPEN_TAG.length(),
                sb.indexOf(PROJECT_NAME_CLOSE_TAG), projectName);

        FileUtils.writeStringToFile(prjFile, sb.toString(), false);
    }

    private void deleteUnselectedComponents(List<String> templates) throws IOException {
        // Delete unnecessary files
        @SuppressWarnings("serial")
        List<String> removedCompoments = new ArrayList<String>() {
            {
                add(WEB_UI_FOLDER);
                add(MOBILE_FOLDER);
                add(WEB_API_FOLDER);
            }
        };
        for (String template : templates) {
            String component = StringConstants.VIEW_LBL_WEB_TESTING.equals(template) ? WEB_UI_FOLDER
                    : StringConstants.VIEW_LBL_MOBILE_TESTING.equals(template) ? MOBILE_FOLDER : WEB_API_FOLDER;
            removedCompoments.remove(component);
        }
        for (String component : removedCompoments) {
            removeTestArtifacts(component);
            // Remove custom keywords, external data
            if (WEB_UI_FOLDER.equals(component)) {
                deleteCustomKeywords(WEB_UI_CUSTOM_KWS);
                FileUtils.cleanDirectory(new File(projectFolder, GlobalStringConstants.ROOT_FOLDER_NAME_DRIVERS));
            }
            if (WEB_API_FOLDER.equals(component)) {
                deleteCustomKeywords(WEB_API_CUSTOM_KWS);
            }
            if (MOBILE_FOLDER.equals(component)) {
                FileUtils.deleteDirectory(new File(projectFolder, SAMPLE_ANDROID_APP));
            }
        }
    }

    private void deleteCustomKeywords(String[] fileNames) throws IOException {
        String customKwPackageFolder = GlobalStringConstants.ROOT_FOLDER_NAME_KEYWORD + File.separator
                + CUSTOM_KEYWORD_PKG.replace(ClassUtils.PACKAGE_SEPARATOR, File.separator);
        File destPackage = new File(projectFolder, customKwPackageFolder);
        if (!destPackage.exists()) {
            return;
        }
        for (String fileName : fileNames) {
            File webUiKeywordFile = new File(destPackage, fileName);
            if(webUiKeywordFile.exists()){
                FileUtils.deleteQuietly(webUiKeywordFile);
            }
        }
        // Delete empty package
        if (destPackage.exists() && destPackage.listFiles().length == 0) {
            FileUtils.deleteQuietly(destPackage.getParentFile());
        }
    }

    /**
     * Remove unnecessary test cases, objects, test suites
     * 
     * @param component
     * @throws IOException
     */
    private void removeTestArtifacts(String component) throws IOException {
        for (String artifact : artifacts) {
            File folder = new File(projectFolder, artifact + File.separator + component);
            if (folder.exists()) {
                FileUtils.deleteQuietly(folder);
            }
        }
    }

    private void extractProjectZipFile() throws IOException {
        String path = TemplateProjectGenerator.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        File jarFile = new File(path);
        if (jarFile.isFile()) {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(TEMPL_PROJ_JAR_ENTRY)) {
                    processZipFileInputStream(jar.getInputStream(jarEntry));
                    break;
                }
            }
            jar.close();
        } else { // Run with IDE
            File zipFile = new File(path + File.separator + TEMPL_PROJ_JAR_ENTRY);
            processZipFileInputStream(new FileInputStream(zipFile));
        }
    }

    private void processZipFileInputStream(InputStream is) throws IOException {
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
            if (output != null){
                output.close();
            }
        }
    }
}
