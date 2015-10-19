package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebRunConfiguration extends WebUiRunConfiguration {
    public RemoteWebRunConfiguration(TestCaseEntity testCase) throws IOException {
        super(testCase, new RemoteWebDriverConnector(testCase.getProject().getFolderLocation() + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }

    public RemoteWebRunConfiguration(TestSuiteEntity testSuite) throws IOException {
        super(testSuite, new RemoteWebDriverConnector(testSuite.getProject().getFolderLocation() + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }

    @Override
    public String getName() {
        return super.getName() + " - " + ((RemoteWebDriverConnector) webUiDriverConnector).getRemoteServerUrl();
    }
    
    public String getRemoteServerUrl() {
        return ((RemoteWebDriverConnector) webUiDriverConnector).getRemoteServerUrl();
    }

    public void setRemoteServerUrl(String remoteServerUrl) {
        ((RemoteWebDriverConnector) webUiDriverConnector).setRemoteServerUrl(remoteServerUrl);
    }
    
    public RemoteWebDriverConnectorType getRemoteWebDriverConnectorType() {
        return ((RemoteWebDriverConnector) webUiDriverConnector).getRemoteWebDriverConnectorType();
    }

    public void setRemoteWebDriverConnectorType(RemoteWebDriverConnectorType remoteWebDriverConnectorType) {
        ((RemoteWebDriverConnector) webUiDriverConnector).setRemoteWebDriverConnectorType(remoteWebDriverConnectorType);
    }
}
