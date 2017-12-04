package com.kms.katalon.selenium.ide.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public final class Formatter {

	private static final Formatter INSTANCE = new Formatter();

	private final Map<String, String> commands = new HashMap<>();

	{
		commands.put("click", "click");
	}
	
	public String format(String command) {
		if (commands.containsKey(command)) {
			return commands.get(command);
		}
		return command;
	}
	
	public List<String> format(List<String> commands) {
		List<String> formattedCommands = new ArrayList<>();
		commands.forEach(command -> {
			String formatted = format(command);
			formattedCommands.add(formatted);
		});
		return formattedCommands;				
	}

	public String getHeader() {
		return StringUtils.EMPTY;
	}

	public String getFooter() {
		return StringUtils.EMPTY;
	}

	public static Formatter getInstance() {
        return INSTANCE;
    }

}
