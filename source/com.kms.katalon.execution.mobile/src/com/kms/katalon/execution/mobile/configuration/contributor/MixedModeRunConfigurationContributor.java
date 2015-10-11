package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.MixedModeRunConfiguration;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class MixedModeRunConfigurationContributor implements IRunConfigurationContributor {

    @Override
    public String getId() {
        return "Mixed mode";
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput)
            throws IOException, ExecutionException {
        if (runInput == null
                || runInput.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM) == null
                || runInput.get(com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER) == null) {
            return null;
        }
        String devicePlatform = runInput
                .get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM);
        String browserType = runInput
                .get(com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER);
        String projectFolderLocation = testCase.getProject().getFolderLocation();
        return new MixedModeRunConfiguration(testCase, MobileExecutionUtil.getMobileDriverConnector(
                MobileDriverType.fromStringValue(devicePlatform), projectFolderLocation),
                WebUIExecutionUtil.getBrowserDriverConnector(WebUIDriverType.fromStringValue(browserType),
                        projectFolderLocation));
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException, ExecutionException {
        if (runInput == null
                || runInput.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM) == null
                || runInput.get(com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER) == null) {
            return null;
        }
        String devicePlatform = runInput
                .get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM);
        String browserType = runInput
                .get(com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER);
        String projectFolderLocation = testSuite.getProject().getFolderLocation();
        return new MixedModeRunConfiguration(testSuite, MobileExecutionUtil.getMobileDriverConnector(
                MobileDriverType.fromStringValue(devicePlatform), projectFolderLocation),
                WebUIExecutionUtil.getBrowserDriverConnector(WebUIDriverType.fromStringValue(browserType),
                        projectFolderLocation));
    }

}
