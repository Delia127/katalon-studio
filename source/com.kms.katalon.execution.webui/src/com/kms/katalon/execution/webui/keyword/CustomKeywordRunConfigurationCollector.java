package com.kms.katalon.execution.webui.keyword;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.execution.configuration.CustomRunConfiguration;

public class CustomKeywordRunConfigurationCollector {

	private static CustomKeywordRunConfigurationCollector _instance;
	private List<CustomRunConfiguration> customKeywordRunConfigurations;

	public CustomKeywordRunConfigurationCollector() {
		customKeywordRunConfigurations = new ArrayList<>();
	}

	public static CustomKeywordRunConfigurationCollector getInstance() {
		if (_instance == null) {
			_instance = new CustomKeywordRunConfigurationCollector();
		}
		return _instance;
	}

	public void addCustomKeywordRunConfiguration(CustomRunConfiguration runConfig) {
		customKeywordRunConfigurations.add(runConfig);
	}

	public List<CustomRunConfiguration> getCustomKeywordRunConfigurations() {
		return customKeywordRunConfigurations;
	}

}
