package com.kms.katalon.composer.webui.execution.menu;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class ChromeExecutionMenuContribution extends AbstractExecutionMenuContribution {
    private static final String CHROME_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.chrome"; //$NON-NLS-1$

    @Override
    protected String getIconUri() {
        return ImageConstants.IMG_URL_16_CHROME;
    }

    @Override
    protected String getDriverTypeName() {
        return WebUIDriverType.CHROME_DRIVER.toString();
    }

    @Override
    protected String getCommandId() {
        return CHROME_EXECUTION_COMMAND_ID;
    }

    @Override
    protected String getMenuLabel() {
        return ComposerWebuiMessageConstants.LBL_CHROME_EXECUTION_MENU_ITEM;
    }
}
