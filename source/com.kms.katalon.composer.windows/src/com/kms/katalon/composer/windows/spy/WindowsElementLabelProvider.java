package com.kms.katalon.composer.windows.spy;

import org.apache.commons.lang3.StringUtils;
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
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.TreeWindowsElement;

public class WindowsElementLabelProvider extends TypeCheckedStyleTreeCellLabelProvider<TreeWindowsElement> {

    public WindowsElementLabelProvider() {
        super(0);
    }

    @Override
    protected Class<TreeWindowsElement> getElementType() {
        return TreeWindowsElement.class;
    }

    @Override
    protected Image getImage(TreeWindowsElement element) {
        return ImageConstants.IMG_16_TEST_OBJECT;
    }
    
    @Override
    protected String getElementToolTipText(TreeWindowsElement element) {
        return getText(element);
    }

    @Override
    protected String getText(TreeWindowsElement element) {
        return element.getName();
    }

    @Override
    protected StyleRange[] getStyleRanges(ViewerCell cell, TreeWindowsElement element) {        
        StyledString styledString = new StyledString(cell.getText());

        CapturedWindowsElement capturedElement = element.getCapturedElement();
        if (capturedElement != null) {
            String capturedElementName = capturedElement.getName();
            if (StringUtils.isNotEmpty(capturedElementName)) {
                styledString.append(" " + capturedElementName, new BoldStyler(cell.getFont()));
            }
        }

        String optionalName = element.getOptinalName().split("(\n|(\r\n))")[0];
        if (StringUtils.isNotEmpty(optionalName)) {
            styledString.append(" " + optionalName, StyledString.COUNTER_STYLER);
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
