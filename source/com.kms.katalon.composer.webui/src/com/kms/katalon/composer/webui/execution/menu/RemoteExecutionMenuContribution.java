package com.kms.katalon.composer.webui.execution.menu;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteExecutionMenuContribution extends AbstractExecutionMenuContribution {
    private static final String REMOTE_WEB_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.remoteweb"; //$NON-NLS-1$
    
    @Override
    protected String getIconUri() {
        return ImageConstants.IMG_URL_16_REMOTE_WEB;
    }

    @Override
    protected String getDriverTypeName() {
        return WebUIDriverType.REMOTE_WEB_DRIVER.toString();
    }

    @Override
    protected String getCommandId() {
        return REMOTE_WEB_EXECUTION_COMMAND_ID;
    }

    @Override
    protected String getMenuLabel() {
        return ComposerWebuiMessageConstants.LBL_REMOTE_EXECUTION_MENU_ITEM;
    }

}
