package com.kms.katalon.composer.components.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorUtil {

    // background
    public static Color getSelectedTableItemBackgroundColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
    }

    public static Color getEvenTableItemBackgroundColor() {
        return new Color(Display.getCurrent(), 135, 206, 250);
    }

    public static Color getOddTableItemBackgroundColor() {
        return new Color(Display.getCurrent(), 243, 243, 243);
    }

    public static Color getCompositeBackgroundColor() {
        return getExtraLightGrayBackgroundColor();
    }

    public static Color getCompositeHeaderBackgroundColor() {
        return new Color(Display.getCurrent(), 67, 81, 90);
    }

    public static Color getHighlightBackgroundColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
    }

    public static Color getWhiteBackgroundColor() {
        return new Color(Display.getCurrent(), 255, 255, 255);
    }

    public static Color getBlackBackgroundColor() {
        return new Color(Display.getCurrent(), 0, 0, 0);
    }

    // #E8E8E8
    public static Color getButtonMouseOverBackgroundColor() {
        return new Color(Display.getCurrent(), 232, 232, 232);
    }

    public static Color getButtonMouseDownBackgroundColor() {
        return new Color(Display.getCurrent(), 218, 218, 218);
    }

    public static Color getPassedLogBackgroundColor() {
        return new Color(Display.getCurrent(), 91, 177, 53);
    }

    public static Color getFailedLogBackgroundColor() {
        return new Color(Display.getCurrent(), 205, 97, 97);
    }

    public static Color getWarningLogBackgroundColor() {
        return new Color(Display.getCurrent(), 247, 168, 98);
    }
    
    public static Color getIncompleteLogColor() {
        return new Color(Display.getCurrent(), 153, 117, 21);
    }

    // #F6F6F6
    public static Color getExtraLightGrayBackgroundColor() {
        return new Color(Display.getCurrent(), 246, 246, 246);
    }

    // foreground
    public static Color getTextPlaceholderColor() {
        return new Color(Display.getCurrent(), 218, 218, 218);
    }

    public static Color getDefaultTextColor() {
        return new Color(Display.getCurrent(), 0, 0, 0);
    }

    public static Color getTextWhiteColor() {
        return new Color(Display.getCurrent(), 255, 255, 255);
    }
    
    public static Color getTextErrorColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    }
    
    public static Color getTextSuccessfulColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
    }

    // #F0F0F0
    public static Color getDefaultBackgroundColor() {
        return new Color(Display.getCurrent(), 240, 240, 240);
    }

    // #F8CBCB
    public static Color getErrorTableItemBackgroundColor() {
        return new Color(Display.getCurrent(), 248, 203, 203);
    }

    // #BD2C00
    public static Color getErrorTableItemForegroundColor() {
        return new Color(Display.getCurrent(), 189, 44, 0);
    }
    
    public static Color getWarningForegroudColor() {
        return new Color(Display.getCurrent(), 255, 128, 0); 
    }
}
