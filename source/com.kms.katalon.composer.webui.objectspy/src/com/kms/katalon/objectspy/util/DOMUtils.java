package com.kms.katalon.objectspy.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.DomElementXpath;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.exception.DOMException;

public class DOMUtils {
	private static int getIndexForNode(Node parentNode, Node node) {
		if (parentNode == null || node == null) {
			return -1;
		}
		int count = 0;
		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			Node htmlNode = parentNode.getChildNodes().item(i);
			if (htmlNode instanceof Element) {
				if (htmlNode.equals(node)) {
					return count;
				}
				count++;
			}
		}
		return -1;
	}

	// Get xpath for node
	public static String getXpathForNode(Node node) {
		if (node != null && node instanceof Element) {
			return ((node.getParentNode() != null) ? getXpathForNode(node.getParentNode()) + "/" : "")
					+ node.getNodeName()
					+ ((node.getParentNode() != null) ? "[" + (getIndexForNode(node.getParentNode(), node) + 1) + "]"
							: "");
		}
		return "";
	}

	// Get xpath for node and save xpath in list
	public static DomElementXpath getDOMElementXpathForNode(Node node) {
		if (node != null && node instanceof Element) {
			DomElementXpath parentDomElementXpath = getDOMElementXpathForNode(node.getParentNode());
			String xpath = ((parentDomElementXpath != null) ? parentDomElementXpath.getXpath() : "")
					+ "/"
					+ node.getNodeName()
					+ ((node.getParentNode() != null) ? "[" + (getIndexForNode(node.getParentNode(), node) + 1) + "]"
							: "");
			String[] xpathTreePaths = null;
			if (parentDomElementXpath != null) {
				String[] parentXpathTreePaths = parentDomElementXpath.getXpathTreePath();
				xpathTreePaths = new String[parentXpathTreePaths.length + 1];
				int i = 0;
				for (i = 0; i < parentXpathTreePaths.length; i++) {
					xpathTreePaths[i] = parentXpathTreePaths[i];
				}
				xpathTreePaths[i] = parentDomElementXpath.getXpath();
			} else {
				xpathTreePaths = new String[] {};
			}
			DomElementXpath domElementXpath = new DomElementXpath(node, xpath, xpathTreePaths);
			return domElementXpath;
		}
		return null;
	}

	public static void compareNodeAttributes(HTMLElement expectedElement, Element actualElement) throws DOMException {
		Map<String, String> expectedAttrs = expectedElement.getAttributes();
		
		// minus 'xpath' attribute
		if ((expectedAttrs.entrySet().size() - 1) != actualElement.getAttributes().getLength()) {
			throw new DOMException(StringConstants.UTIL_EXC_ATTR_NUMBER_DOES_NOT_MATCH);
		}

		for (int i = 0; i < actualElement.getAttributes().getLength(); i++) {
			Attr expectedAttr = (Attr) actualElement.getAttributes().item(i);
			if (expectedAttr.getName().startsWith("xmlns")) {
				continue;
			}
			String actualAttr = expectedAttrs.get(expectedAttr.getName());
			if (actualAttr == null) {
				throw new DOMException(MessageFormat.format(StringConstants.UTIL_EXC_ELEM_ATTR_NOT_FOUND, 
						expectedAttr.getName()));
			}
			if (!expectedAttr.getValue().equals(actualAttr)) {
				throw new DOMException(MessageFormat.format(StringConstants.UTIL_EXC_ATTR_VAL_DOES_NOT_MATCH, 
						expectedAttr.getName(), expectedAttr.getValue(), actualAttr));
			}
		}
	}

	public static String[] getXpathTreePath(String xpath) {
		List<String> paths = new ArrayList<String>();
		while (xpath.lastIndexOf("/") != -1) {
			paths.add(0, xpath);
			xpath = xpath.substring(0, xpath.lastIndexOf("/"));
		}
		return paths.toArray(new String[paths.size()]);

	}
}
