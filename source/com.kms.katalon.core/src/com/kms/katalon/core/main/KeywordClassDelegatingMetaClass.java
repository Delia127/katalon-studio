package com.kms.katalon.core.main;

import com.kms.katalon.core.constants.StringConstants;

import groovy.lang.DelegatingMetaClass;

public class KeywordClassDelegatingMetaClass extends DelegatingMetaClass {
	KeywordClassDelegatingMetaClass(final Class<?> clazz) {
		super(clazz);
		initialize();
	}
	
	@Override
	public Object getProperty(Object object, String property) {
		if (property == null || !StringConstants.GLOBAL_VARIABLE_CLASS_NAME.equals(property)) {
			return super.getProperty(object, property);
		} else {
			try {
				return Class.forName(StringConstants.GLOBAL_VARIABLE_CLASS_NAME);
			} catch (ClassNotFoundException e) {
				return super.getProperty(object, property);
			}
		}
	}
}
