package com.kms.katalon.composer.components.impl.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swt.events.KeyEvent;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class KeyEventUtil {

    /**
     * @param keyEvent {@link org.eclipse.swt.events.KeyEvent}
     * @param keys Key name. This could be a key combination such as {@code M1+C}, {@code M1+Shift+S}. There is NO space
     * in key combination
     * @return {@code true} if keys match, {@code false} otherwise
     * @see #getKeys(String[])
     * @see org.eclipse.jface.bindings.keys.IKeyLookup
     */
    public static boolean isKeysPressed(KeyEvent keyEvent, String keys) {
        try {
            return getKeysPressed(keyEvent).equalsIgnoreCase(KeyStroke.getInstance(keys).format());
        } catch (ParseException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    /**
     * @param keyEvent {@link org.eclipse.swt.events.KeyEvent}
     * @param keys key name array
     * @return {@code true} if keys match, {@code false} otherwise
     * @see #getKeys(String[])
     * @see org.eclipse.jface.bindings.keys.IKeyLookup
     * @return
     */
    public static boolean isKeysPressed(KeyEvent keyEvent, String[] keys) {
        return isKeysPressed(keyEvent, getKeys(keys));
    }

    public static boolean isKeysPressed(KeyEvent keyEvent, int naturalKey) {
        return isKeysPressed(keyEvent, KeyStroke.NO_KEY, naturalKey);
    }

    public static boolean isKeysPressed(KeyEvent keyEvent, int modifierKey, int naturalKey) {
        return getKeysPressed(keyEvent).equalsIgnoreCase(KeyStroke.getInstance(modifierKey, naturalKey).format());
    }

    /**
     * @param keys key name array.
     * @return The key combination string
     * @example <code>new String[] {IKeyLookup.M1_NAME, IKeyLookup.SHIFT_NAME, "S"}</code> will
     * return <code>M1+SHIFT+S</code>
     */
    public static String getKeys(String[] keys) {
        if (keys == null || keys.length == 0) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(keys, KeyStroke.KEY_DELIMITER);
    }

    /**
     * Get native key label. This will resolve the key label between Operating Systems.
     * 
     * @param keys key name array.
     * @return The native key combination string.
     */
    public static String geNativeKeyLabel(String[] keys) {
        String keysCombination = getKeys(keys);
        try {
            return KeyStroke.getInstance(keysCombination).format();
        } catch (ParseException e) {
            LoggerSingleton.logError(e);
        }
        return keysCombination;
    }

    private static String getKeysPressed(KeyEvent keyEvent) {
        return KeyStroke.getInstance(keyEvent.stateMask, keyEvent.keyCode).format();
    }

}
