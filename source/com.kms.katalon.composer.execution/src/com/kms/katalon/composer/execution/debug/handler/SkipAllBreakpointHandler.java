package com.kms.katalon.composer.execution.debug.handler;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;

import com.kms.katalon.controller.ProjectController;

public class SkipAllBreakpointHandler {
    
    @CanExecute
    public boolean canExecute(MHandledMenuItem item) {
        item.setSelected(!DebugPlugin.getDefault().getBreakpointManager().isEnabled());
        
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(MHandledMenuItem item) {
        //change state
        item.setSelected(!item.isSelected());
        DebugPlugin.getDefault().getBreakpointManager().setEnabled(!item.isSelected());
    }
}
