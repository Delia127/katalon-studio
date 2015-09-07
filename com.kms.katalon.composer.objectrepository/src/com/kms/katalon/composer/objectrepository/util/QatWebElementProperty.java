package com.kms.katalon.composer.objectrepository.util;

public class QatWebElementProperty {
	private String name;
	private String type;
	private String value;
	private String matchCondition;
	private boolean isSelected;
	private String elementId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMatchCondition() {
		return matchCondition;
	}

	public void setMatchCondition(String matchCondition) {
		this.matchCondition = matchCondition;
	}

	public boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(String isSelected) {
		this.isSelected = Boolean.valueOf(isSelected);
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

}
