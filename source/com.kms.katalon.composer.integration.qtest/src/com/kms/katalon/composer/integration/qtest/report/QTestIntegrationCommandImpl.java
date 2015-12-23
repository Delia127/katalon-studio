package com.kms.katalon.composer.integration.qtest.report;

import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;

import com.kms.katalon.execution.exception.KatalonArgumentNotValidException;
import com.kms.katalon.execution.integration.IntegrationCommand;

public class QTestIntegrationCommandImpl implements IntegrationCommand {
    private static final int NUM_PARAMS = 2;
    private static final String UPLOADED_ID_PREFIX = "destId";
    private static final String UPLOADED_TYPE_PREFIX = "destType";

    private Option qTestCliOpt;
    private long destId;
    private String destType;

    public QTestIntegrationCommandImpl() {
        setDestinationId(-1L);
    }

    public boolean isUploadByDefault() {
        return destId <= 0 || StringUtils.isBlank(getDestinationType());
    }

    public long getDestinationId() {
        return destId;
    }

    private void setDestinationId(long destId) {
        this.destId = destId;
    }

    public String getDestinationType() {
        return destType;
    }

    @Override
    public Option getOption() {
        if (qTestCliOpt == null) {
            qTestCliOpt = new Option("qTest", true, "");
            qTestCliOpt.setValueSeparator('=');
            qTestCliOpt.setArgs(NUM_PARAMS);
        }
        return qTestCliOpt;
    }

    @Override
    public void setValues(String[] values) throws KatalonArgumentNotValidException {
        if (qTestCliOpt == null || values == null) {
            return;
        }

        if (values.length != NUM_PARAMS) {
            throw new KatalonArgumentNotValidException("qTest requires " + Integer.toString(NUM_PARAMS) + " params.\n"
                    + getPossibleArguments());
        }

        String uploadedIdParam = values[0].trim();
        if (!uploadedIdParam.startsWith(UPLOADED_ID_PREFIX + "=")) {
            throw new KatalonArgumentNotValidException("qTest: " + UPLOADED_ID_PREFIX + " param is missing.\n"
                    + getPossibleArguments());
        }
        destId = Long
                .parseLong(uploadedIdParam.substring((UPLOADED_ID_PREFIX + "=").length(), uploadedIdParam.length()));

        String uploadedTypeParam = values[1].trim();
        if (!uploadedTypeParam.startsWith(UPLOADED_TYPE_PREFIX + "=")) {
            throw new KatalonArgumentNotValidException("qTest: " + UPLOADED_TYPE_PREFIX + " param is missing.\n"
                    + getPossibleArguments());
        }
        destType = uploadedTypeParam.substring((UPLOADED_TYPE_PREFIX + "=").length(), uploadedTypeParam.length());
    }

    private String getPossibleArguments() {
        return "Possible solution: -qTest destId=<destination's id> destType=<destination's type>";
    }

}
