package com.kms.katalon.objectspy.element.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Node;

import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.element.DomElementXpath;
import com.kms.katalon.objectspy.element.HTMLRawElement;

public class HTMLRawElementLabelProvider extends StyledCellLabelProvider implements ILabelProvider {
	private static Color BROWN = null;
	private List<String> filteredElementsXpath;

	public HTMLRawElementLabelProvider() {
		if (BROWN == null || BROWN.isDisposed()) {
			BROWN = new Color(Display.getCurrent(), 124, 84, 50);
		}
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		if (BROWN != null) {
			BROWN.dispose();
		}
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		cell.setText(getText(element));

		if (!(element instanceof HTMLRawElement)) {
			super.update(cell);
			return;
		}
		HTMLRawElement htmlRawElement = (HTMLRawElement) element;
		if (filteredElementsXpath == null) {
			super.update(cell);
			return;
		}

		String elementAbsoluteXpath = htmlRawElement.getAbsoluteXpath();
		boolean isSelected = filteredElementsXpath.contains(elementAbsoluteXpath);

		int textLength = cell.getText().length();
		List<StyleRange> range = new ArrayList<>();
		Color backgroundColor = null;
		Color foreGroundColor = null;
		Color darkMagenta = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		if (isSelected) {
			backgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
			foreGroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
		range.add(new StyleRange(0, htmlRawElement.getTag().length() + 1, foreGroundColor != null ? foreGroundColor
				: darkMagenta, backgroundColor));
		range.add(new StyleRange(textLength - 1, textLength, foreGroundColor != null ? foreGroundColor : darkMagenta,
				backgroundColor));
		int currentPosition = htmlRawElement.getTag().length() + 1;
		for (int i = 0; i < htmlRawElement.getAttributes().getLength(); i++) {
			range.add(new StyleRange(currentPosition, currentPosition + 1, null, backgroundColor));
			Node attribute = htmlRawElement.getAttributes().item(i);
			currentPosition++;
			range.add(new StyleRange(currentPosition, currentPosition + attribute.getNodeName().length(),
					foreGroundColor != null ? foreGroundColor : BROWN, backgroundColor));
			currentPosition += attribute.getNodeName().length();
			range.add(new StyleRange(currentPosition, currentPosition + "='".length(),
					foreGroundColor != null ? foreGroundColor : darkMagenta, backgroundColor));
			currentPosition += "='".length();
			range.add(new StyleRange(currentPosition, currentPosition + attribute.getNodeValue().length(),
					foreGroundColor != null ? foreGroundColor : Display.getCurrent()
							.getSystemColor(SWT.COLOR_DARK_BLUE), backgroundColor));
			currentPosition += attribute.getNodeValue().length();
			range.add(new StyleRange(currentPosition, currentPosition + "'".length(),
					foreGroundColor != null ? foreGroundColor : darkMagenta, backgroundColor));
			currentPosition += "'".length();
		}

		cell.setStyleRanges(range.toArray(new StyleRange[range.size()]));
		cell.setImage(getImage(cell.getElement()));
		super.update(cell);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof HTMLRawElement) {
			return ImageConstants.IMG_16_TEST_OBJECT;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof HTMLRawElement) {
			return ((HTMLRawElement) element).toString();
		}
		return null;
	}

	public void setFilteredElements(List<DomElementXpath> filteredElements) {
		if (filteredElementsXpath == null) {
			filteredElementsXpath = new ArrayList<String>();
		}
		filteredElementsXpath.clear();
		if (filteredElements != null) {
			for (DomElementXpath filteredElement : filteredElements) {
				filteredElementsXpath.add(filteredElement.getXpath());
			}
		}
	}
}
