package com.kms.katalon.selenium.ide.format;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.util.ClazzUtils;

public interface Formatter {

	public String format(Command command);
	
	public default String valueOf(String param) {
		if (StringUtils.isNotBlank(param) && param.contains("{")) {
			param = param.replace("${", "");
			param = param.replace("}", "");
			return param;
		}
		return stringValue(param);
	}
	
	public default String paramOf(String param) {
		if (StringUtils.isNotBlank(param) && param.contains("{")) {
			param = param.replace("${", "");
			param = param.replace("}", "");
			return param;
		}
		return param;
	}
	
	public default String stringValue(String value) {
		return "'" + value + "'";
	}
	
	public default String getCleanCommandTail(String commandTail) {
		String method = commandTail.replace("Not", "");
		return method.replace("AndWait", "");
	}
	
	public default String getWaitIfHas(String commandTail) {
		if (commandTail.lastIndexOf("AndWait") != -1) {
			return "\nselenium.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD_IN_SECONDS)";
		}
		return null;
	}
	
	public default String getParamName(String method, String target, String value) {
		boolean hasParam = ClazzUtils.hasParam(getCleanCommandTail(method));
		if (hasParam) {
			return paramOf(value);
		} 
		return paramOf(target);
	}
	
	public default String getParamMethod(String method, String target) throws Exception {
		boolean hasMethod = ClazzUtils.hasMethod(method);
		if (!hasMethod) {
			throw new Exception("Method is not found");
		}
		boolean hasParam = ClazzUtils.hasParam(method);
		if (hasParam) {
			return "(" + valueOf(target) + ")";
		} 
		return "()";
	}
	
	public default String getNormalMethod(String commandTail, String target) throws Exception {
		String methodName = getCleanCommandTail(commandTail);
		String param = getParamMethod("get" + methodName, target);
		boolean isArray = ClazzUtils.isArrayReturned(methodName);
		String method = methodName + param;
		return isArray ? "join(selenium.get" + method + ", ',')" : "selenium.get" + method;
	}
	
	public default String getBoolMethod(String commandTail, String target) throws Exception {
		String methodName = getCleanCommandTail(commandTail);
		String param = getParamMethod("is" + methodName + "Present", target);
		return "selenium.is" + methodName + "Present" + param;
	}
	
	public default String getBoolCheckedMethod(String commandTail, String target) throws Exception {
		String methodName = getCleanCommandTail(commandTail);
		String param = getParamMethod("is" + methodName, target);
		return "selenium.is" + methodName + param;
	}
}
