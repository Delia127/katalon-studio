package com.kms.katalon.execution.launcher.model;

import org.apache.commons.lang.StringUtils;

public enum LaunchMode {
    RUN("run"), DEBUG("debug");

    private final String text;

    private LaunchMode(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LaunchMode fromText(String text) {
        for (LaunchMode launchMode : values()) {
            if (StringUtils.equals(text, launchMode.text)) {
                return launchMode;
            }
        }
        return null;
    }
}
