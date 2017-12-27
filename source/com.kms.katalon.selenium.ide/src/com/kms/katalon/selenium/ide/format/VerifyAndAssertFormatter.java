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
		String formatted = StringUtils.EMPTY;
		try {
			formatted = checked(command);
			if (StringUtils.isBlank(formatted)) {
				formatted = notChecked(command);
			}
			if (StringUtils.isBlank(formatted)) {
				formatted = whether(command);
			}
			if (StringUtils.isBlank(formatted)) {
				formatted = notWhether(command);
			}
			if (StringUtils.isBlank(formatted)) {
				formatted = notPresent(command);
			}
			if (StringUtils.isBlank(formatted)) {
				formatted = present(command);
			}
			if (StringUtils.isBlank(formatted)) {
				formatted = normal(command);
			}
		} catch (Exception e) {
			formatted = String.format("Method %s is not found", command.getCommand());
		}
		return formatted +"\n";
	}

	public String normal(Command command) throws Exception {
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
	
	public String notPresent(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + action + ")(.*?)(Not)(Present)");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolMethod(matcher.group(2), command.getTarget());
			return action + "False(" + method + ")";
		}
		return StringUtils.EMPTY;
		
	}

	public String present(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + action + ")(.*?)(Present)");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolMethod(matcher.group(2), command.getTarget());
			return action + "True(" + method + ")";
		}
		return StringUtils.EMPTY;
	}
	
	public String checked(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + action + ")(Checked|Editable|Ordered|Visible|SomethingSelected)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolCheckedMethod(matcher.group(2), command.getTarget());
			return action + "True(" + method + ")";
		}
		return StringUtils.EMPTY;
	}
	
	public String notChecked(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + action + ")(Not)(Checked|Editable|Ordered|Visible|SomethingSelected)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolCheckedMethod(matcher.group(3), command.getTarget());
			return action + "False(" + method + ")";
		}
		return StringUtils.EMPTY;
	}

	public String whether(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + action + ")(Whether.*?)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolWhetherMethod(matcher.group(2), command.getTarget(), command.getValue());
			return action + "True(" + method + ")";
		}
		return StringUtils.EMPTY;
	}
	
	public String notWhether(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + action + ")(Not)(Whether.*?)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolWhetherMethod(matcher.group(3), command.getTarget(), command.getValue());
			return action + "False(" + method + ")";
		}
		return StringUtils.EMPTY;
	}
}
