package com.kms.katalon.console.utils;

import java.awt.Desktop;
import java.net.URI;

import com.google.gson.JsonObject;
import com.kms.katalon.logging.LogUtil;

public class VersionUtil {

    public static final String URL_NEW_VERSION = "http://goo.gl/bNWyP0";

    public static boolean hasNewVersion() {
        VersionInfo currentVersion = getCurrentVersion();
        return !currentVersion.equals(getLatestVersion());
    }

    public static VersionInfo getCurrentVersion() {
        VersionInfo curVersion = new VersionInfo();
        curVersion.setVersion(ApplicationInfo.versionNo());
        try {
            curVersion.setBuildNumber(Integer.parseInt(ApplicationInfo.buildNo()));
        } catch (NumberFormatException ex) {
            curVersion.setBuildNumber(0);
        }
        return curVersion;
    }

    public static VersionInfo getLatestVersion() {
        VersionInfo newVersion = new VersionInfo();
        JsonObject versionInfo = ServerAPICommunicationUtil.getJsonInformation("/server/key?value=KATALON_LASTEST_BUILD");
        if (versionInfo == null) {
            return getCurrentVersion();
        }
        newVersion.setVersion(versionInfo.get("version").getAsString());
        try {
            newVersion.setBuildNumber(Integer.parseInt(versionInfo.get("build_number")
                    .getAsString()
                    .replaceAll("build", "")
                    .trim()));
        } catch (NumberFormatException ex) {
            newVersion.setBuildNumber(0);
        }
        return newVersion;
    }

    public static void gotoDownloadPage() {
        try {
            Desktop.getDesktop().browse(new URI(URL_NEW_VERSION));
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
    }
    
    public static boolean isInternalBuild() {
        VersionInfo version = VersionUtil.getCurrentVersion();
        return VersionInfo.MINIMUM_VERSION.equals(version.getVersion()) || version.getBuildNumber() == 0;
    }
}
