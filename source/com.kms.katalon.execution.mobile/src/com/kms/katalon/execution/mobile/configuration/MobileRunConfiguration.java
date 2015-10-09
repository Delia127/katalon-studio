package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public abstract class MobileRunConfiguration extends AbstractRunConfiguration {
    protected MobileDriverConnector mobileDriverConnector;

    public MobileRunConfiguration(TestCaseEntity testCaseEntity, MobileDriverConnector mobileDriverConnector)
            throws IOException {
        super(testCaseEntity);
        this.mobileDriverConnector = mobileDriverConnector;
    }

    public MobileRunConfiguration(TestSuiteEntity testSuiteEntity, MobileDriverConnector mobileDriverConnector)
            throws IOException {
        super(testSuiteEntity);
        this.mobileDriverConnector = mobileDriverConnector;
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return new IDriverConnector[] { mobileDriverConnector };
    }

    @Override
    public String getName() {
        return super.getName() + " - " + mobileDriverConnector.getDeviceName();
    }

    public void setDeviceName(String deviceName) {
        mobileDriverConnector.setDeviceName(deviceName);
    }
    
    public String getDeviceName() {
        return mobileDriverConnector.getDeviceName();
    }
}
