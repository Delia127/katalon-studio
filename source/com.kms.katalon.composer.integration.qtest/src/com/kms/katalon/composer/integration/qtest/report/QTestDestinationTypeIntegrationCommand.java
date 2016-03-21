package com.kms.katalon.composer.integration.qtest.report;

import com.kms.katalon.execution.entity.StringConsoleOption;

public class QTestDestinationTypeIntegrationCommand extends StringConsoleOption {
    private static final String UPLOADED_TYPE_PREFIX = "qTestDestType";
    private String destType;

    public String getDestinationType() {
        return destType;
    }

    @Override
    public String getOption() {
        return UPLOADED_TYPE_PREFIX;
    }

    @Override
    public void setArgumentValue(String value) {
        if (value == null) {
            return;
        }
        destType = value.trim();
    }
}
