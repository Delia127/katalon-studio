package com.kms.katalon.composer.project.sample;

import java.util.Map;

public class SampleRemoteProject extends SampleProject {

    private String sourceUrl;

    private String defaultBranch;

    private Map<Integer, String> thumbnails;

    public Map<Integer, String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Map<Integer, String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }
}
