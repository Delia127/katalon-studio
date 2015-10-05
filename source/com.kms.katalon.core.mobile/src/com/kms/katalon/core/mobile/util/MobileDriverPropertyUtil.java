package com.kms.katalon.core.mobile.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class MobileDriverPropertyUtil {

    public static DesiredCapabilities toDesireCapabilities(Map<String, Object> propertyMap, MobileDriverType mobileDriverType) {
        switch (mobileDriverType) {
        default:
            DesiredCapabilities desireCapabilities = new DesiredCapabilities();
            for (Entry<String, Object> property : propertyMap.entrySet()) {
                KeywordLogger.getInstance().logInfo(
                        MessageFormat.format(StringConstants.KW_LOG_MOBILE_PROPERTY_SETTING, property.getKey(),
                                property.getValue()));
                desireCapabilities.setCapability(property.getKey(), property.getValue());
            }
            return desireCapabilities;
        }
    }
}
