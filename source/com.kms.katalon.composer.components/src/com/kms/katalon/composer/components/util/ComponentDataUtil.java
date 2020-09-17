package com.kms.katalon.composer.components.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
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
        if (control == null || control.isDisposed()) {
            return;
        }
        control.setData(data);
    }

    public static void set(Control control, String key, Object value) {
        if (control == null || control.isDisposed()) {
            return;
        }
        control.setData(key, value);
    }

    public static boolean has(Control control, String key) {
        if (control == null || control.isDisposed()) {
            return false;
        }
        return control.getData(key) != null;
    }

    public static <T> T invokeGet(Control control, String getterMethodName) {
        if (control == null || control.isDisposed()) {
            return null;
        }
        return invokeGet(control, getterMethodName, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeGet(Object object, String getterMethodName, T defaultValue) {
        try {
            Method getter = object.getClass().getMethod(getterMethodName);
            if (getter != null) {
                return (T) getter.invoke(object);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
        return null;
    }

    public static <T> T getField(Object object, String fieldName) {
        return getField(object, fieldName, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Object object, String fieldName, T defaultValue) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            if (field == null) {
                return defaultValue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return (T) field.get(object);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException
                | NoSuchFieldException exception) {
            // Just skip
        }
        return defaultValue;
    }

    public static <T> void invokeSet(Control control, String setterMethodName, T data, Class<?> type) {
        if (control == null || control.isDisposed()) {
            return;
        }
        try {
            Method setter = control.getClass().getMethod(setterMethodName, type);
            if (setter != null) {
                setter.invoke(control, data);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            // Just skip
        }
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

    public static int getWidth(Control control) {
        if (control == null) {
            return 0;
        }

        Object layoutData = control.getLayoutData();
        if (layoutData == null || !(layoutData instanceof GridData)) {
            return control.getSize().x;
        }

        GridData gridData = (GridData) control.getLayoutData();
        int widthHint = gridData.widthHint;
        return widthHint >= 0 ? widthHint : getField(gridData, "cacheWidth", 0);
    }

    public static Color getColor(Control control, String key) {
        return get(control, key, null);
    }

    public static Image getImage(Control control, String key) {
        return get(control, key, null);
    }

    public static String getText(Control control) {
        String text = invokeGet(control, "getText");
        if (StringUtils.isEmpty(text)) {
            text = getText(control, ComponentConstants.CONTROL_PROP_TEXT);
        }
        return text;
    }

    public static void setText(Control control, String text) {
        invokeSet(control, "setText", text, String.class);
    }

    public static Image getImage(Control control) {
        return invokeGet(control, "getImage");
    }

    public static void setImage(Control control, Image image) {
        invokeSet(control, "setImage", image, Image.class);
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
