package com.kms.katalon.execution.webui.configuration;

import java.io.IOException;

import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;

public class RemoteWebRunConfiguration extends AbstractRunConfiguration {
    IDriverConnector[] driverConnectors;
    private String remoteWebServerUrl;
    
    public RemoteWebRunConfiguration(TestCaseEntity testCase) throws IOException {
        super(testCase);
        initDriverConnector(testCase);
    }

    public RemoteWebRunConfiguration(TestSuiteEntity testSuite) throws IOException {
        super(testSuite);
        initDriverConnector(testSuite);
    }

    public RemoteWebRunConfiguration(TestCaseEntity testCase, String remoteWebServerUrl) throws IOException {
        super(testCase);
        initDriverConnector(testCase, remoteWebServerUrl);
    }

    public RemoteWebRunConfiguration(TestSuiteEntity testSuite, String remoteWebServerUrl) throws IOException {
        super(testSuite);
        initDriverConnector(testSuite, remoteWebServerUrl);
    }
    
    private void initDriverConnector(FileEntity fileEntity) throws IOException {
        RemoteWebDriverConnector remoteWebDriverConnector = new RemoteWebDriverConnector(fileEntity.getProject()
                .getFolderLocation());
        driverConnectors = new IDriverConnector[] { remoteWebDriverConnector };
        this.remoteWebServerUrl = remoteWebDriverConnector.getRemoteServerUrl();
    }
    
    private void initDriverConnector(FileEntity fileEntity, String remoteWebServerUrl) throws IOException {
        RemoteWebDriverConnector remoteWebDriverConnector = new RemoteWebDriverConnector(fileEntity.getProject()
                .getFolderLocation());
        remoteWebDriverConnector.setRemoteServerUrl(remoteWebServerUrl);
        driverConnectors = new IDriverConnector[] { remoteWebDriverConnector };
        this.remoteWebServerUrl = remoteWebServerUrl;
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors;
    }

    @Override
    public String getName() {
        return super.getName() + " - " + remoteWebServerUrl;
    }

}
