package com.kms.katalon.composer.mobile.objectspy.element.tree;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleTreeCellLabelProvider;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.ImageConstants;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

public class MobileElementLabelProvider extends TypeCheckedStyleTreeCellLabelProvider<TreeMobileElement> {

    public MobileElementLabelProvider() {
        super(0);
    }

    @Override
    protected Class<TreeMobileElement> getElementType() {
        return TreeMobileElement.class;
    }

    @Override
    protected Image getImage(TreeMobileElement element) {
        return ImageConstants.IMG_16_TEST_OBJECT;
    }
    
    @Override
    protected String getElementToolTipText(TreeMobileElement element) {
        return getText(element);
    }

    @Override
    protected String getText(TreeMobileElement element) {
        return element.getName();
    }

    @Override
    protected StyleRange[] getStyleRanges(ViewerCell cell, TreeMobileElement element) {
        CapturedMobileElement capturedElement = element.getCapturedElement();
        if (capturedElement == null) {
            return super.getStyleRanges(cell, element);
        }
        
        String aliasName = capturedElement.getName();
        StyledString styledString = new StyledString()
                .append(aliasName, new BoldStyler(cell.getFont()));
        
        if (!aliasName.equals(element.getName())) {
            styledString.append(" " + element.getName() + " ", StyledString.DECORATIONS_STYLER);
        }
        
        cell.setText(styledString.getString());
        return styledString.getStyleRanges();
    }

    private class BoldStyler extends Styler {

        private Font currentFont;

        private BoldStyler(Font font) {
            this.currentFont = font;
        }

        @Override
        public void applyStyles(final TextStyle textStyle) {
            FontDescriptor boldDescriptor = FontDescriptor.createFrom(currentFont.getFontData()[0]).setStyle(SWT.BOLD);
            Font boldFont = boldDescriptor.createFont(Display.getCurrent());
            textStyle.foreground = ColorUtil.getTextSuccessfulColor();
            textStyle.font = boldFont;
        }
    }
}
