package com.kms.katalon.objectspy.element.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.w3c.dom.NodeList;

import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.util.DOMUtils;

public class HTMLRawElementTreeViewerFilter extends ViewerFilter {
	private List<String> filteredElementsXpath;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!(element instanceof HTMLRawElement)) {
			return false;
		}
		if (filteredElementsXpath == null || filteredElementsXpath.isEmpty()) {
			return true;
		}

		String elementAbsoluteXpath = ((HTMLRawElement) element).getAbsoluteXpath();

		if (filteredElementsXpath.contains(elementAbsoluteXpath)) {
			return true;
		}

		boolean isChildSelected = false;

		for (HTMLRawElement childElement : ((HTMLRawElement) element).getChildElements()) {
			isChildSelected |= select(viewer, element, childElement);
		}
		return isChildSelected;

	}

	public void setFilteredElements(NodeList filteredElements) {
		if (filteredElementsXpath == null) {
			filteredElementsXpath = new ArrayList<String>();
		}
		filteredElementsXpath.clear();
		if (filteredElements != null) {
			for (int i = 0; i < filteredElements.getLength(); i++) {
				filteredElementsXpath.add(DOMUtils.getXpathForNode(filteredElements.item(i)));
			}
		}
	}
}
