package com.kms.katalon.composer.mobile.objectspy.element;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class MobileElement {
	
	private String name;
	private String type;
	private String xpath;
	private Map<String, String> attributes;
	private MobileElement parentElement;
	
	private List<MobileElement> childrenElement;

	public MobileElement() {
		name = "";
		xpath = "";
	}

	public MobileElement(String name, String type, Map<String, String> attributes, String xpath, MobileElement parentElement) {
		this.name = name;
		this.type = type;
		this.attributes = attributes;
		this.parentElement = parentElement;
		this.xpath = xpath;
	}

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

	@Override
	public String toString() {
		return name;
	}

	public Map<String, String> getAttributes() {
		if(attributes == null){
			attributes = new LinkedHashMap<String, String>();
		}
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public MobileElement getParentElement() {
		return parentElement;
	}

	public void setParentElement(MobileElement parentElement) {
		this.parentElement = parentElement;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (!(object instanceof MobileElement)) {
			return false;
		}

		if (!object.getClass().getName().equals(this.getClass().getName())) {
			return false;
		}

		MobileElement otherElement = (MobileElement) object;

		if (otherElement.getParentElement() != null && this.getParentElement() != null && !otherElement.getParentElement().equals(this.getParentElement())) {
			return false;
		}

		if (otherElement.getXpath().equals(this.getXpath()) && otherElement.getName().equals(this.getName())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).appendSuper(super.hashCode())
				.append((this.getParentElement() != null) ? this.getParentElement().hashCode() : "" + this.xpath)
				.toHashCode();
	}
	
	public List<MobileElement> getChildrenElement() {
		if(childrenElement == null){
			childrenElement = new ArrayList<MobileElement>();
		}
		return childrenElement;
	}

	public void setChildrenElement(List<MobileElement> childrenElement) {
		this.childrenElement = childrenElement;
	}


}
