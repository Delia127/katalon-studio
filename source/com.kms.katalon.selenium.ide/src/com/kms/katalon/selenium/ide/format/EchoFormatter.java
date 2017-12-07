package com.kms.katalon.selenium.ide.format;

import com.kms.katalon.selenium.ide.model.Command;

public class EchoFormatter implements Formatter {

	@Override
	public String format(Command command) {
		return "System.out.println(" + valueOf(command.getTarget()) + ")\n";
	}

}
