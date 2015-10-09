package com.kms.katalon.execution.mobile.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidRunConfiguration extends MobileRunConfiguration {
    public AndroidRunConfiguration(TestCaseEntity testCase) throws IOException {
        super(testCase, new AndroidDriverConnector(testCase.getProject().getFolderLocation() + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }

    public AndroidRunConfiguration(TestSuiteEntity testSuite) throws IOException {
        super(testSuite, new AndroidDriverConnector(testSuite.getProject().getFolderLocation() + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }

}
