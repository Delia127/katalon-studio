package com.kms.katalon.objectspy.element.tree;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.core.webui.constants.HTMLTags;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;

public class HTMLElementLabelProvider extends StyledCellLabelProvider {
    private static Image WEB_ELEMENT_MISSING_ICON = ImageManager.getImage(IImageKeys.ERROR_16);

    private static Image WEB_ELEMENT_MULTIPLE_ICON = ImageManager.getImage(IImageKeys.INFO_16);

    private static Image WEB_ELEMENT_CHANGED_ICON = ImageManager.getImage(IImageKeys.WARNING_16);

    public Image getImage(Object element) {
        if (element instanceof HTMLElement) {
            HTMLElement htmlElement = (HTMLElement) element;
            switch (htmlElement.getMatchedStatus().getStatus()) {
                case Changed:
                    return WEB_ELEMENT_CHANGED_ICON;
                case Exists:
                    return ImageConstants.IMG_16_OK;
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
                    switch (HTMLTags.getElementType(htmlElement.getType(), htmlElement.getTypeAttribute())) {
                        case HTMLTags.TAG_A:
                            return ImageConstants.IMG_16_LNK_TEST_OBJECT;
                        case HTMLTags.TAG_BUTTON:
                            return ImageConstants.IMG_16_BTN_TEST_OBJECT;
                        case HTMLTags.TAG_CHECKBOX:
                            return ImageConstants.IMG_16_CHK_TEST_OBJECT;
                        case HTMLTags.TAG_FILE:
                            return ImageConstants.IMG_16_FILE_TEST_OBJECT;
                        case HTMLTags.TAG_IMG:
                        case HTMLTags.TAG_IMAGE:
                            return ImageConstants.IMG_16_IMG_TEST_OBJECT;
                        case HTMLTags.TAG_SELECT:
                            return ImageConstants.IMG_16_CBX_TEST_OBJECT;
                        case HTMLTags.TAG_LABEL:
                            return ImageConstants.IMG_16_LBL_TEST_OBJECT;
                        case HTMLTags.TAG_TEXTAREA:
                        case HTMLTags.TAG_TEXT:
                            return ImageConstants.IMG_16_TXT_TEST_OBJECT;
                        case HTMLTags.TAG_RADIO:
                            return ImageConstants.IMG_16_RBT_TEST_OBJECT;
                        default:
                            return ImageConstants.IMG_16_TEST_OBJECT;
                    }
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
            switch (htmlElement.getMatchedStatus().getStatus()) {
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

    @Override
    protected void measure(Event event, Object element) {
        super.measure(event, element);
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            event.width += 1;
        }
    }
}
