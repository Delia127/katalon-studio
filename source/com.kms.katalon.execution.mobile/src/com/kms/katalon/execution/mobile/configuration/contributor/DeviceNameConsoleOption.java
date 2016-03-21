package com.kms.katalon.execution.mobile.configuration.contributor;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.execution.entity.StringConsoleOption;

public class DeviceNameConsoleOption extends StringConsoleOption {
    private String name = "";

    public String getName() {
        return name;
    }

    @Override
    public void setArgumentValue(String value) {
        if (value == null) {
            return;
        }
        name = value.trim();
    }

    @Override
    public String getOption() {
        return StringConstants.CONF_EXECUTED_DEVICE_ID;
    }

}
