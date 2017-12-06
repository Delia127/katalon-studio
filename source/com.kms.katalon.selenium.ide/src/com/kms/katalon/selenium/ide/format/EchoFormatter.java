package com.kms.katalon.selenium.ide.format;

import com.kms.katalon.selenium.ide.model.Command;

public class EchoFormatter implements Formatter {

	@Override
	public String format(Command command) {
		return "System.out.println(" + valueOf(command.getTarget()) + ")\n";
	}

	public static void main(String[] args) {
		EchoFormatter verify = new EchoFormatter();

		System.out.println(verify.format(new Command("echo", "${a}", "bbb")));
	}

}
