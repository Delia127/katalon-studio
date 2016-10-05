package com.kms.katalon.composer.mobile.objectspy.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

@SuppressWarnings("restriction")
public class MobileObjectSpyMenuContribution {
    private static final String MENU_ID_ADD_TO_MOBILE_OBJECT_SPY = "com.kms.katalon.composer.mobile.objectspy.command.mobile.addToMobileObjectSpy";
    
    @Inject
    private ESelectionService selectionService;

    @Inject
    private ECommandService commandService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        Object objects = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (objects == null || !objects.getClass().isArray()) {
            return;
        }
        for (Object selected : (Object[]) objects) {
            if (isWebElementFolderTreeEntity(selected) || (selected instanceof WebElementTreeEntity)) {
                MHandledMenuItem newTestObjectPopupMenuItem = createAddToMobileObjectSpyMenuItem();

                if (newTestObjectPopupMenuItem != null) {
                    menuItems.add(newTestObjectPopupMenuItem);
                }
                return;
            }
        }
    }

    private boolean isWebElementFolderTreeEntity(Object selected) {
        try {
            if (selected instanceof FolderTreeEntity) {
                FolderTreeEntity folder = (FolderTreeEntity) selected;
                return ((FolderEntity) folder.getObject()).getFolderType() == FolderType.WEBELEMENT;
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
        return false;
    }

    private MHandledMenuItem createAddToMobileObjectSpyMenuItem() {
        try {
            return MenuFactory.createPopupMenuItem(
                    commandService.createCommand(MENU_ID_ADD_TO_MOBILE_OBJECT_SPY, null),
                    StringConstants.MENU_ADD_TO_MOBILE_OBJECT_SPY, ConstantsHelper.getApplicationURI());
        } catch (Exception e) {
            return null;
        }
    }
}
