package com.kms.katalon.platform.internal.service.impl;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.PlatformException;
import com.katalon.platform.api.dialogs.CommonDialogs;
import com.katalon.platform.api.model.Folder;
import com.katalon.platform.api.util.ExceptionUtil;
import com.kms.katalon.composer.testcase.dialogs.TestCaseFolderSelectionDialog;
import com.kms.katalon.entity.folder.FolderEntity;

public class CommonDialogsImpl implements CommonDialogs {

    @Override
    public Folder showTestCaseFolderSelectionDialog(Shell parentShell, String dialogTitle) throws PlatformException {
        try {
            TestCaseFolderSelectionDialog dialog = new TestCaseFolderSelectionDialog(parentShell, dialogTitle);
            if (dialog.open() == IDialogConstants.OK_ID) {
                FolderEntity selectedFolderEntity = dialog.getSelectedFolder().getObject();
                return new Folder() {
    
                    @Override
                    public String getId() {
                        return selectedFolderEntity.getId();
                    }
    
                    @Override
                    public String getName() {
                        return selectedFolderEntity.getName();
                    }
    
                    @Override
                    public String getFolderLocation() {
                        return selectedFolderEntity.getLocation();
                    }
    
                    @Override
                    public String getFileLocation() {
                        return selectedFolderEntity.getLocation();
                    }
                    
                };
            } else {
                return null;
            }
        } catch (Exception e) {
            ExceptionUtil.wrapAndThrow(e);
        }
        return null;
    }
}
