package com.kms.katalon.execution.collector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.CustomRunConfigurationContributor;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class RunConfigurationCollector {
    public static final String CUSTOM_EXECUTION_CONFIG_ROOT_FOLDER_RELATIVE_PATH = PropertySettingStoreUtil.EXTERNAL_SETTING_ROOT_FOLDER_NAME
            + File.separator + "execution";

    private static RunConfigurationCollector _instance;

    private Map<String, IRunConfigurationContributor> runConfigurationContributors;

    private RunConfigurationCollector() {
        runConfigurationContributors = new HashMap<>();
    }

    public static RunConfigurationCollector getInstance() {
        if (_instance == null) {
            _instance = new RunConfigurationCollector();
        }
        return _instance;
    }

    public void addBuiltinRunConfigurationContributor(IRunConfigurationContributor runConfigurationContributor) {
        runConfigurationContributors.put(runConfigurationContributor.getId(), runConfigurationContributor);
    }

    private List<IRunConfigurationContributor> getListRunContributors() {
        List<IRunConfigurationContributor> contributors = new ArrayList<>();
        for (Entry<String, IRunConfigurationContributor> entry : runConfigurationContributors.entrySet()) {
            contributors.add(entry.getValue());
        }
        return contributors;
    }

    public IRunConfigurationContributor[] getAllBuiltinRunConfigurationContributors() {
        List<IRunConfigurationContributor> lstContributors = getListRunContributors();
        Collections.sort(lstContributors, new Comparator<IRunConfigurationContributor>() {
            @Override
            public int compare(IRunConfigurationContributor runConfigContributor_1,
                    IRunConfigurationContributor runConfigContributor_2) {
                return runConfigContributor_1.getPreferredOrder() - runConfigContributor_2.getPreferredOrder();
            }
        });
        return lstContributors.toArray(new IRunConfigurationContributor[lstContributors.size()]);
    }

    public List<ConsoleOptionContributor> getConsoleOptionContributorList() {
        return new ArrayList<ConsoleOptionContributor>(getListRunContributors());
    }

    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> additionalArgumentList = new ArrayList<ConsoleOption<?>>();
        for (IRunConfigurationContributor runConfigContributor : getListRunContributors()) {
            additionalArgumentList.addAll(runConfigContributor.getConsoleOptionList());
        }
        return additionalArgumentList;
    }

    public CustomRunConfigurationContributor[] getAllCustomRunConfigurationContributors() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return new CustomRunConfigurationContributor[0];
        }

        File customProfileSettingFolder = new File(currentProject.getFolderLocation() + File.separator
                + CUSTOM_EXECUTION_CONFIG_ROOT_FOLDER_RELATIVE_PATH);
        if (!customProfileSettingFolder.exists() || !customProfileSettingFolder.isDirectory()) {
            return new CustomRunConfigurationContributor[0];
        }

        List<IRunConfigurationContributor> customRunConfigContributorList = new ArrayList<IRunConfigurationContributor>();
        for (File customProfile : customProfileSettingFolder.listFiles()) {
            if (customProfile.isDirectory() && !customProfile.isHidden()) {
                customRunConfigContributorList.add(new CustomRunConfigurationContributor(customProfile.getName()));
            }
        }
        return customRunConfigContributorList.toArray(new CustomRunConfigurationContributor[customRunConfigContributorList.size()]);
    }

    public IRunConfiguration getRunConfiguration(String id, String projectDir) throws ExecutionException {
        try {
            if (runConfigurationContributors.containsKey(id)) {
                return runConfigurationContributors.get(id).getRunConfiguration(projectDir);
            }

            for (IRunConfigurationContributor customRunConfigurationContributor : getAllCustomRunConfigurationContributors()) {
                if (customRunConfigurationContributor.getId().equals(id)) {
                    return customRunConfigurationContributor.getRunConfiguration(projectDir);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new ExecutionException(e);
        }
        return null;
    }

    public IRunConfiguration getRunConfiguration(String id, String projectDir,
            RunConfigurationDescription runConfigurationDescription) throws IOException, ExecutionException,
            InterruptedException {
        if (runConfigurationContributors.containsKey(id)) {
            return runConfigurationContributors.get(id).getRunConfiguration(projectDir, runConfigurationDescription);
        }

        for (IRunConfigurationContributor customRunConfigurationContributor : getAllCustomRunConfigurationContributors()) {
            if (customRunConfigurationContributor.getId().equals(id)) {
                return customRunConfigurationContributor.getRunConfiguration(projectDir, runConfigurationDescription);
            }
        }
        return null;
    }

    public IRunConfigurationContributor getRunContributor(String id) {
        if (runConfigurationContributors.containsKey(id)) {
            return runConfigurationContributors.get(id);
        }
        for (IRunConfigurationContributor customRunConfigurationContributor : getAllCustomRunConfigurationContributors()) {
            if (customRunConfigurationContributor.getId().equals(id)) {
                return customRunConfigurationContributor;
            }
        }
        return null;
    }

    /**
     * @return String[] Custom run configuration IDs (names)
     */
    public String[] getAllCustomRunConfigurationIds() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return new String[0];
        }

        File customProfileSettingFolder = new File(currentProject.getFolderLocation() + File.separator
                + CUSTOM_EXECUTION_CONFIG_ROOT_FOLDER_RELATIVE_PATH);
        if (!customProfileSettingFolder.exists() || !customProfileSettingFolder.isDirectory()) {
            return new String[0];
        }

        List<String> customRunConfigIdList = new ArrayList<String>();
        for (File customProfile : customProfileSettingFolder.listFiles()) {
            if (customProfile.isDirectory() && !customProfile.isHidden()) {
                customRunConfigIdList.add(customProfile.getName());
            }
        }
        return customRunConfigIdList.toArray(new String[customRunConfigIdList.size()]);
    }

   
}
