package com.kms.katalon.execution.console.entity;

import java.text.MessageFormat;
import java.util.List;

import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class TestSuiteLauncherOptionParser extends ReportableLauncherOptionParser {
    private StringConsoleOption testSuitePathOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.TESTSUITE_ID_OPTION;
        }
        
        public boolean isRequired() {
            return true;
        }
    };

    private StringConsoleOption browserTypeOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.BROWSER_TYPE_OPTION;
        }
        
        public boolean isRequired() {
            return true;
        }
    };

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = super.getConsoleOptionList();
        allOptions.add(testSuitePathOption);
        allOptions.add(browserTypeOption);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        super.setArgumentValue(consoleOption, argumentValue);
        if (consoleOption == testSuitePathOption || consoleOption == browserTypeOption) {
            consoleOption.setValue(argumentValue);
            return;
        }
    }

    @Override
    public IConsoleLauncher getConsoleLauncher(ProjectEntity project, LauncherManager manager)
            throws InvalidConsoleArgumentException, ExecutionException {
        try {
            TestSuiteEntity testSuite = getTestSuite(project, testSuitePathOption.getValue());
            TestSuiteExecutedEntity executedEntity = new TestSuiteExecutedEntity(testSuite);
            executedEntity.setReportLocation(reportableSetting.getReportLocationSetting());
            executedEntity.setEmailConfig(reportableSetting.getEmailConfig(project));
            executedEntity.setRerunSetting(rerunSetting);
            IRunConfiguration runConfig = createRunConfiguration(project, testSuite, browserTypeOption.getValue());
            runConfig.build(testSuite, executedEntity);
            return new ConsoleLauncher(manager, runConfig);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    private IRunConfiguration createRunConfiguration(ProjectEntity projectEntity, TestSuiteEntity testSuite,
            String browserType) throws ExecutionException, InvalidConsoleArgumentException {
        IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(browserType,
                projectEntity.getFolderLocation());

        if (runConfig == null) {
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(StringConstants.MNG_PRT_INVALID_BROWSER_X, browserType));
        }
        return runConfig;
    }

    private static TestSuiteEntity getTestSuite(ProjectEntity projectEntity, String testSuiteID)
            throws InvalidConsoleArgumentException {
        try {
            TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteID,
                    projectEntity);

            if (testSuite == null) {
                throw throwInvalidTestSuiteIdException(testSuiteID);
            }

            return testSuite;
        } catch (Exception e) {
            throw throwInvalidTestSuiteIdException(testSuiteID);
        }
    }

    private static InvalidConsoleArgumentException throwInvalidTestSuiteIdException(String testSuiteID) {
        return new InvalidConsoleArgumentException(
                MessageFormat.format(StringConstants.MNG_PRT_TEST_SUITE_X_NOT_FOUND, testSuiteID));
    }
}
