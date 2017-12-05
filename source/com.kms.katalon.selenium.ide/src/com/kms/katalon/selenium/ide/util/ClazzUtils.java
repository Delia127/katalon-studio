package com.kms.katalon.selenium.ide.util;

import java.lang.reflect.Method;

public class ClazzUtils {
	
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
