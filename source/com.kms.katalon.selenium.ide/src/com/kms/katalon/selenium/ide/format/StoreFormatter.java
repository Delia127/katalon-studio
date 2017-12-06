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
		String formatted = present(command);
		if (StringUtils.isBlank(formatted)) {
			formatted = normal(command);
		}
		return formatted;
	}
	
	public String normal(Command command) {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(" + ACTION + ")(.*?)$");
        Matcher matcher = pattern.matcher(command.getCommand());
        if (matcher.find()) {
        	String cleanedMethod = getCleanCommandTail(matcher.group(2));
        	Object returnedType = ClazzUtils.getReturnedType("get" + cleanedMethod);
        	String paramName = getParamName("get" + cleanedMethod, command.getTarget(), command.getValue());
        	String method = getNormalMethod(cleanedMethod, command.getTarget());
        	formatted.append(returnedType + " " + paramName + " = " + method);
        	
        	String wait = getWaitIfHas(matcher.group(2));
        	if (StringUtils.isNotBlank(wait)) {
        		formatted.append(wait);
        	}
        }
		return formatted.toString();
	}
	
	public String present(Command command) {
		StringBuffer formatted = new StringBuffer();
		Pattern pattern = Pattern.compile("(" + ACTION + ")(.*?)(Present)");
        Matcher matcher = pattern.matcher(command.getCommand());
        if (matcher.find()) {
        	String paramName = getParamName("get" + matcher.group(2), command.getTarget(), command.getValue());
        	String method = getBoolMethod(matcher.group(2), command.getTarget());
        	formatted.append("boolean " + paramName + " = " + method);
        }
		return formatted.toString();
	}
	
	public static void main(String[] args) {
		StoreFormatter store = new StoreFormatter();
		
		System.out.println(store.format(new Command("storeAlert", "aaa", "bbb")));
		System.out.println(store.format(new Command("storeAllButtons", "aaa", "bbb")));
		System.out.println(store.format(new Command("storeAttributeFromAllWindows", "aaa", "bbb")));
		System.out.println(store.format(new Command("storePrompt", "aaa", "bbb")));
		System.out.println(store.format(new Command("storeTextAndWait", "aaa", "bbb")));
		System.out.println(store.format(new Command("storeElementHeight", "aaa", "bbb")));
		System.out.println(store.format(new Command("storeElementPresent", "aaa", "bbb")));
	}
}
