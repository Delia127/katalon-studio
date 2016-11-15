package com.kms.katalon.composer.webui.execution.menu;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class SafariExecutionMenuContribution extends AbstractExecutionMenuContribution {
    private static final String EXISTING_SAFARI_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.execution.command.existing.safari";

    private static final String SAFARI_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.safari"; //$NON-NLS-1$

    @Override
    protected String getIconUri() {
        return ImageConstants.IMG_URL_16_SAFARI;
    }

    @Override
    protected String getDriverTypeName() {
        return WebUIDriverType.SAFARI_DRIVER.toString();
    }

    @Override
    protected String getCommandId() {
        return SAFARI_EXECUTION_COMMAND_ID;
    }

    @Override
    protected String getMenuLabel() {
        return ComposerWebuiMessageConstants.LBL_SAFARI_EXECUTION_MENU_ITEM;
    }
    
    @Override
    protected String getExistingExecutionCommandId() {
        return EXISTING_SAFARI_EXECUTION_COMMAND_ID;
    }

}
