package com.kms.katalon.composer.components.util;

import org.eclipse.e4.ui.css.swt.helpers.CSSSWTColorHelper;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.ComponentBundleActivator;

@SuppressWarnings("restriction")
public class ColorUtil {

    private static final String DEFAULT_COMPOSITE_BACKGROUND_COLOR_FOR_DARK_THEME = "#2F2E2F";
    
    private static final String DEFAULT_COMPOSITE_BACKGROUND_COLOR_FOR_DARK_THEME_DIALOG = "#505658";

    private static final String EVEN_TABLE_ITEM_BACKGROUND_COLOR = "#87cefa";

    private static final String ODD_TABLE_ITEM_BACKGROUND_COLOR = "#f3f3f3";

    public static final String PASSED_LOG_BACKGROUND_COLOR = "#5bb135";

    public static final String FAILED_LOG_BACKGROUND_COLOR = "#ce2c2c";

    private static final String ERROR_LOG_BACKGROUND_COLOR = "#ec5c51";

    private static final String WARNING_LOG_BACKGROUND_COLOR = "#ffd54f";

    private static final String INCOMPLETE_LOG_COLOR = "#997515";

    public static final String FAILED_STATUS_BACKGROUND_COLOR = "#f19696";

    public static final String INCOMPLETE_STATUS_BACKGROUND_COLOR = "#f2bc70";

    private static final String EXTRA_LIGHT_GRAY_COLOR = "#f6f6f6";

    private static final String TEXT_PLACEHOLDER_COLOR = "#dadada";

    private static final String TOOLTIP_TEXT_COLOR = "#645454";

    private static final String DEFAULT_COMPOSITE_BACKGROUND_COLOR = "#f0f0f0";

    private static final String ERROR_BACKGROUND_COLOR = "#f8cbcb";

    private static final String ERROR_TEXT_COLOR = "#d50000";

    private static final String WARNING_TEXT_COLOR = "#ff8000";

    private static final String TOOLBAR_BACKGROUND_COLOR = "#e8e8e8";

    private static final String COMPOSITE_HEADER_BACKGROUND_COLOR = "#43515a";
    
    private static final String DISABLED_TEXT_COLOR = "#bdbdbd";

    private static Display display = Display.getCurrent();

    /**
     * Get SWT Color from RGB color
     * 
     * @param red
     * @param green
     * @param blue
     * @return Color
     */
    public static Color getColor(int red, int green, int blue) {
        return new Color(display, new RGB(red, green, blue));
    }

    /**
     * Get SWT Color from HEX color
     * 
     * @param hexColor HEX color. E.g {@code #ffffff}
     * @return Color
     */
    public static Color getColor(String hexColor) {
        return new Color(display, CSSSWTColorHelper.getRGBA(hexColor));
    }

    public static Color getSelectedTableItemBackgroundColor() {
        return display.getSystemColor(SWT.COLOR_LIST_SELECTION);
    }

    public static Color getEvenTableItemBackgroundColor() {
        return getColor(EVEN_TABLE_ITEM_BACKGROUND_COLOR);
    }

    public static Color getOddTableItemBackgroundColor() {
        return getColor(ODD_TABLE_ITEM_BACKGROUND_COLOR);
    }

    public static Color getCompositeBackgroundColor() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return getColor(DEFAULT_COMPOSITE_BACKGROUND_COLOR_FOR_DARK_THEME);
        } else {
            return getColor(DEFAULT_COMPOSITE_BACKGROUND_COLOR);
        }
    }
    
    public static Color getCompositeBackgroundColorForDialog() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return getColor(DEFAULT_COMPOSITE_BACKGROUND_COLOR_FOR_DARK_THEME_DIALOG);
        } else {
            return getColor(DEFAULT_COMPOSITE_BACKGROUND_COLOR);
        }
    }

    public static Color getCompositeHeaderBackgroundColor() {
        return getColor(COMPOSITE_HEADER_BACKGROUND_COLOR);
    }

    public static Color getHighlightBackgroundColor() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return getColor("#ef6c00");
        } else {
            return getColor("#ffeb3b");
        }
    }

    public static Color getWhiteBackgroundColor() {
        return display.getSystemColor(SWT.COLOR_WHITE);
    }

    public static Color getBlackBackgroundColor() {
        return display.getSystemColor(SWT.COLOR_BLACK);
    }

    public static Color getButtonMouseOverBackgroundColor() {
        return getColor(TOOLBAR_BACKGROUND_COLOR);
    }

    public static Color getButtonMouseDownBackgroundColor() {
        return getColor(TEXT_PLACEHOLDER_COLOR);
    }

    // #5bb135 (light green)
    public static Color getPassedLogBackgroundColor() {
        return getColor(PASSED_LOG_BACKGROUND_COLOR);
    }

    public static Color getFailedLogBackgroundColor() {
        return getColor(FAILED_LOG_BACKGROUND_COLOR);
    }

    // #ec5c51
    public static Color getErrorLogBackgroundColor() {
        return getColor(ERROR_LOG_BACKGROUND_COLOR);
    }

    public static Color getRunningLogBackgroundColor() {
        return getColor(WARNING_LOG_BACKGROUND_COLOR);
    }

    public static Color getWarningLogBackgroundColor() {
        return getColor(WARNING_LOG_BACKGROUND_COLOR);
    }

    public static Color getIncompleteLogColor() {
        return getColor(INCOMPLETE_LOG_COLOR);
    }

    public static Color getFailedStatusBackgroundColor() {
        return getColor(FAILED_STATUS_BACKGROUND_COLOR);
    }

    public static Color getIncompleteStatusBackgroundColor() {
        return getColor(INCOMPLETE_STATUS_BACKGROUND_COLOR);
    }

    public static Color getExtraLightGrayBackgroundColor() {
        return getColor(EXTRA_LIGHT_GRAY_COLOR);
    }

    public static Color getTextPlaceholderColor() {
        return getColor(TEXT_PLACEHOLDER_COLOR);
    }

    public static Color getDefaultTextColor() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return getColor("#CCCCCC");
        } else {
            return display.getSystemColor(SWT.COLOR_BLACK);
        }
    }

    public static Color getTextWhiteColor() {
        return display.getSystemColor(SWT.COLOR_WHITE);
    }
    
    public static Color getTextBlackColor() {
        return display.getSystemColor(SWT.COLOR_BLACK);
    }

    public static Color getTextLinkColor() {
        return display.getSystemColor(SWT.COLOR_LINK_FOREGROUND);
    }

    public static Color getTextErrorColor() {
        return JFaceColors.getErrorText(display);
    }

    public static Color getTextSuccessfulColor() {
        return display.getSystemColor(SWT.COLOR_DARK_GREEN);
    }

    public static Color getTooltipPlaceHolderForegroundColor() {
        return getColor(TOOLTIP_TEXT_COLOR);
    }

    public static Color getDefaultBackgroundColor() {
        return getColor(DEFAULT_COMPOSITE_BACKGROUND_COLOR);
    }

    public static Color getErrorTableItemBackgroundColor() {
        return getColor(ERROR_BACKGROUND_COLOR);
    }

    public static Color getErrorTableItemForegroundColor() {
        return JFaceColors.getErrorText(display);
    }

    public static Color getWarningForegroudColor() {
        return getColor(WARNING_TEXT_COLOR);
    }

    public static Color getDisabledItemBackgroundColor() {
        return display.getSystemColor(SWT.COLOR_GRAY);
    }

    public static Color getUnEditableTableCellBackgroundColor() {
        return display.getSystemColor(SWT.COLOR_GRAY);
    }

    public static Color getHighlightForegroundColor() {
        return display.getSystemColor(SWT.COLOR_LINK_FOREGROUND);
    }

    public static Color getToolBarBackgroundColor() {
        return getColor(TOOLBAR_BACKGROUND_COLOR);
    }
    
    public static Color getDisabledTextColor() {
        return getColor(DISABLED_TEXT_COLOR);
    }
    
    public static Color getHintForegroundColor() {
        return getColor("#DC923C");
    }
    
    public static Color getTextColor() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return display.getSystemColor(SWT.COLOR_WHITE);
        } else {
            return display.getSystemColor(SWT.COLOR_BLACK);
        }
    }

    public static Color getToolBarForegroundColor() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return display.getSystemColor(SWT.COLOR_WHITE);
        } else {
            return display.getSystemColor(SWT.COLOR_BLACK);
        }
    }
    
    public static Color getCucumberCommentColor() {
        if (ComponentBundleActivator.isDarkTheme(display)) {
            return JFaceColors.getErrorText(display);
        } else {
            return display.getSystemColor(SWT.COLOR_DARK_RED);
        }
    }

    public static Color getHyperlinkTextColor() {
        return JFaceColors.getHyperlinkText(display);
    }
}
