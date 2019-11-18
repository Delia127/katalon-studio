package com.kms.katalon.composer.mobile.util;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class ComposerUtil {
    static public StyleRange appendStyledText(StyledText styledText, String text, Color color) {
        StyleRange range = new StyleRange();
        range.start = styledText.getText().length();
        range.length = text.length();
        range.foreground = color;

        styledText.append(text);
        styledText.setStyleRange(range);
        return range;
    }
    
    static public StyleRange appendErrorText(StyledText styledText, String text) {
        return appendStyledText(styledText, text, JFaceColors.getErrorText(styledText.getDisplay()));
    }
    
    static public StyleRange appendWarningText(StyledText styledText, String text) {
        Device device = Display.getCurrent();
        Color orange = new Color(device, 255, 92, 14);
        return appendStyledText(styledText, text, orange);
    }
    
    static public StyleRange appendSuccessText(StyledText styledText, String text) {
        return appendStyledText(styledText, text, styledText.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
    }
}
