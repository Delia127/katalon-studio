package com.kms.katalon.selenium.ide.format;

import com.kms.katalon.selenium.ide.model.Command;

public class PauseFormatter implements Formatter {

	@Override
	public String format(Command command) {
		return "Thread.sleep(" + valueOf(command.getTarget()) + ")\n";
	}

	public static void main(String[] args) {
		PauseFormatter verify = new PauseFormatter();

		System.out.println(verify.format(new Command("pause", "10", "bbb")));
	}
}
