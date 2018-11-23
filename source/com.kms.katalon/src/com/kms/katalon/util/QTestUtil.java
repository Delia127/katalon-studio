package com.kms.katalon.util;

import org.eclipse.core.runtime.Platform;

public class QTestUtil {
    
    public static boolean isQTestEdition() {
        return Platform.getBundle("com.kms.katalon.integration.qtest") != null;
    }
}
