package com.kms.katalon.composer.testcase.model;


public class InputParameter {
	private Object value;
	private String paramName;
	private InputParameterClass paramType;

	public InputParameter(Object value) {
		this.value = value;
	}

	public InputParameter(String paramName, InputParameterClass paramType, Object value) {
		this.value = value;
		this.paramName = paramName;
		this.paramType = paramType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public InputParameterClass getParamType() {
		return paramType;
	}

	public void setParamType(InputParameterClass paramType) {
		this.paramType = paramType;
	}

	
}
