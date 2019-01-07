package com.kms.katalon.platform.internal.ui;

import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.ui.DialogService;

public class DialogHelperImpl implements DialogService {

    @Override
    public FolderEntity showTestCaseFolderSelectionDialog(Shell parentShell, String dialogTitle)
            throws PlatformException {
        
        return null;
    }

    @Override
    public void openApplicationPreferences() {
        
    }

    @Override
    public void openPluginPreferencePage(String preferenceId) {
        // TODO Auto-generated method stub
        
    }

}
