package com.kms.katalon.composer.mobile.execution.testsuite;

import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class AndroidTestExecutionDriverEntry extends MobileTestExecutionDriverEntry {
    public AndroidTestExecutionDriverEntry(final String groupName) {
        super(MobileDriverType.ANDROID_DRIVER, groupName, ImageConstants.IMG_URL_16_ANDROID);
    }
}
