package com.kms.katalon.execution.util;

import java.util.ArrayList;
import java.util.List;

import com.katalon.platform.api.model.PluginConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.StringConsoleOption;

public class ConsoleAdapter {

	public static List<StringConsoleOption> adaptToStringConsoleOptions(
			List<PluginConsoleOption<?>> pluginConsoleOptions) {
		List<StringConsoleOption> consoleOptions = new ArrayList<>();
		for (PluginConsoleOption<?> pluginConsoleOption : pluginConsoleOptions) {
			consoleOptions.add(new StringConsoleOption() {
				@Override
				public String getOption() {
					return pluginConsoleOption.getOption();
				}
			});
		}
		return consoleOptions;
	}

	public static List<PluginConsoleOption<?>> adaptToPluginConsoleOptions(
			List<? extends ConsoleOption<?>> stringConsoleOptions) {
		List<PluginConsoleOption<?>> consoleOptions = new ArrayList<>();
		for (ConsoleOption<?> stringConsoleOption : stringConsoleOptions) {
			consoleOptions.add(new PluginConsoleOption<String>() {
				@Override
				public Class<String> getArgumentType() {
					return String.class;
				}

				@Override
				public String getDefaultArgumentValue() {
					return "";
				}

				@Override
				public String getOption() {
					return stringConsoleOption.getOption();
				}

				@Override
				public String getValue() {
					return (String) stringConsoleOption.getValue();
				}

				@Override
				public boolean hasArgument() {
					return true;
				}

				@Override
				public boolean isRequired() {
					return stringConsoleOption.isRequired();
				}

				@Override
				public void setValue(String arg0) {
					stringConsoleOption.setValue(arg0);
				}
			});
		}
		return consoleOptions;
	}
	
	public static PluginConsoleOption<?> adaptToPluginConsoleOption(
			ConsoleOption<?> stringConsoleOption) {
		 return new PluginConsoleOption<String>() {
				@Override
				public Class<String> getArgumentType() {
					return String.class;
				}

				@Override
				public String getDefaultArgumentValue() {
					return "";
				}

				@Override
				public String getOption() {
					return stringConsoleOption.getOption();
				}

				@Override
				public String getValue() {
					return (String) stringConsoleOption.getValue();
				}

				@Override
				public boolean hasArgument() {
					return true;
				}

				@Override
				public boolean isRequired() {
					return stringConsoleOption.isRequired();
				}

				@Override
				public void setValue(String arg0) {
					stringConsoleOption.setValue(arg0);
				}
			};
		}
	}
