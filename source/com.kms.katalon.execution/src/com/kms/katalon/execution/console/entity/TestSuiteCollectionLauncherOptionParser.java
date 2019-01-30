package com.kms.katalon.execution.console.entity;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.constants.ExecutionMessageConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionConsoleLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class TestSuiteCollectionLauncherOptionParser extends ReportableLauncherOptionParser {

    private ConsoleOption<String> testSuiteCollectionOption = new StringConsoleOption() {

        @Override
        public String getOption() {
            return ConsoleMain.TESTSUITE_COLLECTION_ID_OPTION;
        }
        
        public boolean isRequired() {
            return true;
        };
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
        allOptions.add(testSuiteCollectionOption);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        super.setArgumentValue(consoleOption, argumentValue);
        if (consoleOption == testSuiteCollectionOption) {
            consoleOption.setValue(argumentValue);
        }
    }

    @Override
    public IConsoleLauncher getConsoleLauncher(ProjectEntity projectEntity, LauncherManager manager)
            throws ExecutionException, InvalidConsoleArgumentException, DALException {
        TestSuiteCollectionEntity testSuiteCollection = getTestSuiteCollection(projectEntity, testSuiteCollectionOption.getValue());
        TestSuiteCollectionExecutedEntity executedEntity = new TestSuiteCollectionExecutedEntity(testSuiteCollection);
        executedEntity.setReportable(reportableSetting);;
       // executedEntity.setEmailConfig(reportableSetting.getEmailConfig(projectEntity));
        executedEntity.setRerunable(rerunSetting);
        AbstractRunConfiguration runConfig = (AbstractRunConfiguration) createRunConfiguration(projectEntity, testSuiteCollection,
                browserTypeOption.getValue());
        
        String profileName = testSuiteCollectionOption.getValue();
        if (StringUtils.isBlank(profileName)) {
            profileName = ExecutionProfileEntity.DF_PROFILE_NAME;
        }
        ExecutionProfileEntity executionProfile = GlobalVariableController.getInstance()
                .getExecutionProfile(profileName, projectEntity);
        if (executionProfile == null) {
            throw new ExecutionException(
                    MessageFormat.format(ExecutionMessageConstants.CONSOLE_MSG_PROFILE_NOT_FOUND, profileName));
        }
        runConfig.setExecutionProfile(executionProfile);
        runConfig.setOverridingGlobalVariables(super.getOverridingGlobalVariables());
        try {
            runConfig.build(testSuiteCollection, executedEntity);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            GlobalVariableController.getInstance().
            generateGlobalVariableLibFileWithSpecificProfile(projectEntity, executionProfile, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return TestSuiteCollectionConsoleLauncher.newInstance(testSuiteCollection, manager, reportableSetting,
                rerunSetting);
    }

    private IRunConfiguration createRunConfiguration(ProjectEntity projectEntity, TestSuiteCollectionEntity testSuiteCollection,
            String browserType) throws ExecutionException, InvalidConsoleArgumentException {
        IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(browserType,
                projectEntity.getFolderLocation());

        if (runConfig == null) {
            throw new InvalidConsoleArgumentException(
                    MessageFormat.format(StringConstants.MNG_PRT_INVALID_BROWSER_X, browserType));
        }
        return runConfig;
    }

    private TestSuiteCollectionEntity getTestSuiteCollection(ProjectEntity projectEntity, String testSuiteCollectionID)
            throws InvalidConsoleArgumentException {
        try {
            TestSuiteCollectionEntity testSuite = TestSuiteCollectionController.getInstance()
                    .getTestRunByDisplayId(testSuiteCollectionID);

            if (testSuite == null) {
                throw createInvalidTestSuiteCollectionIdException(testSuiteCollectionID);
            }

            return testSuite;
        } catch (DALException e) {
            throw createInvalidTestSuiteCollectionIdException(testSuiteCollectionID);
        }
    }

    private InvalidConsoleArgumentException createInvalidTestSuiteCollectionIdException(String testSuiteCollectionID)
            throws InvalidConsoleArgumentException {
        return new InvalidConsoleArgumentException(
                MessageFormat.format(StringConstants.MNG_PRT_TEST_SUITE_X_NOT_FOUND, testSuiteCollectionID));
    }
}
