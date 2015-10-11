package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.execution.configuration.AbstractDriverConnector;

public abstract class WebUiDriverConnector extends AbstractDriverConnector {

    public WebUiDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }

    @Override
    public String getSettingFileName() {
        return StringConstants.WEB_UI_PROPERTY_FILE_NAME + "." + getDriverType().toString().toLowerCase();
    }

}
