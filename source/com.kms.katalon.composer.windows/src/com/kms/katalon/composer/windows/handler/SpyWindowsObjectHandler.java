package com.kms.katalon.composer.windows.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.windows.dialog.SpyWindowsObjectDialog;
import com.kms.katalon.composer.windows.dialog.WindowsAppComposite;

public class SpyWindowsObjectHandler {

    @Execute
    public void execute(Shell activeShell) {
        try {
            SpyWindowsObjectDialog dialog = new SpyWindowsObjectDialog(activeShell, new WindowsAppComposite());
            dialog.open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
