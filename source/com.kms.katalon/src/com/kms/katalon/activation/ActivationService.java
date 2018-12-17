package com.kms.katalon.activation;

import org.eclipse.swt.widgets.Shell;

public interface ActivationService {
    boolean checkActivation(Shell activeShell);

    
    void openAboutDialog(Shell activeShell);
}
