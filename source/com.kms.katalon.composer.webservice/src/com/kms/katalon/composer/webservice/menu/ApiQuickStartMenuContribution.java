package com.kms.katalon.composer.webservice.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class ApiQuickStartMenuContribution {

    private static final String OPEN_QUICK_START_DIALOG_COMMAND_ID = 
            "com.kms.katalon.composer.webservice.command.quickstart";
    
    @Inject
    private ECommandService commandService;
    
    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return;
        }
        MHandledMenuItem quickStartMenuItem = MenuFactory.createPopupMenuItem(
                commandService.createCommand(OPEN_QUICK_START_DIALOG_COMMAND_ID, null),
                StringConstants.TITLE_QUICKSTART, ConstantsHelper.getApplicationURI());
        if (quickStartMenuItem != null) {
            menuItems.add(quickStartMenuItem);
        }
    }
}
