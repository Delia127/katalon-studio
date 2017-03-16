package com.kms.katalon.execution.launcher.result;

public enum LauncherStatus {
    WAITING("Waiting"), SUSPENDED("Suspended"), RUNNING("Running"), PREPARE_REPORT("Prepare report"), SENDING_REPORT("Sending report"), UPLOAD_REPORT("Upload report"), TERMINATED(
            "Terminated"),
    DONE("Done");

    private final String text;

    private LauncherStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
