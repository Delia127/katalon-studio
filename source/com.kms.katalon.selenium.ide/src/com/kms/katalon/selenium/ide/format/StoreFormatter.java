package com.kms.katalon.selenium.ide.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.util.ClazzUtils;

public class StoreFormatter implements Formatter {

	private static final String ACTION = "store";
	
	@Override
	public String format(Command command) {
		String formatted = StringUtils.EMPTY;
		try {
			formatted = checked(command);
			if (StringUtils.isBlank(formatted)) {
				formatted = notChecked(command);
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
		return formatted + "\n";
	}
	
	public String normal(Command command) throws Exception {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(" + ACTION + ")(.*?)$");
        Matcher matcher = pattern.matcher(command.getCommand());
        if (matcher.find()) {
        	String commandTail = matcher.group(2);
        	if (StringUtils.isNotBlank(commandTail)) {
	        	String cleanedMethod = getCleanCommandTail(commandTail);
		        	if (StringUtils.isNotBlank(cleanedMethod)) {
		        	Object returnedType = ClazzUtils.getReturnedType("get" + cleanedMethod);
		        	String paramName = getParamName("get" + cleanedMethod, command.getTarget(), command.getValue());
		        	String method = getNormalMethod(cleanedMethod, command.getTarget());
		        	formatted.append(returnedType + " " + paramName + " = " + method);
	        	} else {
	        		formatted.append("String" + " " + command.getValue() + " = " + valueOf(command.getTarget()));
	        	}
        	} else {
        		formatted.append("String" + " " + command.getValue() + " = " + valueOf(command.getTarget()));
        	}
        	
        	String wait = getWaitIfHas(command.getCommand());
        	if (StringUtils.isNotBlank(wait)) {
        		formatted.append(wait);
        	}
        }
		return formatted.toString();
	}
	
	public String present(Command command) throws Exception {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(" + ACTION + ")(.*?)(Present)");
        Matcher matcher = pattern.matcher(command.getCommand());
        if (matcher.find()) {
        	String paramName = getParamName("is" + matcher.group(2) + "Present", command.getTarget(), command.getValue());
        	String method = getBoolMethod(matcher.group(2), command.getTarget());
        	formatted.append("boolean " + paramName + " = " + method);
        }
		return formatted.toString();
	}
	
	public String checked(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + ACTION + ")(Checked|Editable|Ordered|Visible|SomethingSelected)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String paramName = getParamName("is" + matcher.group(2), command.getTarget(), command.getValue());
			String method = getBoolCheckedMethod(matcher.group(2), command.getTarget());
			return "boolean " + paramName + " = " + method;
		}
		return StringUtils.EMPTY;
	}
	
	public String notChecked(Command command) throws Exception {
		Pattern pattern = Pattern.compile("(" + ACTION + ")(Not)(Checked|Editable|Ordered|Visible|SomethingSelected)$");
		Matcher matcher = pattern.matcher(command.getCommand());
		if (matcher.find()) {
			String method = getBoolCheckedMethod(matcher.group(3), command.getTarget());
			return ACTION + "False(" + method + ")";
		}
		return StringUtils.EMPTY;
	}

}
