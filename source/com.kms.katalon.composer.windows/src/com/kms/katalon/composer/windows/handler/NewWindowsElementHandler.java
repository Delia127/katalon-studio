package com.kms.katalon.composer.windows.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.windows.constant.ComposerWindowsMessage;
import com.kms.katalon.composer.windows.dialog.NewWindowsElementDialog;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewWindowsElementHandler {

    @Execute
    public void createNewWindowsElement(Shell activeShell) {
        try {
            ITreeEntity selectedTreeEntity = (ITreeEntity) ExplorerPart.getInstance().getSelectedTreeEntities().get(0);

            FolderTreeEntity folderTreeEntity = null;
            if (selectedTreeEntity instanceof FolderTreeEntity) {
                folderTreeEntity = (FolderTreeEntity) selectedTreeEntity;
            } else {
                folderTreeEntity = (FolderTreeEntity) selectedTreeEntity.getParent();
            }

            FolderEntity folder = (FolderEntity) selectedTreeEntity.getObject();

            String suggestedName = EntityNameController.getInstance()
                    .getAvailableName(ComposerWindowsMessage.TITLE_NEW_WINDOWS_OBJECT_NAME, folder, false);
            NewWindowsElementDialog dialog = new NewWindowsElementDialog(activeShell, folder, suggestedName);

            if (dialog.open() != Dialog.OK) {
                return;
            }
            WindowsElementEntity testRunEntity = dialog.getEntity();
            if (testRunEntity == null) {
                return;
            }

            WindowsElementEntity newWindowsElementEntity = WindowsElementController.getInstance()
                    .newWindowsElementEntity(folder, testRunEntity.getName());

            Trackings.trackCreatingObject("windowsObject");

            ExplorerPart.getInstance().refreshTreeEntity(folderTreeEntity);
            ExplorerPart.getInstance().setSelectedItems(
                    new Object[] { TreeEntityUtil.getWindowsElementTreeEntity(newWindowsElementEntity, folder) });

            OpenWindowsElementHandler.getInstance().openWindowsElement(newWindowsElementEntity);

        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to create Windows object", e.getMessage());
        }
    }
}
