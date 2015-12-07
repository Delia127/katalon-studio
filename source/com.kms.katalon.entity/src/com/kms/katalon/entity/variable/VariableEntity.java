package com.kms.katalon.entity.variable;

import java.io.Serializable;

import com.kms.katalon.entity.util.Util;

public class VariableEntity implements Serializable {
	protected static final long serialVersionUID = 1L;
	protected String defaultValue; // raw string of default value of the variable
	protected String name;
	protected String id;

	public VariableEntity() {
		id = Util.generateGuid();
		name = "";
		defaultValue = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public VariableEntity clone() {
	    VariableEntity newVariable = new VariableEntity();
	    newVariable.setName(getName());
	    newVariable.setDefaultValue(getDefaultValue());
	    return newVariable;
	}
}
