package com.kms.katalon.core.testcase;

public class Variable {
	private String name;
	private String defaultValue;	

	public String getName() {
		if (name == null) {
			name = "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		if (defaultValue == null) {
			defaultValue = "";
		}
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
