package com.kms.katalon.application.utils;

import com.kms.katalon.application.helper.LicenseHelper;
import com.kms.katalon.application.helper.LicenseHelperFactory;

public class LicenseUtil {
    
    public static boolean isNotFreeLicense() {
        return getLicenseHelper().isNotFreeLicense();
    }
    
    public static boolean isFreeLicense() {
    	return getLicenseHelper().isFreeLicense();
    }
    
    public static boolean isNonPaidLicense() {
        return getLicenseHelper().isNonPaidLicense();
    }
    
    public static boolean isPaidLicense() {
        return getLicenseHelper().isPaidLicense();
    }
    
    private static LicenseHelper getLicenseHelper() {
        return LicenseHelperFactory.get();
    }
}
