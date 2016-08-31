package com.kms.katalon.composer.mobile.execution.menu;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.mobile.constants.StringConstants;

public class IosExecutionDynamicContribution extends AbstractExecutionMenuContribution {
    private static final String IOS_ICON_URI = "platform:/plugin/com.kms.katalon.composer.mobile/icons/apple_16.png";

    private static final String IOS_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.mobile.execution.command.ios";
    
    @Override
    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        if (!Platform.getOS().equals(Platform.OS_MACOSX)) {
            return;
        }
        super.aboutToShow(items);
    }

    @Override
    protected String getIconUri() {
        return IOS_ICON_URI;
    }
    
    @Override
    protected String getMenuLabel() {
        return StringConstants.LBL_IOS_EXECUTION_MENU_ITEM;
    }

    @Override
    protected String getCommandId() {
        return IOS_EXECUTION_COMMAND_ID;
    }

}
