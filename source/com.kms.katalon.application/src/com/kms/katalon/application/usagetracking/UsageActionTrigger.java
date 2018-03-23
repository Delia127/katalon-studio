package com.kms.katalon.application.usagetracking;

public enum UsageActionTrigger {
    OPEN_FIRST_TIME("openFirstTime"),
    OPEN_APPLICATION("openApplication"),
    SAVE("save"),
    SAVE_ALL("saveAll"),
    RUN_SCRIPT("runScript");

    private final String action;

    private UsageActionTrigger(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
