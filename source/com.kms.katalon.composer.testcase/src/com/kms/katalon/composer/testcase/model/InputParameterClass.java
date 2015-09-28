package com.kms.katalon.composer.testcase.model;

import java.util.List;

public class InputParameterClass {
	private String fullName;
	private String simpleName;
	private boolean isArray;
	private boolean isEnum;
	private Object[] enumConstants;
	private InputParameterClass componentType;
	private List<InputParameterClass> actualTypeArguments;
	private int modifiers;

	public InputParameterClass(String fullName, String simpleName) {
		this.fullName = fullName;
		this.simpleName = simpleName;
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Object[] getEnumConstants() {
		return enumConstants;
	}

	public void setEnumConstants(Object[] enumConstants) {
		this.enumConstants = enumConstants;
	}

	public InputParameterClass getComponentType() {
		return componentType;
	}

	public void setComponentType(InputParameterClass componentType) {
		this.componentType = componentType;
	}

	public List<InputParameterClass> getActualTypeArguments() {
		return actualTypeArguments;
	}

	public void setActualTypeArguments(List<InputParameterClass> actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}
}
