package com.kms.katalon.composer.update.models;

public class ExecInfo {

    // Absolute path to KS application directory
    private String appDir;

    // Relative path to katalon executable file. Eg: katalon.exe for Windows, katalon for unix.
    private String execFile;

    // Absolute path to downloaded latest version directory
    private String latestVersionDir;

    // Version number of the latest version
    private String latestVersion;

    // Version number of the current version
    private String currentVersion;

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    public String getExecFile() {
        return execFile;
    }

    public void setExecFile(String execFile) {
        this.execFile = execFile;
    }

    public String getLatestVersionDir() {
        return latestVersionDir;
    }

    public void setLatestVersionDir(String latestVersionDir) {
        this.latestVersionDir = latestVersionDir;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}


