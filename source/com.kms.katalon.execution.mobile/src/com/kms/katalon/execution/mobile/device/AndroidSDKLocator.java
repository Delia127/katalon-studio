package com.kms.katalon.execution.mobile.device;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;

public class AndroidSDKLocator {

    private File baseDir;

    public AndroidSDKLocator(File baseDir) {
        this.baseDir = baseDir;
    }

    public File getSDKFolder() {
        return new File(baseDir, "android_sdk");
    }

    public File getPlatformToolsFolder() {
        File sdkFolder = getSDKFolder();
        return new File(sdkFolder, "platform-tools");
    }

    public File getBuildToolsFolder() {
        File sdkFolder = getSDKFolder();
        return new File(sdkFolder, "build-tools");
    }
    
    public File getToolsFolder() {
        File sdkFolder = getSDKFolder();
        return new File(sdkFolder, "tools");
    }

    public boolean checkSDKExists() {
        File sdkFolder = getSDKFolder();
        if (!sdkFolder.exists() || !sdkFolder.isDirectory()) {
            return false;
        }

        File platformToolsFolder = getPlatformToolsFolder();
        if (!platformToolsFolder.exists() || !platformToolsFolder.isDirectory()
                || ArrayUtils.isEmpty(platformToolsFolder.listFiles())) {
            return false;
        }

        File buildToolsFolder = getBuildToolsFolder();
        if (!buildToolsFolder.exists() || !buildToolsFolder.isDirectory()
                || ArrayUtils.isEmpty(buildToolsFolder.listFiles())) {
            return false;
        }

        return true;
    }
}
