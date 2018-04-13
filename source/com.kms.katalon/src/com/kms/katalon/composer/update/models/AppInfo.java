package com.kms.katalon.composer.update.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.gson.Gson;

public class AppInfo {
    private String platform;

    private String version;

    private List<FileInfo> files;

    public AppInfo() {
        this("", "", new ArrayList<>());
    }

    public AppInfo(String platform, String version, List<FileInfo> files) {
        this.platform = platform;
        this.version = version;
        this.files = files;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<FileInfo> getFiles() {
        if (files == null) {
            return Collections.emptyList();
        }
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public void removeFile(String fileLocation) {
        if (files == null || files.isEmpty()) {
            return;
        }
        files.removeIf(file -> StringUtils.equals(file.getLocation(), fileLocation));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AppInfo)) {
            return false;
        }

        AppInfo that = (AppInfo) obj;
        return new EqualsBuilder()
                .append(this.getPlatform(), that.getPlatform())
                .append(this.getVersion(), that.getVersion())
                .append(this.getFiles(), that.getFiles())
                .isEquals();
    }
}
