package com.kms.katalon.composer.windows.dialog;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.windows.constant.ComposerWindowsMessage;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class NewWindowsElementDialog extends CommonNewEntityDialog<WindowsElementEntity> {

    public NewWindowsElementDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(ComposerWindowsMessage.TITLE_NEW_WINDOWS_OBJECT_NAME);
        setDialogMsg(ComposerWindowsMessage.NewWindowsElementDialog_MSG_CREATE_NEW_WINDOWS_OBJECT);
    }

    @Override
    protected void createEntity() {
        try {
            entity = newWindowsTestObject(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public WindowsElementEntity newWindowsTestObject(FolderEntity parentFolder, String name) {
        WindowsElementEntity windowsElementEntity = new WindowsElementEntity();
        windowsElementEntity.setName(name);
        windowsElementEntity.setParentFolder(parentFolder);
        windowsElementEntity.setProject(parentFolder.getProject());
        return windowsElementEntity;
    }
}
