package com.kms.katalon.objectspy.element;

import org.w3c.dom.Node;

public class DomElementXpath {
	private Node element;
	private String xpath;
	private String[] xpathTreePath;
	
	public DomElementXpath(Node element, String xpath, String[] xpathTreePath) {
		this.setElement(element);
		this.setXpath(xpath);
		this.setXpathTreePath(xpathTreePath);
	}

	public Node getElement() {
		return element;
	}

	public void setElement(Node element) {
		this.element = element;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String[] getXpathTreePath() {
		return xpathTreePath;
	}

	public void setXpathTreePath(String[] xpathTreePath) {
		this.xpathTreePath = xpathTreePath;
	}
}
