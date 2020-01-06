package com.kms.katalon.application.utils;

import com.kms.katalon.application.helper.LicenseHelper;
import com.kms.katalon.application.helper.LicenseHelperFactory;

public class LicenseUtil {
    
    public static boolean isNotFreeLicense() {
        return getLicenseHelper().isNotFreeLicense();
    }
    
    public static boolean isPaidLicense() {
        return getLicenseHelper().isPaidLicense();
    }
    
    private static LicenseHelper getLicenseHelper() {
        return LicenseHelperFactory.get();
    }
}
