package com.kms.katalon.composer.webui.execution.menu;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.execution.menu.AbstractExecutionMenuContribution;
import com.kms.katalon.composer.webui.constants.StringConstants;

public class EdgeExecutionDynamicContribution extends AbstractExecutionMenuContribution {
    private static final String EDGE_ICON_URI = "platform:/plugin/com.kms.katalon.composer.webui/icons/edge.png";

    private static final String EDGE_EXECUTION_COMMAND_ID = "com.kms.katalon.composer.webui.execution.command.edge";
    
    @Override
    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        if (!Platform.getOS().equals(Platform.OS_WIN32)) {
            return;
        }
        super.aboutToShow(items);
    }

    @Override
    protected String getIconUri() {
        return EDGE_ICON_URI;
    }
    
    @Override
    protected String getMenuLabel() {
        return StringConstants.LBL_EDGE_EXECUTION_MENU_ITEM;
    }

    @Override
    protected String getCommandId() {
        return EDGE_EXECUTION_COMMAND_ID;
    }
}
