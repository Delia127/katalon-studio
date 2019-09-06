package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.entity.DefaultReportSetting;
import com.kms.katalon.execution.entity.DefaultRerunSetting;

public abstract class ReportableLauncherOptionParser implements LauncherOptionParser {
    protected DefaultReportSetting reportableSetting;

    protected DefaultRerunSetting rerunSetting;
    
    protected InfoOptionContributor infoOptionContributor;
    
    protected StringConsoleOption executionUUIDOption = new StringConsoleOption() {
        @Override
        public String getOption() {
            return ConsoleMain.EXECUTION_UUID_OPTION;
        }

        @Override
        public boolean isRequired() {
            return false;
        }
    };
    
    private static final String OVERRIDING_GLOBAL_VARIABLE_PREFIX = "g_";

    private List<ConsoleOption<?>> overridingOptions = new ArrayList<>();
    
    public ReportableLauncherOptionParser() {
        reportableSetting = new DefaultReportSetting();
        rerunSetting = new DefaultRerunSetting();
        infoOptionContributor = new InfoOptionContributor();
    }

    protected List<ConsoleOptionContributor> getContributors() {
        return Arrays.asList(reportableSetting, rerunSetting, infoOptionContributor);
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = new ArrayList<>();
        getContributors().forEach(contributor -> allOptions.addAll(
                contributor.getConsoleOptionList()));
        allOptions.addAll(overridingOptions);
        if (overridingOptions.isEmpty()) {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject != null) {
                overridingOptions = new OverridingParametersConsoleOptionContributor(currentProject).getConsoleOptionList();
                allOptions.addAll(overridingOptions);
            }
        }
        allOptions.add(executionUUIDOption);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
            if (consoleOption == executionUUIDOption){
                consoleOption.setValue(argumentValue);
            }
            for (ConsoleOption<?> option : overridingOptions) {
                if (option.getOption().equals(consoleOption.getOption())) {
                    option.setValue(argumentValue);
                }
            }
            for (ConsoleOptionContributor contributor : getContributors()) {
                if (contributor.getConsoleOptionList().contains(consoleOption)) {
                    contributor.setArgumentValue(consoleOption, argumentValue);
                }
            }
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
}
