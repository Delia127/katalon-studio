package com.kms.katalon.execution.launcher.model;

public enum LauncherStatus {
    WAITING("Waiting"), SUSPEND("Suspend"), RUNNING("Running"), TERMINATED("Terminated"), DONE("Done"), SENDING_EMAIL(
            "Sending email");

    private final String text;

    private LauncherStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
