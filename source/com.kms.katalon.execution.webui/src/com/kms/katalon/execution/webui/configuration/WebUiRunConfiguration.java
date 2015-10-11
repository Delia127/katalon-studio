package com.kms.katalon.execution.webui.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public abstract class WebUiRunConfiguration extends AbstractRunConfiguration {
    protected WebUiDriverConnector webUiDriverConnector;

    public WebUiRunConfiguration(TestCaseEntity testCaseEntity, WebUiDriverConnector webUiDriverConnector)
            throws IOException {
        super(testCaseEntity);
        this.webUiDriverConnector = webUiDriverConnector;
    }

    public WebUiRunConfiguration(TestSuiteEntity testSuiteEntity, WebUiDriverConnector webUiDriverConnector)
            throws IOException {
        super(testSuiteEntity);
        this.webUiDriverConnector = webUiDriverConnector;
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return new IDriverConnector[] { webUiDriverConnector };
    }

}
