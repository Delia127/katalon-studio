package com.kms.katalon.composer.windows.menu;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;

public class WindowsExecutionMenuContribution extends AbstractExecutionMenuContribution {
    private static final String WINDOWS_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.windows.execution.command"; //$NON-NLS-1$
    
    @Override
    protected String getIconUri() {
        return ImageManager.getImageURLString(IImageKeys.WINDOWS_ENTITY_16);
    }

    @Override
    protected String getDriverTypeName() {
        return "Windows";
    }

    @Override
    protected String getCommandId() {
        return WINDOWS_EXECUTION_COMMAND_ID;
    }

    @Override
    protected String getMenuLabel() {
        return "Windows";
    }

}
