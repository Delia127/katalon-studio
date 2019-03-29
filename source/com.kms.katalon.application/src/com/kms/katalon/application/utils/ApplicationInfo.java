package com.kms.katalon.application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.logging.LogManager;
import com.kms.katalon.logging.LogMode;
import com.kms.katalon.logging.LogUtil;

/**
 * Read application info from <code>about.mappings</code>
 * <p>
 * By default <code>about.mappings</code> has 2 entries with
 * </p>
 * <ul>
 * <li>Key <strong>0</strong> for build number</li>
 * <li>Key <strong>1</strong> for version number</li>
 * </ul>
 */
public class ApplicationInfo {
    public static final String DEV_PROFILE = "dev";
    public static final String STAG_PROFILE = "stag";
    public static final String PROD_PROFILE = "prod";

    private static final String ABOUT_MAPPINGS_FILE_NAME = "about.mappings";
    private static final String ABOUT_BUILD_NUMBER_KEY = "0";
    private static final String ABOUT_VERSION_NUMBER_KEY = "1";
    private static final String ABOUT_PROFILE_KEY = "2";
    private static final String ABOUT_VERSION_TAG = "3";
    
    private static final String PROFILE_PROPERTY_KEY = "katalonProfile";

    private static Properties aboutMappingsProperties;

    private static Properties appProperties;

    private static Properties getAboutMappingsProperties() {
        if (aboutMappingsProperties != null && !aboutMappingsProperties.isEmpty()) {
            return aboutMappingsProperties;
        }

        aboutMappingsProperties = new Properties();

        try {
            aboutMappingsProperties.load(Platform.getBundle("com.kms.katalon").getResource(ABOUT_MAPPINGS_FILE_NAME)
                            .openStream());
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        return aboutMappingsProperties;
    }

    public static String versionNo() {
        return getAboutMappingsProperties().getProperty(ABOUT_VERSION_NUMBER_KEY, GlobalStringConstants.EMPTY);
    }

    public static String buildNo() {
        return getAboutMappingsProperties().getProperty(ABOUT_BUILD_NUMBER_KEY, GlobalStringConstants.EMPTY);
    }

    public static String profile() {
//        return getAboutMappingsProperties().getProperty(ABOUT_PROFILE_KEY, DEV_PROFILE);
        return System.getProperty(PROFILE_PROPERTY_KEY);
    }
    
    public static String versionTag() {
    	return getAboutMappingsProperties().getProperty(ABOUT_VERSION_TAG, GlobalStringConstants.EMPTY);
    }

    public static String installLocation() {
        try {
            return Paths.get(new URI(Platform.getInstallLocation().getURL().toString().replace(" ", "%20"))).toString();
        } catch (NullPointerException e) {
            // do nothing
        } catch (URISyntaxException e) {
            // do nothing
        }
        return ApplicationStringConstants.EMPTY;
    }

    /**
     * @return <code>.katalon</code> directory (in user home) location
     */
    public static String userDirLocation() {
        return ApplicationStringConstants.APP_USER_DIR_LOCATION;
    }

    public static void setAppInfoIntoUserHomeDir() {
        String version = versionNo();
        String buildNo = buildNo();

        getAppProperties();
        System.setProperty(ApplicationStringConstants.APP_VERSION_NUMBER_KEY, version);
        logInfo(ApplicationStringConstants.APP_VERSION_NUMBER_KEY + "=" + version);
        logInfo(ApplicationStringConstants.APP_BUILD_NUMBER_KEY + "=" + buildNo);

        if (version.equals(getAppProperty(ApplicationStringConstants.APP_VERSION_NUMBER_KEY))
                && buildNo.equals(getAppProperty(ApplicationStringConstants.APP_BUILD_NUMBER_KEY))) {
            return;
        }

        setAppProperty(ApplicationStringConstants.APP_VERSION_NUMBER_KEY, version, false);
        setAppProperty(ApplicationStringConstants.APP_BUILD_NUMBER_KEY, buildNo, false);
        saveAppProperties();
    }

    private static Properties getAppProperties() {
        if (appProperties != null) {
            return appProperties;
        }

        File appPropFile = new File(ApplicationStringConstants.APP_INFO_FILE_LOCATION);
        File katalonDir = new File(userDirLocation());
        if (!appPropFile.exists()) {
            if (!katalonDir.exists()) {
                katalonDir.mkdir();
            }
            try {
                appPropFile.createNewFile();
            } catch (Exception ex) {
                LogUtil.logError(ex);
            }
        }
        try (FileInputStream in = new FileInputStream(appPropFile)) {
            appProperties = new Properties();
            appProperties.load(in);
        } catch (Exception ex) {
            appProperties = null;
            LogUtil.logError(ex);
        }

        return appProperties;
    }

    public static void setAppProperty(String key, String value, boolean autoSave) {
        Properties appProps = getAppProperties();

        appProps.setProperty(key, value);
        if (autoSave) {
            saveAppProperties();
        }
    }

    public static String getAppProperty(String key) {
        Properties appProps = getAppProperties();

        if (appProps != null && appProps.containsKey(key)) {
            return appProps.getProperty(key);
        }
        return null;
    }

    private static void saveAppProperties() {
        if (appProperties == null) {
            return;
        }
        try (FileOutputStream out = new FileOutputStream(ApplicationStringConstants.APP_INFO_FILE_LOCATION)) {
            appProperties.store(out, installLocation());
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
    }

    private static void logInfo(String message) {
        LogUtil.println(LogManager.getOutputLogger(), message, LogMode.LOG);
    }

    public static void removeAppProperty(String key) {
        Properties appProps = getAppProperties();

        if (appProps != null && appProps.containsKey(key)) {
            appProps.remove(key);
            saveAppProperties();
        }
    }
}