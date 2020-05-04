package com.kms.katalon.application.helper;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.license.models.LicenseType;

public class LicenseHelper {

    public boolean isNotFreeLicense() {
        return ActivationInfoCollector.getLicenseType() != LicenseType.FREE;
    }
    
    public boolean isFreeLicense() {
    	return ActivationInfoCollector.getLicenseType() == LicenseType.FREE;
    }
    
    public boolean isNonPaidLicense() {
        LicenseType licenseType = ActivationInfoCollector.getLicenseType();
        return licenseType == LicenseType.FREE || licenseType == LicenseType.TRIAL;
    }
    
    public boolean isPaidLicense() {
        return ActivationInfoCollector.getLicenseType() == LicenseType.ENTERPRISE;
    }
}
