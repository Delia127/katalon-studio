package com.kms.katalon.execution.webui.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;

public class FirefoxRunConfiguration extends AbstractRunConfiguration {
    IDriverConnector[] driverConnectors;

    public FirefoxRunConfiguration(TestCaseEntity testCase) throws IOException {
        super(testCase);
        driverConnectors = new IDriverConnector[] { new FirefoxDriverConnector(testCase.getProject()
                .getFolderLocation()) };
    }

    public FirefoxRunConfiguration(TestSuiteEntity testSuite) throws IOException {
        super(testSuite);
        driverConnectors = new IDriverConnector[] { new FirefoxDriverConnector(testSuite.getProject()
                .getFolderLocation()) };
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors;
    }

}
