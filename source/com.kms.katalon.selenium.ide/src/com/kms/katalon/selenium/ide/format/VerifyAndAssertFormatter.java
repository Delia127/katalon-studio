package com.kms.katalon.selenium.ide.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;

public class VerifyAndAssertFormatter implements Formatter {

	private String action;
	
	public VerifyAndAssertFormatter(String action) {
		this.action = action;
	}

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

	public String normal(Command command) {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(" + action + ")(.*?)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String actionMethod = getActionMethod(matcher.group(2));
			String method = getNormalMethod(matcher.group(2), command.getTarget());
			String paramName = getParamName("get" + matcher.group(2), command.getTarget(), command.getValue());
			formatted.append(actionMethod + "('" + paramName + "', " + method + ")");

			String wait = getWaitIfHas(matcher.group(2));
			if (StringUtils.isNotBlank(wait)) {
				formatted.append(wait);
			}
		}
		return formatted.toString();
	}

	public String getActionMethod(String commandTail) {
		return commandTail.startsWith("Not") ? action + "NotEquals" : action + "Equals";
	}
	
	public String notPresent(Command command) {
		Pattern pattern = Pattern.compile("(" + action + ")(.*?)(Not)(Present)");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolMethod(matcher.group(2), command.getTarget());
			return action + "False(" + method + ")";
		}
		return StringUtils.EMPTY;
		
	}

	public String present(Command command) {
		Pattern pattern = Pattern.compile("(" + action + ")(.*?)(Present)");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolMethod(matcher.group(2), command.getTarget());
			return action + "True(" + method + ")";
		}
		return StringUtils.EMPTY;
	}

	public static void main(String[] args) {
		VerifyAndAssertFormatter verify = new VerifyAndAssertFormatter("verify");

		System.out.println(verify.format(new Command("verifyAlert", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyAlertPresent", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyAlertNotPresent", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyAllButtons", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyAttributeFromAllWindows", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyConfirmationNotPresent", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyPrompt", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyNotPrompt", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyNotSelectOptions", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyElementHeight", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyNotSelectedLabels", "aaa", "bbb")));
		System.out.println(verify.format(new Command("verifyPromptNotPresent", "aaa", "bbb")));
	}
}
