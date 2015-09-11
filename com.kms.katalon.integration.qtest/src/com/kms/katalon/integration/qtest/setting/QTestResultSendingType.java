package com.kms.katalon.integration.qtest.setting;

public enum QTestResultSendingType {
    SEND_IF_PASSES("If test case passes"),
    SEND_IF_FAILS("If test case fails");
    
    private final String text;

    private QTestResultSendingType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
