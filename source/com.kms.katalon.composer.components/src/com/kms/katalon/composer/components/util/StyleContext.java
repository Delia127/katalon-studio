package com.kms.katalon.composer.components.util;

import java.util.Stack;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

public class StyleContext {

    private static Color color;

    private static Stack<Color> colorStack = new Stack<>();

    private static Color background;

    private static Stack<Color> backgroundStack = new Stack<>();

    private static boolean isEnable;

    private static Font font;

    private static Stack<Font> fontStack = new Stack<>();

    public static void begin() {
        isEnable = true;
    }

    public static void end() {
        isEnable = false;
    }

    public static boolean isEnable() {
        return isEnable;
    }

    public static void style(Control control) {
        if (!isEnable) {
            return;
        }

        if (getColor() != null) {
            control.setForeground(getColor());
        }
        if (getBackground() != null) {
            control.setBackground(getBackground());
        }
        if (getFont() != null) {
            control.setFont(getFont());
        }
    }

    // Getters / Setters

    public static Color getColor() {
        return color;
    }

    public static void setColor(Color color) {
        colorStack.add(StyleContext.color);
        StyleContext.color = color;
    }

    public static void prevColor() {
        StyleContext.color = colorStack.size() > 0
                ? colorStack.pop()
                : null;
    }

    public static Color getBackground() {
        return background;
    }

    public static void setBackground(Color background) {
        backgroundStack.add(StyleContext.background);
        StyleContext.background = background;
    }

    public static void prevBackground() {
        StyleContext.background = backgroundStack.size() > 0
                ? backgroundStack.pop()
                : null;
    }

    public static Font getFont() {
        return font;
    }

    public static void setFont(Font font) {
        fontStack.add(StyleContext.font);
        StyleContext.font = font;
    }

    public static void setFontSize(int fontSize) {
        Font currentFont = StyleContext.font;
        setFont(FontUtil.size(currentFont != null
                ? currentFont
                : FontUtil.REGULAR, fontSize));
    }

    public static void prevFont() {
        StyleContext.font = fontStack.size() > 0
                ? fontStack.pop()
                : null;
    }
}
