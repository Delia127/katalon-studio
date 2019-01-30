package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.entity.DefaultReportSetting;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.exception.ExecutionException;

public abstract class ReportableLauncherOptionParser implements LauncherOptionParser {
    protected DefaultReportSetting reportableSetting;

    protected DefaultRerunSetting rerunSetting;
    
    private static final String OVERRIDING_GLOBAL_VARIABLE_PREFIX = "g_";

    private List<ConsoleOption<?>> overridingOptions = new ArrayList<>();
    
    public ReportableLauncherOptionParser() {
        reportableSetting = new DefaultReportSetting();
        rerunSetting = new DefaultRerunSetting();
    }

    protected List<ConsoleOptionContributor> getContributors() {
        return Arrays.asList(reportableSetting, rerunSetting);
    }

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> allOptions = new ArrayList<>();
        getContributors().forEach(contributor -> allOptions.addAll(
                contributor.getConsoleOptionList()));
        allOptions.addAll(overridingOptions);
        return allOptions;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        for (ConsoleOptionContributor contributor : getContributors()) {
            if (contributor.getConsoleOptionList().contains(consoleOption)) {
                contributor.setArgumentValue(consoleOption, argumentValue);
            }
        }
    }

	@Override
	public void setOverridingArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
    	if (overridingOptions.contains(consoleOption)) {
        	consoleOption.setValue(argumentValue);
            return;
        }
	}
	
	@Override
	public void collectOverridingParameters(ProjectEntity project) throws ExecutionException {
		collectOverridableGlobalVariables(project);
	}

	
	private void collectOverridableGlobalVariables(ProjectEntity project) throws ExecutionException {
		overridingOptions = new OverridingParametersConsoleOptionContributor(project).getConsoleOptionList();
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
