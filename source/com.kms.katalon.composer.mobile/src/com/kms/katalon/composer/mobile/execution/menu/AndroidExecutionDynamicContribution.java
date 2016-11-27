package com.kms.katalon.composer.mobile.execution.menu;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.mobile.constants.ComposerMobileMessageConstants;
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class AndroidExecutionDynamicContribution extends AbstractExecutionMenuContribution {
    private static final String ANDROID_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.mobile.execution.command.android"; //$NON-NLS-1$

    @Override
    protected String getIconUri() {
        return ImageConstants.IMG_URL_16_ANDROID;
    }

    @Override
    protected String getMenuLabel() {
        return ComposerMobileMessageConstants.LBL_ANDROID_EXECUTION_MENU_ITEM;
    }

    @Override
    protected String getCommandId() {
        return ANDROID_EXECUTION_COMMAND_ID;
    }

    @Override
    protected String getDriverTypeName() {
        return MobileDriverType.ANDROID_DRIVER.toString();
    }

}
