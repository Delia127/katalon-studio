package com.kms.katalon.composer.mobile.util;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import com.kms.katalon.composer.components.util.ColorUtil;

public class RichTextUtil {
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
        return appendStyledText(styledText, text, ColorUtil.getWarningForegroudColor());
    }
    
    static public StyleRange appendSuccessText(StyledText styledText, String text) {
        return appendStyledText(styledText, text, ColorUtil.getTextSuccessfulColor());
    }
}
