package com.kms.katalon.composer.mobile.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.mobile.dialog.NewMobileElementDialog;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.MobileElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewMobileElementHandler {
    @Execute
    public void createNewWindowsElement(Shell activeShell) {
        try {
            FolderTreeEntity folderTreeEntity = (FolderTreeEntity) ExplorerPart.getInstance()
                    .getSelectedTreeEntities()
                    .get(0);

            FolderEntity folder = folderTreeEntity.getObject();

            String suggestedName = EntityNameController.getInstance()
                    .getAvailableName("New Mobile Object", folder, false);
            NewMobileElementDialog dialog = new NewMobileElementDialog(activeShell, folder, suggestedName);

            if (dialog.open() != Dialog.OK) {
                return;
            }
            MobileElementEntity newMobileElement = dialog.getEntity();
            if (newMobileElement == null) {
                return;
            }

            MobileElementEntity newMobileElementEntity = ObjectRepositoryController.getInstance()
                    .newMobileElement(newMobileElement);

            Trackings.trackCreatingObject("mobileObject");

            ExplorerPart.getInstance().refreshTreeEntity(folderTreeEntity);
            ExplorerPart.getInstance().setSelectedItems(
                    new Object[] { TreeEntityUtil.getWebElementTreeEntity(newMobileElementEntity, folder.getProject()) });

            OpenMobileTestObjectHandler.getInstance().execute(newMobileElementEntity);

        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to create Mobile object", e.getMessage());
        }
    }
}
