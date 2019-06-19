package com.kms.katalon.execution.webui.configuration;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.configuration.impl.WebUIExecutionSetting;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public abstract class WebUiRunConfiguration extends AbstractRunConfiguration {
    protected WebUiDriverConnector webUiDriverConnector;
    protected String projectDir;

    public WebUiRunConfiguration(String projectDir, WebUiDriverConnector webUiDriverConnector)
            throws IOException {
        this.projectDir = projectDir;
        this.webUiDriverConnector = webUiDriverConnector;
    }
    
    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        Map<String, IDriverConnector> webUIDriverCollector = new LinkedHashMap<String, IDriverConnector>();
        webUIDriverCollector.put(DriverFactory.WEB_UI_DRIVER_PROPERTY, webUiDriverConnector);
        return webUIDriverCollector;
    }
    
    @Override
    protected void initExecutionSetting() {
        super.initExecutionSetting();
        executionSetting = new WebUIExecutionSetting();
    }
}
