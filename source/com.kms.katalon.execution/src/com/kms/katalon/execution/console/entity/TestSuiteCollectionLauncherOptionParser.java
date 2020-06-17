package com.kms.katalon.execution.console.entity;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.ILauncher;
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
            return false;
        }
    };

    private StringConsoleOption executionProfileOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return TestSuiteLauncherOptionParser.EXECUTION_PROFILE_OPTION;
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
        allOptions.add(testSuiteCollectionOption);
        allOptions.add(browserTypeOption);
        allOptions.add(executionProfileOption);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        super.setArgumentValue(consoleOption, argumentValue);
        if (consoleOption == testSuiteCollectionOption || consoleOption == browserTypeOption
                || consoleOption == executionProfileOption) {
            consoleOption.setValue(argumentValue);
        }
    }

    @Override
    public IConsoleLauncher getConsoleLauncher(ProjectEntity projectEntity, LauncherManager manager)
            throws ExecutionException, InvalidConsoleArgumentException, ControllerException {
        TestSuiteCollectionEntity testSuiteCollection = getTestSuiteCollection(projectEntity,
                testSuiteCollectionOption.getValue());
        if (browserTypeOption.getValue() != null) {
            testSuiteCollection.setBrowserType(browserTypeOption.getValue());
        }
        if (executionProfileOption.getValue() != null) {
            testSuiteCollection.setProfileName(executionProfileOption.getValue());
        }
        Map<String, Object> globalVariables = super.getOverridingGlobalVariables();
        Map<String, String> additionalInfo = infoOptionContributor.getOptionValues();
        return TestSuiteCollectionConsoleLauncher.newInstance(testSuiteCollection, manager, reportableSetting,
                rerunSetting, globalVariables, executionUUIDOption.getValue(), additionalInfo);
    }

    @Override
    public ILauncher getIDELauncher(ProjectEntity projectEntity, LauncherManager manager)
            throws ExecutionException, InvalidConsoleArgumentException, ControllerException {
        TestSuiteCollectionEntity testSuiteCollection = getTestSuiteCollection(projectEntity,
                testSuiteCollectionOption.getValue());
        Map<String, Object> globalVariables = super.getOverridingGlobalVariables();
        Map<String, String> additionalInfo = infoOptionContributor.getOptionValues();
        return TestSuiteCollectionConsoleLauncher.newIDEInstance(testSuiteCollection, manager, reportableSetting,
                rerunSetting, globalVariables, executionUUIDOption.getValue(), additionalInfo);
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
