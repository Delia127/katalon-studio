package com.kms.katalon.execution.console.entity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.InvalidConsoleArgumentException;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionConsoleLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class TestSuiteCollectionLauncherOptionParser extends ReportableLauncherOptionParser {
    private static final String OVERRIDING_GLOBAL_VARIABLE_PREFIX = "g_";

    private List<ConsoleOption<?>> overridingOptions = new ArrayList<>();

    protected StringConsoleOption browserTypeOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.BROWSER_TYPE_OPTION;
        }

        public boolean isRequired() {
            return true;
        }
    };
    
    private ConsoleOption<String> testSuiteCollectionOption = new StringConsoleOption() {

        @Override
        public String getOption() {
            return ConsoleMain.TESTSUITE_COLLECTION_ID_OPTION;
        }

        public boolean isRequired() {
            return true;
        };
    };

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = super.getConsoleOptionList();
        allOptions.add(testSuiteCollectionOption);
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject != null && overridingOptions.isEmpty()) {
            overridingOptions = new OverridingParametersConsoleOptionContributor(currentProject).getConsoleOptionList();
        }
        allOptions.addAll(overridingOptions);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        for (ConsoleOption<?> overridingOption : overridingOptions) {
            if (overridingOption.getOption().equals(consoleOption.getOption())) {
                overridingOption.setValue(argumentValue);
                return;
            }
        }
        super.setArgumentValue(consoleOption, argumentValue);
        if (consoleOption == testSuiteCollectionOption) {
            consoleOption.setValue(argumentValue);
        }
    }

    @Override
    public IConsoleLauncher getConsoleLauncher(ProjectEntity projectEntity, LauncherManager manager)
            throws ExecutionException, InvalidConsoleArgumentException {
        TestSuiteCollectionEntity testSuiteCollection = getTestSuiteCollection(projectEntity, testSuiteCollectionOption.getValue());
        Map<String,Object> overridingVariables = getOverridingGlobalVariables();
        return TestSuiteCollectionConsoleLauncher.newInstance(testSuiteCollection, manager, reportableSetting,
                rerunSetting, overridingVariables);
    }


    public Map<String, Object> getOverridingGlobalVariables(){
        Map<String, Object> overridingGlobalVariables = new HashMap<>();
        overridingOptions.forEach(a -> {
            if (a.getOption().startsWith(OVERRIDING_GLOBAL_VARIABLE_PREFIX) 
                    && a.getValue() != null) {
                overridingGlobalVariables.put(a.getOption().
                        replace(OVERRIDING_GLOBAL_VARIABLE_PREFIX, ""),
                        String.valueOf(a.getValue()));
            }
        });
        return overridingGlobalVariables;
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
