package com.kms.katalon.selenium.util;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Keys;

public class SeleniumKeysUtil {
    private SeleniumKeysUtil() {
        // Disable default constructor
    }

    /**
     * Return a visualized text with the format: <code>Key_1 + Key_2 + ... + Normal_Key</code></br>
     * Example:
     * <ul>
     * <li>SHIFT + Katalon</li>
     * <li>COMMAND + S</li>
     * <li>CTRL + SHIFT + F5</li>
     * </ul>
     * 
     * @param text the unicode text, can be null or empty.
     */
    public static String getReadableText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        List<Keys> lstKeys = new ArrayList<>();
        StringBuilder normalText = new StringBuilder();
        for (char c : text.toCharArray()) {
            Keys keys = Keys.getKeyFromUnicode(c);
            if (keys == null) {
                normalText.append(c);
                continue;
            }
            if (keys != Keys.NULL) {
                lstKeys.add(keys);
                continue;
            }
        }
        StringBuilder readableText = new StringBuilder();
        lstKeys.forEach(keys -> {
            String keyName = keys.name();
            if (keys == Keys.META) {
                keyName = Keys.COMMAND.name();
            }
            readableText.append(String.format("%s + ", keyName));
        });
        readableText.append(normalText.toString());
        return readableText.toString();
    }

}
