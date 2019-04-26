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
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.handlers.NewFolderHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

@SuppressWarnings("restriction")
public class CustomKeywordPluginMenuContribution extends NewFolderHandler {

    private static final String CUSTOM_KEYWORD_PLUGIN_COMMAND_ID = "com.kms.katalon.composer.keyword.command.customKeywordPlugin";
    @Inject
    private ESelectionService selectionService;

    @Inject
    private ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        FolderTreeEntity selectedEntity = getSelectedTreeEntity(selectedObjects);
        if (selectedEntity == null) {
            return;
        }
        try {
            FolderEntity folder = selectedEntity.getObject();
            if (FolderController.getInstance().isSourceFolder(ProjectController.getInstance().getCurrentProject(),
                    folder)) {
                return;
            }
            MHandledMenuItem customKeywordPluginMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(CUSTOM_KEYWORD_PLUGIN_COMMAND_ID, null), "Custom Keyword Plugin",
                    ConstantsHelper.getApplicationURI());
            if (customKeywordPluginMenuItem != null) {
                menuItems.add(customKeywordPluginMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
    
    protected FolderTreeEntity getSelectedTreeEntity(Object[] selectedObjects ) {
        if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof ITreeEntity)) {
            return null;
        }

        if (selectedObjects[0] instanceof FolderTreeEntity) {
            FolderTreeEntity parentFolder = (FolderTreeEntity) selectedObjects[0];
            return isKeywordFolder(parentFolder) ? parentFolder : null; 
        } else {
            ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
            try {
                ITreeEntity parent = treeEntity.getParent();
                if (!(parent instanceof FolderTreeEntity)) {
                    return null;
                }
                FolderTreeEntity parentFolder = (FolderTreeEntity) parent;
                return isKeywordFolder(parentFolder) ? parentFolder : null; 
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return null;
            }
        }
    }
    
    private boolean isKeywordFolder(FolderTreeEntity folderTree) {
        try {
            return folderTree.getObject().getFolderType() == FolderType.KEYWORD;
        } catch (Exception e) {
           return false;
        }
    }
}
