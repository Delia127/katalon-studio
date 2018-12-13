package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class OverridingParametersConsoleOptionContributor implements ConsoleOptionContributor{
    private List<ConsoleOption<?>> overridingOptions = new ArrayList<>();
    private static final String OVERRIDING_GLOBAL_VARIABLE_PREFIX = "g_";
    
	public OverridingParametersConsoleOptionContributor(ProjectEntity projectEntity){
		try {
			List<GlobalVariableEntity> globalVariablesEntity = GlobalVariableController.getInstance()
					.getAllGlobalVariables(projectEntity);
			
			globalVariablesEntity.forEach(a -> {
				overridingOptions.add(new StringConsoleOption() {
					@Override
					public String getOption() {
						String name = OVERRIDING_GLOBAL_VARIABLE_PREFIX + a.getName();
						return name;
					}
					public boolean isRequired() {
						return false;
					}
				});
			});
			
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
	
	@Override
	public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> consoleOptions = new ArrayList<>();
        consoleOptions.addAll(overridingOptions);
        return consoleOptions;
	}

	@Override
	public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
		// Do nothing here
	}

}
