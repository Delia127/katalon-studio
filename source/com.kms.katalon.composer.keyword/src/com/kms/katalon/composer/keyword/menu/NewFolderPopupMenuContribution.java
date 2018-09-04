package com.kms.katalon.composer.keyword.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.keyword.handlers.NewFolderHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

@SuppressWarnings("restriction")
public class NewFolderPopupMenuContribution extends NewFolderHandler {
    private static final String NEW_FOLDER_POPUP_MENUITEM_LABEL = "New Folder";

    private static final String NEW_FOLDER_COMMAND_ID = "com.kms.katalon.composer.keyword.command.addFolder";

    @Inject
    ESelectionService selectionService;

    @Inject
    ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        FolderTreeEntity selectedEntity = getSelectedTreeEntity(selectedObjects);
        if (selectedEntity == null) {
            return;
        }
        try {
            FolderEntity folder = selectedEntity.getObject();
            if (FolderController.getInstance()
                    .isSourceFolder(ProjectController.getInstance().getCurrentProject(), folder)) {
                return;
            }
            MHandledMenuItem newKeywordPopupMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_FOLDER_COMMAND_ID, null), NEW_FOLDER_POPUP_MENUITEM_LABEL,
                    ConstantsHelper.getApplicationURI());
            if (newKeywordPopupMenuItem != null) {
                menuItems.add(newKeywordPopupMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
