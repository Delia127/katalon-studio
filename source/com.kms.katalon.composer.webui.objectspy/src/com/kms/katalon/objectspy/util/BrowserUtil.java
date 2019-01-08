package com.kms.katalon.objectspy.util;

import com.kms.katalon.composer.components.impl.util.PlatformUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class BrowserUtil {

    public static boolean isBrowserInstalled(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case FIREFOX_DRIVER:
                return PlatformUtil.isFirefoxInstalled();

            case CHROME_DRIVER:
                return PlatformUtil.isChromeInstalled();

            case IE_DRIVER:
                return PlatformUtil.isIEInstalled();

            case EDGE_DRIVER:
                return PlatformUtil.isEdgeInstalled();

            case SAFARI_DRIVER:
                return PlatformUtil.isSafariInstalled();

            default:
                return false;
        }
    }

}