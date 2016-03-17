package com.kms.katalon.composer.integration.qtest.report;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.execution.entity.LongConsoleOption;

public class QTestDestinationIdIntegrationCommand extends LongConsoleOption {
    private static final long DEFAULT_DESTINATION_ID = -1L;
    private static final String UPLOADED_ID_PREFIX = "qTestDestId";
    private long destId;

    public QTestDestinationIdIntegrationCommand() {
        setDestinationId(DEFAULT_DESTINATION_ID);
    }

    public long getDestinationId() {
        return destId;
    }

    private void setDestinationId(long destId) {
        this.destId = destId;
    }

    @Override
    public String getOption() {
        return UPLOADED_ID_PREFIX;
    }

    @Override
    public void setArgumentValue(String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        destId = Long.parseLong(value.trim());
    }

    @Override
    public boolean hasArgument() {
        return true;
    }
}
