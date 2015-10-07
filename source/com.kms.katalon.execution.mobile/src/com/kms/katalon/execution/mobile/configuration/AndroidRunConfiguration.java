package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidRunConfiguration extends AbstractMobileRunConfiguration {
    public AndroidRunConfiguration(TestCaseEntity testCase) throws IOException {
        super(testCase);
        initDriverConnector(testCase);
    }

    public AndroidRunConfiguration(TestSuiteEntity testSuite) throws IOException {
        super(testSuite);
        initDriverConnector(testSuite);
    }

    public AndroidRunConfiguration(TestCaseEntity testCase, String deviceName) throws IOException {
        super(testCase);
        initDriverConnector(testCase, deviceName);
    }

    public AndroidRunConfiguration(TestSuiteEntity testSuite, String deviceName) throws IOException {
        super(testSuite);
        initDriverConnector(testSuite, deviceName);
    }

    private void initDriverConnector(FileEntity fileEntity) throws IOException {
        AndroidDriverConnector driverConnector = new AndroidDriverConnector(fileEntity.getProject().getFolderLocation());
        driverConnectors = new IDriverConnector[] { driverConnector };
        deviceName = driverConnector.getDeviceName();
    }

    private void initDriverConnector(FileEntity fileEntity, String deviceName) throws IOException {
        this.deviceName = deviceName;
        AndroidDriverConnector driverConnector = new AndroidDriverConnector(fileEntity.getProject().getFolderLocation());
        driverConnectors = new IDriverConnector[] { driverConnector };
    }
}
