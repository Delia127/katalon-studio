package com.kms.katalon.execution.webui.driver;

import java.io.IOException;

import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.execution.entity.AbstractDriverConnector;

public abstract class WebUiDriverConnector extends AbstractDriverConnector {

    public WebUiDriverConnector(String projectDir) throws IOException {
        super(projectDir);
    }
    
    public WebUiDriverConnector(String projectDir, String customProfileName) throws IOException {
        super(projectDir, customProfileName);
    }

    @Override
    protected String getSettingFileName() {
        return StringConstants.WEB_UI_PROPERTY_FILE_NAME;
    }

}
