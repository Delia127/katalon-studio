package com.kms.katalon.objectspy.element.tree;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.core.webui.constants.HTMLTags;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.WebElement;

public class WebElementLabelProvider extends CellLabelProvider {

    @Override
    public void update(ViewerCell cell) {
        String name = StringConstants.EMPTY;
        if (cell.getElement() != null && cell.getElement() instanceof WebElement) {
            name = ((WebElement) cell.getElement()).getName();
        }
        cell.setText(name);
        cell.setImage(getImage(cell.getElement()));
    }

    private Image getImage(Object element) {
        if (!(element instanceof WebElement)) {
            return null;
        }
        WebElement htmlElement = (WebElement) element;
        switch (htmlElement.getType()) {
            case PAGE:
                return ImageConstants.IMG_16_PAGE_OBJECT;
            case FRAME:
                return ImageConstants.IMG_16_FRAME_OBJECT;
            default:
                switch (HTMLTags.getElementType(htmlElement.getTag(), htmlElement.getTypeProperty())) {
                    case HTMLTags.TAG_A:
                        return ImageConstants.IMG_16_LNK_TEST_OBJECT;
                    case HTMLTags.TAG_RESET:
                    case HTMLTags.TAG_SUBMIT:
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
        }
    }

}
