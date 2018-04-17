package com.kms.katalon.composer.update.models;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.gson.Gson;

public class FileInfo {

    private String location;

    private String extractLocation;

    private String hash;

    private long size;

    private String status;

    private String[] excludes;

    private String[] executableFiles;

    public FileInfo() {
        this(null, null, null, 0);
    }

    public FileInfo(String location, String extractLocation, String hash, long size) {
        this(location, extractLocation, hash, size, "NEW");
    }

    public FileInfo(String location, String extractLocation, String hash, long size, String status) {
        this.location = location;
        this.extractLocation = extractLocation;
        this.hash = hash;
        this.size = size;
        this.status = status;
        this.excludes = new String[0];
        this.executableFiles = new String[0];
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FileInfo)) {
            return false;
        }

        FileInfo that = (FileInfo) obj;
        return new EqualsBuilder().append(this.getLocation(), that.getLocation())
                .append(this.getHash(), that.getHash())
                .append(this.getExcludes(), that.getExcludes())
                .append(this.getExecutableFiles(), that.getExecutableFiles())
                .append(this.getExtractLocation(), that.getExtractLocation())
                .isEquals();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String getExtractLocation() {
        return extractLocation;
    }

    public void setExtractLocation(String extractLocation) {
        this.extractLocation = extractLocation;
    }

    public String[] getExecutableFiles() {
        return executableFiles;
    }

    public void setExecutableFiles(String[] executableFiles) {
        this.executableFiles = executableFiles;
    }
}
