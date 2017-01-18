package com.kms.katalon.composer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.processors.ToolbarProcessor;

public class NewHandler extends AbstractHandler {

    @Inject
    private IEclipseContext context;

    @Optional
    private MHandledToolItem newToolItem;

    @Override
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Override
    public void execute() {
        if (context == null) {
            return;
        }

        if (newToolItem == null) {
            newToolItem = (MHandledToolItem) context.get(ToolbarProcessor.KATALON_TOOLITEM_NEW_ID);
            if (newToolItem == null) {
                return;
            }
        }

        ToolItem toolItem = (ToolItem) newToolItem.getWidget();
        if (toolItem == null) {
            return;
        }

        MMenu mMenu = newToolItem.getMenu();
        if (mMenu == null) {
            return;
        }

        Menu menu = (Menu) mMenu.getWidget();
        if (menu == null) {
            return;
        }

        // Show the drop-down menu
        Rectangle newToolItemBounds = toolItem.getBounds();
        Point location = toolItem.getParent().toDisplay(new Point(newToolItemBounds.x, newToolItemBounds.y));
        menu.setLocation(location.x, location.y + newToolItemBounds.height);
        menu.setVisible(true);
    }

}
