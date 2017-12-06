package com.kms.katalon.selenium.ide.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;

public class WaitForFormatter implements Formatter {
	
	@Override
	public String format(Command command) {
		String formatted = notPresent(command);
		if (StringUtils.isBlank(formatted)) {
			formatted = present(command);
		}
		if (StringUtils.isBlank(formatted)) {
			formatted = normal(command);
		}
		return formatted;
	}

	private String normal(Command command) {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(waitFor)(.*?)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String paramName = getParamName("get" + matcher.group(2), command.getTarget(), command.getValue());
			String method = getNormalMethod(matcher.group(2), command.getTarget());
			return returnPattern("'" + paramName + "'.equals(" + method + ")");
		}
		return formatted.toString();
	}

	private String present(Command command) {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(waitFor)(.*?)(Present)");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolMethod(matcher.group(2), command.getTarget());
			return returnPattern(method);
		}
		return formatted.toString();
	}

	private String notPresent(Command command) {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(waitFor)(.*?)(Not)(Present)");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolMethod(matcher.group(2), command.getTarget());
			return returnPattern("!" + method);
		}
		return formatted.toString();
	}

	private String returnPattern(String condition) {
		return "for (int second = 0;; second++) {\n"
				+ "   if (second >= 60) fail(\"timeout\");\n"
				+ "   try { if (" + condition + ") break; } catch (Exception e) {}\n"
				+ "   Thread.sleep(1000);\n"
				+ "}\n";
	}
	
	public static void main(String[] args) {
		WaitForFormatter store = new WaitForFormatter();
		
		System.out.println(store.format(new Command("waitForText", "aaa", "bbb")));
		System.out.println(store.format(new Command("waitForSelectedIndexes", "aaa", "bbb")));	
		System.out.println(store.format(new Command("waitForTextPresent", "aaa", "bbb")));
		System.out.println(store.format(new Command("waitForTextNotPresent", "aaa", "bbb")));
		System.out.println(store.format(new Command("waitForXpathCount", "aaa", "bbb")));
	}
}
