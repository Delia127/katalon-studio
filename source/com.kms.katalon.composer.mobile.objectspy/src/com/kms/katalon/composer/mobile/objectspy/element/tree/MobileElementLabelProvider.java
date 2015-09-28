package com.kms.katalon.composer.mobile.objectspy.element.tree;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;

public class MobileElementLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

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
	public Image getImage(Object element) {
		if (element instanceof MobileElement) {
			return ImageConstants.IMG_16_TEST_OBJECT;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof MobileElement) {
			return ((MobileElement) element).getName();
		}
		return null;
	}
}
