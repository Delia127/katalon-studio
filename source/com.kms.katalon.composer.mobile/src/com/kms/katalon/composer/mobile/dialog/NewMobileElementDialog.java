package com.kms.katalon.composer.mobile.dialog;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.MobileElementEntity;

public class NewMobileElementDialog extends CommonNewEntityDialog<MobileElementEntity> {

    public NewMobileElementDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle("New Mobile Object");
        setDialogMsg("Create new mobile object");
    }

    @Override
    protected void createEntity() {
        try {
            entity = newMobileElement(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public MobileElementEntity newMobileElement(FolderEntity parentFolder, String name) {
        MobileElementEntity windowsElementEntity = new MobileElementEntity();
        windowsElementEntity.setName(name);
        windowsElementEntity.setParentFolder(parentFolder);
        windowsElementEntity.setProject(parentFolder.getProject());
        return windowsElementEntity;
    }
}