package com.kms.katalon.objectspy.element;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class HTMLPageElement extends HTMLFrameElement {

	private String url;

	public HTMLPageElement() {
		super();
	}

	public HTMLPageElement(String name, Map<String, String> attributes, List<HTMLElement> childElements, String url) {
		super(name, "PAGE", attributes, null, childElements);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (!(object instanceof HTMLPageElement)) {
			return false;
		}

		HTMLPageElement otherPage = (HTMLPageElement) object;

		if (otherPage.getUrl().equals(this.getUrl()) && otherPage.getName().equals(this.getName())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).appendSuper(super.hashCode()).append(this.getUrl()).toHashCode();
	}
}
