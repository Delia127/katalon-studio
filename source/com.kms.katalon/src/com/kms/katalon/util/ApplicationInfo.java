package com.kms.katalon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.StringConstants;
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
    private static final String ABOUT_MAPPINGS_FILE_NAME = "about.mappings";

    private static final String ABOUT_BUILD_NUMBER_KEY = "0";

    private static final String ABOUT_VERSION_NUMBER_KEY = "1";

    private static Properties aboutMappingsProperties;

    private static Properties getAboutMappingsProperties() {
        if (aboutMappingsProperties != null && !aboutMappingsProperties.isEmpty()) {
            return aboutMappingsProperties;
        }

        aboutMappingsProperties = new Properties();

        try {
            aboutMappingsProperties.load(FrameworkUtil.getBundle(ApplicationInfo.class)
                    .getResource(ABOUT_MAPPINGS_FILE_NAME)
                    .openStream());
        } catch (IOException e) {
            LogUtil.logError(e);
        }

        return aboutMappingsProperties;
    }

    public static String versionNo() {
        return getAboutMappingsProperties().getProperty(ABOUT_VERSION_NUMBER_KEY, StringConstants.EMPTY);
    }

    public static String buildNo() {
        return getAboutMappingsProperties().getProperty(ABOUT_BUILD_NUMBER_KEY, StringConstants.EMPTY);
    }

    public static String installLocation() {
        try {
            return Paths.get(Platform.getInstallLocation().getURL().toURI()).toString();
        } catch (NullPointerException e) {
            // do nothing
        } catch (URISyntaxException e) {
            // do nothing
        }
        return StringConstants.EMPTY;
    }

    /**
     * @return <code>.katalon</code> directory (in user home) location
     */
    public static String userDirLocation() {
        return StringConstants.APP_USER_DIR_LOCATION;
    }

    public static void setAppInfoIntoUserHomeDir() {
        File katalonDir = new File(userDirLocation());
        File appPropFile = new File(StringConstants.APP_INFO_FILE_LOCATION);
        Properties appProp = new Properties();
        try {
            if (!appPropFile.exists()) {

                if (!katalonDir.exists()) {
                    katalonDir.mkdir();
                }
                appPropFile.createNewFile();
            }

            appProp.load(new FileInputStream(appPropFile));
            logInfo(StringConstants.APP_VERSION_NUMBER_KEY + "=" + versionNo());
            logInfo(StringConstants.APP_BUILD_NUMBER_KEY + "=" + buildNo());

            if (versionNo().equals(appProp.getProperty(StringConstants.APP_VERSION_NUMBER_KEY))
                    && buildNo().equals(appProp.getProperty(StringConstants.APP_BUILD_NUMBER_KEY))) {
                return;
            }

            appProp.setProperty(StringConstants.APP_VERSION_NUMBER_KEY, versionNo());
            appProp.setProperty(StringConstants.APP_BUILD_NUMBER_KEY, buildNo());
            appProp.store(new FileOutputStream(appPropFile), installLocation());
        } catch (FileNotFoundException e) {
            LogUtil.logError(e);
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }

    private static void logInfo(String message) {
        LogUtil.println(LogManager.getOutputLogger(), message, LogMode.LOG);
    }
}
