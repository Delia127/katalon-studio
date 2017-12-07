package com.kms.katalon.selenium.ide.util;

import java.lang.reflect.Method;

public final class ClazzUtils {
	
	public static final String WebDriverBackedSelenium = "com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium";
	
	public static boolean isArrayReturned(String methodName) {
		String getMethod = "get" + methodName;
		try {
			Method method = getMethodInClass(getMethod, WebDriverBackedSelenium);
			return method.getReturnType().isArray();
		} catch (Exception e) {
			return false;
		}
	}
	
	public static int getParamCount(String methodName) {
		try {
			Method method = getMethodInClass(methodName, WebDriverBackedSelenium);
			return method.getParameterCount();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static Object getReturnedType(String methodName) {
		try {
			Method method = getMethodInClass(methodName, WebDriverBackedSelenium);
			return method.getReturnType().getSimpleName();
		} catch (Exception e) {
			return String.class.getSimpleName();
		}
	}
	
	public static boolean hasMethod(String methodName) {
		try {
			Method method = getMethodInClass(methodName, WebDriverBackedSelenium);
			return method != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean hasParam(String methodName) {
		try {
			Method method = getMethodInClass(methodName, WebDriverBackedSelenium);
			return method.getParameterCount() > 0;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static Method getMethodInClass(String method, String clazz) throws Exception {
		Method[] methods = getMethods(clazz);
		for (Method m : methods) {
			if (m.getName().equalsIgnoreCase(method)) {
				return m;
			}
		}
		return null;
	}
	
	public static boolean hasMethodInClass(String method, String clazz) throws Exception {
		boolean has = false;
		Method[] methods = getMethods(clazz);
		for (Method m : methods) {
			if (m.getName().equalsIgnoreCase(method)) {
				return true;
			}
		}
		return has;
	}
	
	public static Method[] getMethods(Class<?> clazz) throws Exception {
		Method[] publicMethods = clazz.getMethods();
		return publicMethods;
	}
	
	public static Method[] getMethods(String clazz) throws Exception {
		Method[] publicMethods = Class.forName(clazz).getMethods();
		return publicMethods;
	}
	
}
