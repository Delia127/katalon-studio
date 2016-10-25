package com.kms.katalon.composer.components.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.swt.widgets.Widget;

@SuppressWarnings("restriction")
public class CssUtil {

    /**
     * Key value for setting and getting the CSS class name of a widget.
     * Clients may rely on the value of this key if they want to avoid a dependency on this package.
     * 
     * @see Widget.getData(String) Widget.setData(String, Object)
     */
    public static final String CSS_CLASS_NAME_KEY = CSSSWTConstants.CSS_CLASS_NAME_KEY;

    /**
     * Key value for setting and getting the CSS ID of a widget.
     * Clients may rely on the value of this key if they want to avoid a dependency on this package.
     * 
     * @see Widget.getData(String) Widget.setData(String, Object)
     */
    public static final String CSS_ID_KEY = CSSSWTConstants.CSS_ID_KEY;

    /**
     * Apply CSS classes for the widget
     * 
     * @param widget Widget
     * @param cssClassName CSS classes (separated by space)
     * @see com.kms.katalon/css/default.css
     */
    public static void applyCssClassName(Widget widget, String cssClassName) {
        widget.setData(CSS_CLASS_NAME_KEY, cssClassName);
    }

    public static void applyCssClassName(Widget widget, String[] cssClassName) {
        applyCssClassName(widget, StringUtils.join(cssClassName, " "));
    }

    /**
     * Apply CSS to specific identifier widget
     * 
     * @param widget Widget
     * @param cssId CSS identifier for the widget. Identifier should not contain periods as they cannot otherwise be
     * referenced in a CSS selector.
     * @see com.kms.katalon/css/default.css
     */
    public static void applyCssId(Widget widget, String cssId) {
        widget.setData(CSS_ID_KEY, cssId);
    }

}
