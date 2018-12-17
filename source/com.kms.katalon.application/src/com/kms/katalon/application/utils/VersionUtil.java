package com.kms.katalon.application.utils;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.program.Program;

import com.google.gson.JsonObject;
import com.kms.katalon.logging.LogUtil;

public class VersionUtil {

    public static final String URL_NEW_VERSION = "http://goo.gl/bNWyP0";

    public static boolean hasNewVersion() {
        VersionInfo currentVersion = getCurrentVersion();
        return isNewer(getLatestVersion().getVersion(), currentVersion.getVersion());
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
            Program.launch(URL_NEW_VERSION);
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
    }
    
    public static boolean isDevelopmentBuild() {
        return ApplicationInfo.DEV_PROFILE.equals(ApplicationInfo.profile())
                || "${build.profile}".equals(ApplicationInfo.profile());
    }

    public static boolean isStagingBuild() {
        return ApplicationInfo.STAG_PROFILE.equals(ApplicationInfo.profile()) 
                || ApplicationInfo.DEV_PROFILE.equals(ApplicationInfo.profile());
    }

    public static boolean isNewer(String version, String comparedVersion) {
        if (StringUtils.equals(version, comparedVersion)) {
            return false;
        }

        int[] thisVer = Arrays.stream(StringUtils.split(version, '.')).mapToInt(Integer::parseInt).toArray();

        int[] thatVer = Arrays.stream(StringUtils.split(comparedVersion, '.')).mapToInt(Integer::parseInt).toArray();
        
        int maxLength = Math.max(thisVer.length, thatVer.length);
        while (thisVer.length < maxLength) {
            thisVer = ArrayUtils.add(thisVer, 0);
        }
        
        while (thatVer.length < maxLength) {
            thatVer = ArrayUtils.add(thatVer, 0);
        }

        for (int i = 0; i < maxLength; i++) {
            if (thisVer[i] == thatVer[i]) {
                continue;
            }

            if (thisVer[i] > thatVer[i]) {
                return true;
            }
            
            if (thisVer[i] < thatVer[i]) {
                return false;
            }
        }
        return false;
    }
}