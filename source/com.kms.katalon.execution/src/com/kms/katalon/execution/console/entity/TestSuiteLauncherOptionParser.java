package com.kms.katalon.execution.console.entity;

import java.text.MessageFormat;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.ConsoleLauncher;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class TestSuiteLauncherOptionParser extends ReportableLauncherOptionParser {
    private static final String EXECUTION_PROFILE_OPTION = "executionProfile";
    
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

    private StringConsoleOption executionProfileOption = new StringConsoleOption() {
		@Override
		public String getOption() {
			return EXECUTION_PROFILE_OPTION;
		}
		
    	public boolean isRequired() {
            return false;
        }

        @Override
        public String getDefaultArgumentValue() {
            return ExecutionProfileEntity.DF_PROFILE_NAME;
        }
    };

    
    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = super.getConsoleOptionList();
        allOptions.add(testSuitePathOption);
        allOptions.add(browserTypeOption);
        allOptions.add(executionProfileOption);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
		super.setArgumentValue(consoleOption, argumentValue);
		if (consoleOption == testSuitePathOption || consoleOption == browserTypeOption
				|| consoleOption == executionProfileOption) {
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
            executedEntity.prepareTestCases();
            AbstractRunConfiguration runConfig = (AbstractRunConfiguration) createRunConfiguration(project, testSuite,
                    browserTypeOption.getValue());
            
            String profileName = executionProfileOption.getValue();
            if (StringUtils.isBlank(profileName)) {
                profileName = ExecutionProfileEntity.DF_PROFILE_NAME;
            }
            ExecutionProfileEntity executionProfile = GlobalVariableController.getInstance()
                    .getExecutionProfile(profileName, project);
            if (executionProfile == null) {
                throw new ExecutionException(
                        MessageFormat.format(ExecutionMessageConstants.CONSOLE_MSG_PROFILE_NOT_FOUND, profileName));
            }
            runConfig.setExecutionProfile(executionProfile);
            runConfig.setOverridingGlobalVariables(super.getOverridingGlobalVariables());
            runConfig.build(testSuite, executedEntity);
            GlobalVariableController.getInstance().
            generateGlobalVariableLibFileWithSpecificProfile(project, executionProfile, null);
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
