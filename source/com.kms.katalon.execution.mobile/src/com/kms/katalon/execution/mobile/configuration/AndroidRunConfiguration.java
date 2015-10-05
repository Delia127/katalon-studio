package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidRunConfiguration extends AbstractRunConfiguration {
    private IDriverConnector[] driverConnectors;

    public AndroidRunConfiguration(TestCaseEntity testCase, String deviceName) throws IOException {
        super(testCase);
        driverConnectors = new IDriverConnector[] { new AndroidDriverConnector(testCase.getProject()
                .getFolderLocation(), deviceName) };
    }

    public AndroidRunConfiguration(TestSuiteEntity testSuite, String deviceName) throws IOException {
        super(testSuite);
        driverConnectors = new IDriverConnector[] { new AndroidDriverConnector(testSuite.getProject()
                .getFolderLocation(), deviceName) };
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors;
    }
}
