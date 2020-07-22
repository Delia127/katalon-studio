package com.kms.katalon.composer.components.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

import com.kms.katalon.composer.components.constants.ComponentConstants;

public class ComponentDataUtil {

    public static <T> T get(Control control, String key, T defaultValue) {
        if (control == null || control.isDisposed()) {
            return defaultValue;
        }
        return valueOrDefault(control.getData(key), defaultValue);
    }

    public static void set(Control control, Object data) {
        control.setData(data);
    }

    public static void set(Control control, String key, Object value) {
        control.setData(key, value);
    }

    public static boolean has(Control control, String key) {
        return control.getData(key) != null;
    }

    public static String getText(Control control, String key) {
        return getText(control, key, StringUtils.EMPTY);
    }

    public static String getText(Control control, String key, String defaultValue) {
        return get(control, key, defaultValue);
    }

    public static boolean getBoolean(Control control, String key) {
        return getBoolean(control, key, false);
    }

    public static boolean getBoolean(Control control, String key, boolean defaultValue) {
        return get(control, key, defaultValue);
    }

    public static int getInt(Control control, String key) {
        return getInt(control, key, 0);
    }

    public static int getInt(Control control, String key, int defaultValue) {
        return get(control, key, defaultValue);
    }

    public static Color getColor(Control control, String key) {
        return get(control, key, null);
    }

    public static Image getImage(Control control, String key) {
        return get(control, key, null);
    }

    public static String getText(Control control) {
        try {
            Method getText = control.getClass().getMethod("getText");
            if (getText != null) {
                return (String) getText.invoke(control);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
        return null;
    }

    public static void setText(Control control, String text) {
        try {
            Method setText = control.getClass().getMethod("setText", String.class);
            if (setText != null) {
                setText.invoke(control, text);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
    }

    public static Image getImage(Control control) {
        try {
            Method getImage = control.getClass().getMethod("getImage");
            if (getImage != null) {
                return (Image) getImage.invoke(control);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
        return null;
    }

    public static void setImage(Control control, Image image) {
        try {
            Method setImage = control.getClass().getMethod("setImage", Image.class);
            if (setImage != null) {
                setImage.invoke(control, image);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
    }

    public static MouseTrackListener getMouseTrackListener(Control control) {
        Listener[] listeners = control.getListeners(SWT.MouseEnter);
        if (listeners == null || listeners.length <= 0) {
            return null;
        }
        return (MouseTrackListener) ((TypedListener) listeners[0]).getEventListener();
    }

    public static boolean shouldUseCustomRender(Control control) {
        return shouldTrackMouseMove(control) || shouldTrackMouseAction(control)
                || has(control, ComponentConstants.CONTROL_BORDER_RADIUS);
    }

    public static boolean shouldTrackMouseMove(Control control) {
        return has(control, ComponentConstants.CONTROL_HOVER_BACKGROUND)
                || has(control, ComponentConstants.CONTROL_HOVER_COLOR);
    }

    public static boolean shouldTrackMouseAction(Control control) {
        return has(control, ComponentConstants.CONTROL_ACTIVE_BACKGROUND)
                || has(control, ComponentConstants.CONTROL_ACTIVE_COLOR);
    }

    @SuppressWarnings("unchecked")
    public static <T> T valueOrDefault(Object value, T defaultValue) {
        return value != null
                ? (T) value
                : defaultValue;
    }
}
