package com.kms.katalon.composer.project.sample;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SampleRemoteProject {
    private String name;

    private ProjectType type;

    private String sourceUrl;

    private String defaultBranch;

    private Map<Integer, String> thumbnails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

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

    public static enum ProjectType {
        WEBUI, MOBILE, WS, MIXED;

        private ProjectType() {

        }

        public static ProjectType fromString(String type) {
            if (StringUtils.isEmpty(type)) {
                return ProjectType.MIXED;
            }
            switch (type.toLowerCase()) {
                case "webui":
                    return WEBUI;
                case "mobile":
                    return MOBILE;
                case "ws":
                    return WS;
                default:
                    return MIXED;
            }
        }
    }
}
