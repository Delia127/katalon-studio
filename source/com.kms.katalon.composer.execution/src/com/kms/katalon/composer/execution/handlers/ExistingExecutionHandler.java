package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.commands.ParameterizedCommand;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.ExistingRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class ExistingExecutionHandler extends AbstractExecutionHandler {
    private String sessionId;

    private String remoteServerUrl;

    private String driverTypeName;

    private ExistingRunConfiguration existingRunConfig;
    
    public ExistingExecutionHandler() {
    }

    @Override
    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        return new ExistingRunConfiguration(projectDir);
    }

    @Override
    public void execute(ParameterizedCommand command) {
        sessionId = getSessionId(command);
        remoteServerUrl = getServerUrl(command);
        driverTypeName = getDriverTypeName(command);
        super.execute(command);
    }

    private String getSessionId(ParameterizedCommand command) {
        return getParameter(command, IdConstants.EXISTING_SESSION_SESSION_ID_ID);
    }

    protected String getParameter(ParameterizedCommand command, String parameterId) {
        return ObjectUtils.toString(command.getParameterMap().get(parameterId));
    }

    protected String getServerUrl(ParameterizedCommand command) {
        return getParameter(command, IdConstants.EXISTING_SESSION_SERVER_URL_ID);
    }

    protected String getDriverTypeName(ParameterizedCommand command) {
        return getParameter(command, IdConstants.EXISTING_SESSION_DRIVER_NAME_ID);
    }

    @Override
    public void executeTestCase(TestCaseEntity testCase, LaunchMode launchMode)
            throws Exception {
        super.executeTestCase(testCase, launchMode);
    }

    @Override
    public void executeTestSuite(TestSuiteEntity testSuite, LaunchMode launchMode)
            throws Exception {
        super.executeTestSuite(testSuite, launchMode);
    }

    public ExistingRunConfiguration getExistingRunConfig() {
        return existingRunConfig;
    }

    public void setExistingRunConfig(ExistingRunConfiguration existingRunConfig) {
        this.existingRunConfig = existingRunConfig;
    }

    @Override
    public AbstractRunConfiguration buildRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        ExistingRunConfiguration existingRunConfiguration = new ExistingRunConfiguration(projectDir);
        existingRunConfiguration.setSessionId(sessionId);
        existingRunConfiguration.setRemoteUrl(remoteServerUrl);
        existingRunConfiguration.setDriverName(driverTypeName);
        if (existingRunConfig != null) {
            ((DefaultExecutionSetting) existingRunConfiguration.getExecutionSetting())
            .setRawScript(existingRunConfig.getExecutionSetting().getRawScript());
        }
        return existingRunConfiguration;
    }
}
