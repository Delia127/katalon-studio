package com.kms.katalon.execution.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.console.PluginConsoleOption;
import com.kms.katalon.execution.console.entity.ConsoleOption;

public class ConsoleAdapter {

	public static <T> ConsoleOption<?> adapt(PluginConsoleOption<?> pluginConsoleOption) {
		return new ConsoleOption<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public Class<T> getArgumentType() {
				return (Class<T>) pluginConsoleOption.getArgumentType();
			}

			@Override
			public String getDefaultArgumentValue() {
				return "";
			}

			@Override
			public String getOption() {
				return pluginConsoleOption.getOption();
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getValue() {
				return (T) pluginConsoleOption.getValue();
			}

			@Override
			public boolean hasArgument() {
				return true;
			}

			@Override
			public boolean isRequired() {
				return pluginConsoleOption.isRequired();
			}

			@Override
			public void setValue(String arg0) {
				pluginConsoleOption.setValue(arg0);
			}
		};
	}
	
	public static <T> PluginConsoleOption<?> adapt(ConsoleOption<?> pluginConsoleOption) {
		return new PluginConsoleOption<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public Class<T> getArgumentType() {
				return (Class<T>) pluginConsoleOption.getArgumentType();
			}

			@Override
			public String getDefaultArgumentValue() {
				return "";
			}

			@Override
			public String getOption() {
				return pluginConsoleOption.getOption();
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getValue() {
				return (T) pluginConsoleOption.getValue();
			}

			@Override
			public boolean hasArgument() {
				return true;
			}

			@Override
			public boolean isRequired() {
				return pluginConsoleOption.isRequired();
			}

			@Override
			public void setValue(String arg0) {
				pluginConsoleOption.setValue(arg0);
			}
		};
	}

	public static List<ConsoleOption<?>> adapt(List<PluginConsoleOption<?>> pluginConsoleOptions) {
		return pluginConsoleOptions
		.stream()
		.map(a -> adapt(a))
		.collect(Collectors.toCollection(() -> new ArrayList<ConsoleOption<?>>()));
	}
}
