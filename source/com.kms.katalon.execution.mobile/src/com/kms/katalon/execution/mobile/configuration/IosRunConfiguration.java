package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosRunConfiguration extends AbstractMobileRunConfiguration {
    public IosRunConfiguration(TestCaseEntity testCase) throws IOException {
        super(testCase);
        initDriverConnector(testCase);
    }

    public IosRunConfiguration(TestSuiteEntity testSuite) throws IOException {
        super(testSuite);
        initDriverConnector(testSuite);
    }

    public IosRunConfiguration(TestCaseEntity testCase, String deviceName) throws IOException {
        super(testCase);
        initDriverConnector(testCase, deviceName);
    }

    public IosRunConfiguration(TestSuiteEntity testSuite, String deviceName) throws IOException {
        super(testSuite);
        initDriverConnector(testSuite, deviceName);
    }

    private void initDriverConnector(FileEntity fileEntity) throws IOException {
        IosDriverConnector driverConnector = new IosDriverConnector(fileEntity.getProject().getFolderLocation());
        driverConnectors = new IDriverConnector[] { driverConnector };
        deviceName = driverConnector.getDeviceName();
    }

    private void initDriverConnector(FileEntity fileEntity, String deviceName) throws IOException {
        this.deviceName = deviceName;
        IosDriverConnector driverConnector = new IosDriverConnector(fileEntity.getProject().getFolderLocation());
        driverConnectors = new IDriverConnector[] { driverConnector };
    }
}
