package com.kms.katalon.objectspy.element;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class HTMLRawElement {
	private String tag;
	private NamedNodeMap attributes;
	private HTMLRawElement parentElement;
	private List<HTMLRawElement> childElements;
	private String xpath;
	private String absoluteXpath;
	private Element domElement;
	private int index;

	public HTMLRawElement(Element domElement, int index, HTMLRawElement parentElement, String xpath, List<HTMLRawElement> childElements) {
		this.tag = domElement.getTagName();
		this.xpath = xpath;
		this.attributes = domElement.getAttributes();
		this.childElements = childElements;
		this.domElement = domElement;
		this.index = index;
		this.parentElement = parentElement;
		buildAbsoluteXPath();
	}
	
	private void buildAbsoluteXPath() {
		absoluteXpath = (parentElement != null ? parentElement.getAbsoluteXpath() : "") +  "/" + tag + "[" + index + "]";
	}

	public String getTag() {
		return tag;
	}

	public NamedNodeMap getAttributes() {
		return attributes;
	}

	public HTMLRawElement getParentElement() {
		return parentElement;
	}

	public List<HTMLRawElement> getChildElements() {
		return childElements;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("<");
		stringBuilder.append(tag);
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			stringBuilder.append(" ");
			stringBuilder.append(attribute.getNodeName());
			stringBuilder.append("='");
			stringBuilder.append(attribute.getNodeValue());
			stringBuilder.append("'");
		}
		stringBuilder.append(">");
		return stringBuilder.toString();
	}

	public String getXpath() {
		return xpath;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}

		if (!(object instanceof HTMLRawElement || object instanceof String)) {
			return false;
		}

		String xpath = null;
		if (object instanceof HTMLRawElement) {
			xpath = ((HTMLRawElement) object).getAbsoluteXpath();
		} else {
			xpath = String.valueOf(object);
		}
		return StringUtils.equals(xpath, getAbsoluteXpath());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).append(absoluteXpath).toHashCode();
	}

	public String getAbsoluteXpath() {
		return absoluteXpath;
	}

	public Element getDomElement() {
		return domElement;
	}

	public void setDomElement(Element domElement) {
		this.domElement = domElement;
	}
}
