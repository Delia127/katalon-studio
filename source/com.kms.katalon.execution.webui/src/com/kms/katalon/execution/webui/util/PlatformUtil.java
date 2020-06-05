package com.kms.katalon.execution.webui.util;

import org.eclipse.core.runtime.Platform;

public class PlatformUtil {

    public static boolean isWindowsOS() {
        return Platform.OS_WIN32.equals(Platform.getOS());
    }
}
