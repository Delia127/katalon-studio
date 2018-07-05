package com.kms.katalon.application.usagetracking;

public enum UsageActionTrigger {
    OPEN_FIRST_TIME("openFirstTime"),
    OPEN_APPLICATION("openApplication"),
    SAVE("save"),
    SAVE_ALL("saveAll"),
    RUN_SCRIPT("runScript"),
    SPY("spy"),
    RECORD("record"),
    EXECUTE_TEST_CASE("executeTestCase"),
    EXECUTE_TEST_SUITE("executeTestSuite"),
    EXECUTE_TEST_SUITE_COLLECTION("executeTestSuiteCollection"),
    GENERATE_CMD("generateCommand"),
    COLLECT_STATISTICS("collectStatistics"),
    QUICK_OVERVIEW("quickOverview"),
    NEW_OBJECT("newObject");

    private final String action;

    private UsageActionTrigger(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
