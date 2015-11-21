package com.kms.katalon.integration.qtest.setting;

public enum QTestAttachmentSendingType {
	SEND_IF_PASSES("Test case passes"),
	SEND_IF_FAILS("Test case fails");
	
    private final String text;

    private QTestAttachmentSendingType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
