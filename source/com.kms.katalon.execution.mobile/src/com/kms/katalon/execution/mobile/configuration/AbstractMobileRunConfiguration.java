package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;

public abstract class AbstractMobileRunConfiguration extends AbstractRunConfiguration {
    protected IDriverConnector[] driverConnectors;
    protected String deviceName;

    public AbstractMobileRunConfiguration(TestCaseEntity testCaseEntity) throws IOException {
        super(testCaseEntity);
    }

    public AbstractMobileRunConfiguration(TestSuiteEntity testSuiteEntity) throws IOException {
        super(testSuiteEntity);
    }
    
    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors;
    }

    @Override
    public String getName() {
        return super.getName() + " - " + deviceName;
    }
}
