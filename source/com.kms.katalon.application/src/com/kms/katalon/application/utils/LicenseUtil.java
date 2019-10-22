package com.kms.katalon.application.utils;

import com.kms.katalon.license.models.LicenseType;

public class LicenseUtil {
    
    public static boolean isNotFreeLicense() {
        return ActivationInfoCollector.getLicenseType() != LicenseType.FREE;
    }
}
