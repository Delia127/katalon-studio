package com.kms.katalon.application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.logging.LogUtil;

public class LicenseInfo {

    private static final String LICENSE_SERVER_URL = "serverURL";

    private static final String LICENSE_SERVER_URL_SECOND_OPTION = "serverUrl";

    private static final String LICENSE_SERVER_URL_THIRD_OPTION = "serverurl";

    private static final String LICENSE_API_KEY = "apiKey";

    private static final String LICENSE_API_KEY_SECOND_OPTION = "apikey";

    private static Properties licenseProperties;

    private static Properties getLicenseProperties() {
        if (licenseProperties != null) {
            return licenseProperties;
        }

        File licensePropFile = new File(ApplicationStringConstants.APP_LICENSE_SERVER_FILE_LOCATION);
        File katalonDir = new File(ApplicationInfo.userDirLocation());
        if (!licensePropFile.exists()) {
            if (!katalonDir.exists()) {
                katalonDir.mkdir();
            }
            try {
                licensePropFile.createNewFile();
            } catch (Exception e) {
                LogUtil.logError(e);
            }
        }
        try (FileInputStream in = new FileInputStream(licensePropFile)) {
            licenseProperties = new Properties();
            licenseProperties.load(in);
        } catch (Exception e) {
            licenseProperties = null;
            LogUtil.logError(e);
        }
        return licenseProperties;
    }

    public static String getLicenseProperty(String key) {
        Properties licenseProps = getLicenseProperties();

        if (licenseProps != null && licenseProps.containsKey(key)) {
            return licenseProps.getProperty(key);
        }

        return null;
    }

    public static String getServerURL() {
        String server = getLicenseProperty(LICENSE_SERVER_URL);

        if (server == null) {
            server = getLicenseProperty(LICENSE_SERVER_URL_SECOND_OPTION);
        }

        if (server == null) {
            server = getLicenseProperty(LICENSE_SERVER_URL_THIRD_OPTION);
        }

        return server;
    }

    public static String getApiKey() {
        String apiKey = getLicenseProperty(LICENSE_API_KEY);

        if (apiKey == null) {
            apiKey = getLicenseProperty(LICENSE_API_KEY_SECOND_OPTION);
        }

        return apiKey;
    }
}
