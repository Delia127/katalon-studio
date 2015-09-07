package com.kms.katalon.objectspy.element.tree;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;

public class HTMLElementLabelProvider extends StyledCellLabelProvider {
	private static Image WEB_ELEMENT_MISSING_ICON = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	private static Image WEB_ELEMENT_MULTIPLE_ICON = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
	private static Image WEB_ELEMENT_CHANGED_ICON = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_WARN_TSK);

	public Image getImage(Object element) {
		if (element instanceof HTMLElement) {
			HTMLElement htmlElement = (HTMLElement) element;
			switch (htmlElement.getStatus()) {
			case Changed:
				return WEB_ELEMENT_CHANGED_ICON;
			case Exists:
				return ImageConstants.IMG_16_DONE;
			case Missing:
				return WEB_ELEMENT_MISSING_ICON;
			case Multiple:
				return WEB_ELEMENT_MULTIPLE_ICON;
			case NotVerified:
				if (element instanceof HTMLPageElement) {
					return ImageConstants.IMG_16_PAGE_OBJECT;
				} else if (element instanceof HTMLFrameElement) {
					return ImageConstants.IMG_16_FRAME_OBJECT;
				}
				return ImageConstants.IMG_16_TEST_OBJECT;
			case Invalid:
				return ImageConstants.IMG_16_BUG;
			}
		}
		return null;
	}

	@Override
	public Image getToolTipImage(Object object) {
		return getImage(object);
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof HTMLElement) {
			HTMLElement htmlElement = (HTMLElement) element;
			switch (htmlElement.getStatus()) {
			case Changed:
				return StringConstants.TREE_ELEMENT_TIP_ATTRIBUTES_CHANGED;
			case Exists:
				return StringConstants.TREE_ELEMENT_TIP_EXISTED;
			case Missing:
				return StringConstants.TREE_ELEMENT_TIP_MISSING;
			case Multiple:
				return StringConstants.TREE_ELEMENT_TIP_FOUND_MULTIPLE_ELEM;
			case NotVerified:
				return StringConstants.TREE_ELEMENT_TIP_IS_NOT_VERIFIED;
			case Invalid:
				return StringConstants.TREE_ELEMENT_TIP_INVALID_XPATH;
			}
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof HTMLElement) {
			return ((HTMLElement) element).getName();
		}
		return null;
	}

	@Override
	public void update(ViewerCell cell) {
		StyledString styledString = new StyledString();

		if (cell.getElement() != null && cell.getElement() instanceof HTMLElement) {
			styledString.append(((HTMLElement) cell.getElement()).getName());
		}

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());
		cell.setImage(getImage(cell.getElement()));
		super.update(cell);
	}
}
