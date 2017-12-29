package com.kms.katalon.execution.mobile.util;

import org.apache.commons.lang3.StringUtils;

public final class OSUtil {
    private OSUtil() {
        // Disable default constructor
    }

    public static String toOSString(String s) {
        if (s == null || s.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return s.replace(" ", "\\ ");
    }
}
