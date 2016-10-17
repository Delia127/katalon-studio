package com.kms.katalon.composer.mobile.execution.testsuite;

import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class IosTestExecutionDriverEntry extends MobileTestExecutionDriverEntry {
    public IosTestExecutionDriverEntry(final String groupName) {
        super(MobileDriverType.IOS_DRIVER, groupName, ImageConstants.IMG_URL_16_APPLE);
    }
}
