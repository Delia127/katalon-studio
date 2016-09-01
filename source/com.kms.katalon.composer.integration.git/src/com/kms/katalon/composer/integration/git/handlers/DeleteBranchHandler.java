 
package com.kms.katalon.composer.integration.git.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.git.components.utils.CustomBranchOperationUI;

public class DeleteBranchHandler extends BranchHandler {
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

    @Execute
    public void execute() {
        CustomBranchOperationUI operation = CustomBranchOperationUI.delete(getRepository());
        operation.start();
    }
		
}