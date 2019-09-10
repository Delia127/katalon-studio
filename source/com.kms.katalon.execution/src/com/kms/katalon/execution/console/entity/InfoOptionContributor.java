package com.kms.katalon.execution.console.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.execution.console.ConsoleMain;

public class InfoOptionContributor implements ConsoleOptionContributor {
	
	public String getConfigOption() {
		return "info";
	}
	
	public static final StringConsoleOption BUILD_LABEL_CONSOLE_OPTION = new StringConsoleOption() {
		@Override
		public String getOption() {
			return ConsoleMain.BUILD_LABEL_OPTION;
		}
		
		@Override
		public String getDefaultArgumentValue() {
			return "";
		} 
	};
	
	public static final StringConsoleOption BUILD_URL_CONSOLE_OPTION = new StringConsoleOption() {
		@Override
		public String getOption() {
			return ConsoleMain.BUILD_URL_OPTION;
		}
		
		@Override
		public String getDefaultArgumentValue() {
			return "";
		}
	};
	
	@Override
	public List<ConsoleOption<?>> getConsoleOptionList() {
		List<ConsoleOption<?>> consoleOptionList = new ArrayList<ConsoleOption<?>>();
		consoleOptionList.add(BUILD_LABEL_CONSOLE_OPTION);
		consoleOptionList.add(BUILD_URL_CONSOLE_OPTION);
		return consoleOptionList;
	}
	
	@Override
	public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {	
		if (consoleOption == BUILD_LABEL_CONSOLE_OPTION) {
			consoleOption.setValue(argumentValue);
		}
		
		if (consoleOption == BUILD_URL_CONSOLE_OPTION) {
			consoleOption.setValue(argumentValue);
		}
	}
	
	
	public Map<String, String> getOptionValues() {	
		Map<String, String> info = new HashMap<>();
		for (ConsoleOption<?> consoleOption : getConsoleOptionList()) {
			String optionName = consoleOption.getOption();
			Object value = consoleOption.getValue();
			if (value != null) {
				info.put(optionName, value.toString());
			}
		}
		return info;
	}
}
