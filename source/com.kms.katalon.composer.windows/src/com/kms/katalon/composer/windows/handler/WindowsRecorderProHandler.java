package com.kms.katalon.composer.windows.handler;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialogV2;
import com.kms.katalon.controller.ProjectController;

public class WindowsRecorderProHandler {

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }
    
    
    @Execute
    public void execute() {
        Shell shell = getShell(Display.getCurrent().getActiveShell());
        WindowsRecorderDialogV2 dialog = new WindowsRecorderDialogV2(shell);
        dialog.open();
    }
    
    private Shell getShell(Shell activeShell) {
        String os = Platform.getOS();
        if (Platform.OS_WIN32.equals(os) || Platform.OS_LINUX.equals(os)) {
            return null;
        }
        Shell shell = new Shell();
        Rectangle activeShellSize = activeShell.getBounds();
        shell.setLocation((activeShellSize.width - shell.getBounds().width) / 2,
                (activeShellSize.height - shell.getBounds().height) / 2);
        return shell;
    }
}
