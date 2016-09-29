package com.kms.katalon.util;

import java.awt.Desktop;
import java.net.URI;

import com.google.gson.JsonObject;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class VersionUtil {

    public static final String URL_NEW_VERSION = "http://goo.gl/bNWyP0";

    public static boolean hasNewVersion() {
        VersionInfo currentVersion = getCurrentVersion();
        return !currentVersion.equals(getLatestVersion());
    }

    public static VersionInfo getCurrentVersion() {
        VersionInfo curVersion = new VersionInfo();
        curVersion.version = ApplicationInfo.versionNo();
        curVersion.buildNumber = Integer.parseInt(ApplicationInfo.buildNo());
        return curVersion;
    }

    public static VersionInfo getLatestVersion() {
        VersionInfo newVersion = new VersionInfo();
        JsonObject versionInfo = ServerAPICommunicationUtil.getJsonInformation("/server/key?value=KATALON_LASTEST_BUILD");
        if (versionInfo == null) {
            return getCurrentVersion();
        }
        newVersion.version = versionInfo.get("version").getAsString();
        newVersion.buildNumber = Integer.parseInt(versionInfo.get("build_number")
                .getAsString()
                .replaceAll("build", "")
                .trim());
        return newVersion;
    }

    public static void gotoDownloadPage() {
        try {
            Desktop.getDesktop().browse(new URI(URL_NEW_VERSION));
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }
}
