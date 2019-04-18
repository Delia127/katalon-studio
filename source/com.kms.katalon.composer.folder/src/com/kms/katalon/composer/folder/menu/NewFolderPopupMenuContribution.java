package com.kms.katalon.composer.folder.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;

import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

@SuppressWarnings("restriction")
public class NewFolderPopupMenuContribution {
    private static final String NEW_FOLDER_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_NEW_FOLDER;

    private static final String NEW_FOLDER_COMMAND_ID = "com.kms.katalon.composer.folder.command.add";

    @Inject
    private ECommandService commandService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    public void init() {
        selectionService.addSelectionListener(new ISelectionListener() {
            @Override
            public void selectionChanged(MPart part, Object selection) {
                if (part.getElementId().equals(IdConstants.EXPLORER_PART_ID)) {
                    selectionService.setSelection(null);
                    selectionService.setSelection(selection);
                }
            }
        });
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (canExecute(selectedObjects)) {
                MHandledMenuItem newFolderPopupMenuItem = MenuFactory.createPopupMenuItem(
                        commandService.createCommand(NEW_FOLDER_COMMAND_ID, null), NEW_FOLDER_POPUP_MENUITEM_LABEL,
                        ConstantsHelper.getApplicationURI());
                if (newFolderPopupMenuItem != null) {
                    menuItems.add(newFolderPopupMenuItem);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    private boolean canExecute(Object[] selectedObjects) throws Exception {
        if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity) {
            ITreeEntity parentTreeEntity = (ITreeEntity) selectedObjects[0];
            if (parentTreeEntity instanceof FolderTreeEntity) {
                FolderEntity parentFolder = (FolderEntity) parentTreeEntity.getObject();
                if (parentFolder.getFolderType() == FolderType.TESTCASE
                        || parentFolder.getFolderType() == FolderType.TESTSUITE
                        || parentFolder.getFolderType() == FolderType.DATAFILE
                        || parentFolder.getFolderType() == FolderType.WEBELEMENT
                        || parentFolder.getFolderType() == FolderType.CHECKPOINT
                        || parentFolder.getFolderType() == FolderType.USER) {
                    return true;
                }
            } else if (parentTreeEntity instanceof TestCaseTreeEntity || parentTreeEntity instanceof TestDataTreeEntity
                    || parentTreeEntity instanceof TestSuiteTreeEntity
                    || parentTreeEntity instanceof WebElementTreeEntity
                    || parentTreeEntity instanceof TestSuiteCollectionTreeEntity
                    || parentTreeEntity instanceof CheckpointTreeEntity
                    || parentTreeEntity instanceof UserFileTreeEntity) {
                return true;
            }
        }
        return false;
    }
}
