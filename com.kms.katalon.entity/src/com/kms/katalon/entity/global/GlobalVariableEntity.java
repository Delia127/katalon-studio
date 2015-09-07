package com.kms.katalon.entity.global;

import java.io.Serializable;


public class GlobalVariableEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String initValue;
	
	public GlobalVariableEntity() {
		super();
	}

	public GlobalVariableEntity(String newName, String value) {
		setName(newName);
		setInitValue(value);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInitValue() {
		return initValue;
	}
	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}
}
