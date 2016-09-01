package com.kms.katalon.composer.project.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class TemplateProjectGenerator {

    private static final String TEMPL_PROJ_JAR_ENTRY = "resources/templates/Katalon Get Started Project.zip";

    private static final String OS_TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static final String JAR_ENTRY_PATH_SEP = "/";

    private static final String FILE_EXTENSION_SEP = ".";

    private static final String WEB_UI_FOLDER = "Web UI";

    private static final String MOBILE_FOLDER = "Mobile";

    private static final String WEB_API_FOLDER = "Web API";

    private static final String DATA_FOLDER = "Data";

    private static final String CUSTOM_KEYWORD_PKG = "com.example";

    private static final String GLOBAL_VAR_FILE = "GlobalVariables.glbl";

    private static final String SAMPLE_ANDROID_APP = "androidapp";
    
    private static final String[] WEB_UI_CUSTOM_KWS = new String[]{ "WebUiCustomKeywords.groovy", "DatabaseUtils.groovy" }; 
    
    private static final String[] WEB_API_CUSTOM_KWS = new String[]{ "WebApiCustomKeywords.groovy" };

    private static final String EXCEL_DATA_FILE = "DataFile.xlsx";
    
    private static final String SAMPLE_DB_FILE = "SampleDB.sqlite";
    
    private String[] artifacts = new String[] { 
            GlobalStringConstants.ROOT_FOLDER_NAME_TEST_CASE,
            GlobalStringConstants.TEST_CASE_SCRIPT_ROOT_FOLDER_NAME,
            GlobalStringConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY, 
            GlobalStringConstants.ROOT_FOLDER_NAME_DATA_FILE,
            GlobalStringConstants.ROOT_FOLDER_NAME_TEST_SUITE 
    };
    
    private File tmpProjectDir;
    
    private File destProjectDir;
    
    public TemplateProjectGenerator(ProjectEntity project) {
        String tmplProjName = TEMPL_PROJ_JAR_ENTRY.substring(
                TEMPL_PROJ_JAR_ENTRY.lastIndexOf(JAR_ENTRY_PATH_SEP) + 1,
                TEMPL_PROJ_JAR_ENTRY.indexOf(FILE_EXTENSION_SEP));
        tmpProjectDir = new File(OS_TEMP_DIR, tmplProjName);
        destProjectDir = new File(project.getFolderLocation());        
    }

    public void copyTemplates(List<String> templates) throws IOException, ZipException {
        
        if (templates == null || templates.size() == 0) {
            return;
        }
        
        extractProjectZipFile();

        copyCommonResources();

        for (String template : templates) {
            String component = StringConstants.VIEW_LBL_WEB_TESTING.equals(template) ? WEB_UI_FOLDER
                    : StringConstants.VIEW_LBL_MOBILE_TESTING.equals(template) ? MOBILE_FOLDER : WEB_API_FOLDER;
            copyTestArtifacts(component);
            if(WEB_UI_FOLDER.equals(component)){
                copyCustomKeywords(WEB_UI_CUSTOM_KWS);
                //Copy extenal data files
                copyExternalDataFile(EXCEL_DATA_FILE);
                copyExternalDataFile(SAMPLE_DB_FILE);
                // Copy driver for JDBC
                FileUtils.copyDirectory(new File(tmpProjectDir, GlobalStringConstants.ROOT_FOLDER_NAME_DRIVERS),
                        new File(destProjectDir, GlobalStringConstants.ROOT_FOLDER_NAME_DRIVERS));
            }
            if(WEB_API_FOLDER.equals(component)){
                copyCustomKeywords(WEB_API_CUSTOM_KWS);
                // Copy sample app
                FileUtils.copyDirectoryToDirectory(new File(tmpProjectDir, SAMPLE_ANDROID_APP), destProjectDir);
                //Copy extenal data file
                copyExternalDataFile(EXCEL_DATA_FILE);
            }
        }
    }
    
    /**
     * Copy test cases, objects, test suites, data files
     * @param component
     * @throws IOException
     */
    private void copyTestArtifacts(String component) throws IOException {
        for(String artifact : artifacts){
            File src = new File(tmpProjectDir, artifact + File.separator + component);
            if(src.exists()){
                File dest = new File(destProjectDir, artifact);
                FileUtils.copyDirectoryToDirectory(src, dest);                
            }
        }
    }
    
    /**
     * Copy common settings, global variables
     * @throws IOException 
     */
    private void copyCommonResources() throws IOException{
        FileUtils.copyDirectoryToDirectory(new File(tmpProjectDir, GlobalStringConstants.ROOT_FOLDER_NAME_SETTINGS), destProjectDir);
        FileUtils.copyFileToDirectory(new File(tmpProjectDir, GLOBAL_VAR_FILE), destProjectDir);        
    }
    
    private void copyCustomKeywords(String[] fileNames) throws IOException{
        String customKwPackageFolder = GlobalStringConstants.ROOT_FOLDER_NAME_KEYWORD + File.separator + CUSTOM_KEYWORD_PKG.replace(FILE_EXTENSION_SEP, File.separator);
        File destPackage = new File(destProjectDir, customKwPackageFolder);
        if (!destPackage.exists()) {
            destPackage.mkdirs();
        }
        for(String fileName : fileNames){
            File webUiKeywordFile = new File(tmpProjectDir, customKwPackageFolder + File.separator + fileName);
            FileUtils.copyFileToDirectory(webUiKeywordFile, destPackage);
        }
    }
    
    private void copyExternalDataFile(String fileName) throws IOException{
        File tmpDataFolder = new File(tmpProjectDir, DATA_FOLDER);
        File destDataFolder = new File(destProjectDir, DATA_FOLDER);
        if(!destDataFolder.exists()){
            destDataFolder.mkdir();
        }
        if(!new File(destDataFolder, fileName).exists()){
            FileUtils.copyFileToDirectory(new File(tmpDataFolder, fileName), destDataFolder);    
        }
    }
    
    private void extractProjectZipFile() throws IOException, ZipException {
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

                    String zipFileName = entryName.substring(entryName.lastIndexOf(JAR_ENTRY_PATH_SEP) + 1);
                    String projectFolderName = entryName.substring(entryName.lastIndexOf(JAR_ENTRY_PATH_SEP) + 1,
                            entryName.indexOf(FILE_EXTENSION_SEP));

                    // Clean all old temporary version
                    File tempProjectFolder = new File(OS_TEMP_DIR + File.separator + projectFolderName);
                    if (tempProjectFolder.exists()) {
                        FileUtils.deleteDirectory(tempProjectFolder);
                    }

                    File tempZipFile = new File(OS_TEMP_DIR + File.separator + zipFileName);
                    if (tempZipFile.exists()) {
                        FileUtils.deleteQuietly(tempZipFile);
                    }

                    // Copy new zip file
                    FileOutputStream fos = new FileOutputStream(tempZipFile);
                    IOUtils.copy(jar.getInputStream(jarEntry), fos);
                    fos.flush();
                    fos.close();

                    // Extract zip file
                    unzip(tempZipFile, OS_TEMP_DIR, true);

                    break;
                }
            }
            jar.close();
        } else { // Run with IDE

            File zipFile = new File(path + File.separator + TEMPL_PROJ_JAR_ENTRY);
            String projectFolderName = FilenameUtils.getBaseName(zipFile.getName());

            // Clean all old temporary version
            File tempProjectFolder = new File(OS_TEMP_DIR + File.separator + projectFolderName);
            if (tempProjectFolder.exists()) {
                FileUtils.deleteDirectory(tempProjectFolder);
            }

            // Extract zip file
            unzip(zipFile, OS_TEMP_DIR, false);
        }
    }
    
    private void unzip(File tempZipFile, String destination, boolean deleteZipFile) throws ZipException {
        try {
            ZipFile zipFile = new ZipFile(tempZipFile);
            zipFile.extractAll(destination);
        } finally {
            if (deleteZipFile) {
                tempZipFile.delete();
            }
        }
    }
}
