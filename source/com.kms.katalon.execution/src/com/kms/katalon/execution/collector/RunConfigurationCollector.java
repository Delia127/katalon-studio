package com.kms.katalon.execution.collector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.ConsoleOption;
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

    public IRunConfigurationContributor[] getAllBuiltinRunConfigurationContributors() {
        Collections.sort(runConfigurationContributors, new Comparator<IRunConfigurationContributor>() {
            @Override
            public int compare(IRunConfigurationContributor runConfigContributor_1,
                    IRunConfigurationContributor runConfigContributor_2) {
                return runConfigContributor_1.getPreferredOrder() - runConfigContributor_2.getPreferredOrder();
            }
        });
        return runConfigurationContributors.toArray(new IRunConfigurationContributor[runConfigurationContributors
                .size()]);
    }

    public List<ConsoleOption<?>> getAllAddionalRequiredArguments() {
        List<ConsoleOption<?>> additionalArgumentList = new ArrayList<ConsoleOption<?>>();
        for (IRunConfigurationContributor runConfigContributor : runConfigurationContributors) {
            additionalArgumentList.addAll(runConfigContributor.getRequiredArguments());
        }
        return additionalArgumentList;
    }

    public CustomRunConfigurationContributor[] getAllCustomRunConfigurationContributors() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return new CustomRunConfigurationContributor[0];
        }

        File customProfileSettingFolder = new File(currentProject.getFolderLocation() + File.separator
                + CUSTOM_EXECUTION_CONFIG_ROOT_FOLDLER_RELATIVE_PATH);
        if (!customProfileSettingFolder.exists() || !customProfileSettingFolder.isDirectory()) {
            return new CustomRunConfigurationContributor[0];
        }

        List<IRunConfigurationContributor> customRunConfigContributorList = new ArrayList<IRunConfigurationContributor>();
        for (File customProfile : customProfileSettingFolder.listFiles()) {
            if (customProfile.isDirectory() && !customProfile.isHidden()) {
                customRunConfigContributorList.add(new CustomRunConfigurationContributor(customProfile.getName()));
            }
        }
        return customRunConfigContributorList
                .toArray(new CustomRunConfigurationContributor[customRunConfigContributorList.size()]);
    }

    public IRunConfiguration getRunConfiguration(String id, String projectDir) throws IOException, ExecutionException,
            InterruptedException {
        for (IRunConfigurationContributor builtinRunConfigurationContributor : getAllBuiltinRunConfigurationContributors()) {
            if (builtinRunConfigurationContributor.getId().equals(id)) {
                return builtinRunConfigurationContributor.getRunConfiguration(projectDir);
            }
        }
        for (IRunConfigurationContributor customRunConfigurationContributor : getAllCustomRunConfigurationContributors()) {
            if (customRunConfigurationContributor.getId().equals(id)) {
                return customRunConfigurationContributor.getRunConfiguration(projectDir);
            }
        }
        return null;
    }
}
