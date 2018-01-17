package com.kms.katalon.selenium.ide.format;

import com.kms.katalon.selenium.ide.model.Command;

public class PauseFormatter implements Formatter {

	@Override
	public String format(Command command) {
		return "Thread.sleep(" + valueOf(command.getTarget()) + ")\n";
	}

}
