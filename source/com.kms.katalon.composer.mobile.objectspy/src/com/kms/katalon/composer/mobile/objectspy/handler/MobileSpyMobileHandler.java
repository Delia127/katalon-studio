package com.kms.katalon.composer.mobile.objectspy.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.controller.ProjectController;

public class MobileSpyMobileHandler {

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(MHandledToolItem element) {
        ToolItem toolItem = (ToolItem) element.getWidget();

        Event e = new Event();
        e.type = SWT.Selection;
        e.widget = toolItem;
        e.detail = SWT.DROP_DOWN;
        e.x = toolItem.getBounds().x;
        e.y = toolItem.getBounds().height;

        toolItem.notifyListeners(SWT.Selection, e);
    }
}
