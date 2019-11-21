package com.kms.katalon.composer.mobile.menu;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.mobile.handler.NewMobileElementHandler;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class NewMobileElementPopupMenuContribution {
    private static final String CONTRIBUTION_CLASS_URI = "bundleclass://com.kms.katalon.composer.mobile/" + NewMobileElementHandler.class.getName();

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            ExplorerPart explorerPart = ExplorerPart.getInstance();
            List<Object> selectedObjects = explorerPart.getSelectedTreeEntities();
            if (selectedObjects == null || selectedObjects.size() != 1
                    || !(selectedObjects.get(0) instanceof FolderTreeEntity)) {
                return;
            }
            FolderEntity folderEntity = ((FolderTreeEntity) selectedObjects.get(0)).getObject();
            if (folderEntity.getFolderType() == FolderType.WEBELEMENT) {
                MDirectMenuItem newWindowsElementToolItem = MenuFactory.createDirectMenuItem("Mobile Object",
                        ConstantsHelper.getApplicationURI());
                newWindowsElementToolItem.setContributionURI(CONTRIBUTION_CLASS_URI);
                menuItems.add(newWindowsElementToolItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
