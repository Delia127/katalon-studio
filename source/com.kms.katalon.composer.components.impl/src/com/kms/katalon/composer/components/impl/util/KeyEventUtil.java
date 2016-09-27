package com.kms.katalon.composer.components.impl.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class KeyEventUtil {

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

}
