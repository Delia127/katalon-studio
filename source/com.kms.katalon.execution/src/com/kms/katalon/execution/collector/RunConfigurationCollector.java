package com.kms.katalon.execution.collector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class RunConfigurationCollector {
    public static final String CUSTOM_EXECUTION_CONFIG_ROOT_FOLDLER_RELATIVE_PATH = PropertySettingStoreUtil.EXTERNAL_SETTING_ROOT_FOLDLER_NAME
            + File.separator + "execution";
    private static RunConfigurationCollector _instance;
    private List<IRunConfigurationContributor> runConfigurationContributors;

    private RunConfigurationCollector() {
        runConfigurationContributors = new ArrayList<IRunConfigurationContributor>();
    }

    public static RunConfigurationCollector getInstance() {
        if (_instance == null) {
            _instance = new RunConfigurationCollector();
        }
        return _instance;
    }

    public void addBuiltinRunConfigurationContributor(IRunConfigurationContributor runConfigurationContributor) {
        runConfigurationContributors.add(runConfigurationContributor);
    }

    public IRunConfiguration getRunConfiguration(String id, TestCaseEntity testCase, Map<String, String> runInput)
            throws IOException, ExecutionException {
        for (IRunConfigurationContributor runConfigurationContributor : runConfigurationContributors) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testCase, runInput);
            }
        }
        for (IRunConfigurationContributor runConfigurationContributor : getAllCustomRunConfigurationContributors()) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testCase, runInput);
            }
        }
        return null;
    }

    public IRunConfiguration getRunConfiguration(String id, TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException, ExecutionException {
        for (IRunConfigurationContributor runConfigurationContributor : runConfigurationContributors) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testSuite, runInput);
            }
        }
        for (IRunConfigurationContributor runConfigurationContributor : getAllCustomRunConfigurationContributors()) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testSuite, runInput);
            }
        }
        return null;
    }

    public IRunConfigurationContributor[] getAllBuiltinRunConfigurationContributors() {
        return runConfigurationContributors.toArray(new IRunConfigurationContributor[runConfigurationContributors
                .size()]);
    }

    public CustomRunConfigurationContributor[] getAllCustomRunConfigurationContributors() {
        List<IRunConfigurationContributor> customRunConfigContributorList = new ArrayList<IRunConfigurationContributor>();
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject != null) {
            File customProfileSettingFolder = new File(currentProject.getFolderLocation() + File.separator
                    + CUSTOM_EXECUTION_CONFIG_ROOT_FOLDLER_RELATIVE_PATH);
            if (customProfileSettingFolder.exists() && customProfileSettingFolder.isDirectory()) {
                for (File customProfile : customProfileSettingFolder.listFiles()) {
                    if (customProfile.isDirectory()) {
                        customRunConfigContributorList.add(new CustomRunConfigurationContributor(customProfile
                                .getName()));
                    }
                }
            }
        }
        return customRunConfigContributorList
                .toArray(new CustomRunConfigurationContributor[customRunConfigContributorList.size()]);
    }
}
