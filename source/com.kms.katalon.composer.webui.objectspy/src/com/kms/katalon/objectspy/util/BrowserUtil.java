package com.kms.katalon.objectspy.util;

import com.kms.katalon.composer.components.impl.util.PlatformUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class BrowserUtil {

    public static boolean isBrowserInstalled(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {

            default:
                return true;
        }
    }

}
