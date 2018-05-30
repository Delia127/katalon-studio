package com.kms.katalon.composer.handlers;

import java.util.concurrent.Executors;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.application.RunningMode;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.application.usagetracking.UsageInfoCollector;

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
        try {
            partService.saveAll(false);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(false);
        } finally {
            Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector
                    .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.SAVE_ALL, RunningMode.GUI)));
        }
    }
}
