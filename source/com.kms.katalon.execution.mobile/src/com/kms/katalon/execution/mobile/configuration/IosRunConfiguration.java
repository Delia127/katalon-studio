package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosRunConfiguration extends AbstractRunConfiguration {
    private IDriverConnector[] driverConnectors;

    public IosRunConfiguration(TestCaseEntity testCase, String deviceName) throws IOException {
        super(testCase);
        driverConnectors = new IDriverConnector[] { new IosDriverConnector(testCase.getProject().getFolderLocation(),
                deviceName) };
    }

    public IosRunConfiguration(TestSuiteEntity testSuite, String deviceName) throws IOException {
        super(testSuite);
        driverConnectors = new IDriverConnector[] { new IosDriverConnector(testSuite.getProject().getFolderLocation(),
                deviceName) };
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors;
    }
}
