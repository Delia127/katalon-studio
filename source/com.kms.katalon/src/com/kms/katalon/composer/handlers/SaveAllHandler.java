package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.PlatformUI;

public class SaveAllHandler {

    @CanExecute
    private boolean canExecute(@Optional EPartService partService) {
        if (partService != null) {
            return !partService.getDirtyParts().isEmpty();
        }
        return false;
    }

    @Execute
    void execute(EPartService partService) {
        partService.saveAll(false);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(false);
    }
}
