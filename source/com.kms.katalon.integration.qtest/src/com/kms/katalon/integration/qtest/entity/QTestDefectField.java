package com.kms.katalon.integration.qtest.entity;

import java.util.ArrayList;
import java.util.List;

public class QTestDefectField extends QTestEntity {

	private String link;
	private boolean required;
	private String attributeType;
	private Object value;
	private String caption;
	
	private QTestDefectField mainField;

	private List<QTestDefectField> allowedValues;
	
	public QTestDefectField(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public QTestDefectField() {
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public List<QTestDefectField> getAllowedValues() {
		if(allowedValues == null){
			allowedValues = new ArrayList<QTestDefectField>();
		}
		return allowedValues;
	}

	public void setAllowedValues(List<QTestDefectField> allowedValues) {
		this.allowedValues = allowedValues;
	}

	public QTestDefectField getMainField() {
		return mainField;
	}

	public void setMainField(QTestDefectField parentField) {
		this.mainField = parentField;
	}

	@Override
	public String toString() {
    	return this.name;
    }
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getCaption() {
		if(caption == null){
			caption = "";
		}
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

}
