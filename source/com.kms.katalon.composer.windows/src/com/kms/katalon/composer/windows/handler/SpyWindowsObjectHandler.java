package com.kms.katalon.composer.windows.handler;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.windows.dialog.SpyWindowsObjectDialog;
import com.kms.katalon.composer.windows.dialog.WindowsAppComposite;

public class SpyWindowsObjectHandler {

    @Execute
    public void execute(Shell activeShell) {
        Shell shell = null;
        try {
            SpyWindowsObjectDialog dialog = new SpyWindowsObjectDialog(getShell(activeShell),
                    new WindowsAppComposite());
            dialog.open();
        } finally {
            if (shell != null) {
                shell.dispose();
            }
        }
    }

    private Shell getShell(Shell activeShell) {
        String os = Platform.getOS();
        if (Platform.OS_WIN32.equals(os) || Platform.OS_LINUX.equals(os)) {
            return null;
        }
        Shell shell = new Shell();
        return shell;
    }
}
