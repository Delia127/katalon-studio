package com.kms.katalon.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.util.QTestUtil;

@SuppressWarnings("restriction")
public class CheckForUpdatesMenuContribution {
    
    @Inject
    private ECommandService commandService;
    
    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        if (QTestUtil.isQTestEdition()) {
            return;
        }
        
        MHandledMenuItem checkForUpdateMenuItem = MenuFactory.createPopupMenuItem(
                commandService.createCommand("com.kms.katalon.command.checkForUpdates", null),
                "Check for Updates...",
                ConstantsHelper.getApplicationURI());
        if (checkForUpdateMenuItem != null) {
            menuItems.add(checkForUpdateMenuItem);
        }
    }
}
