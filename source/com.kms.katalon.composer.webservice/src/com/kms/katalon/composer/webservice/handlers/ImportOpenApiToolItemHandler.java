package com.kms.katalon.composer.webservice.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Menu;

import com.kms.katalon.controller.ProjectController;

public class ImportOpenApiToolItemHandler {

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute() {
        MUIElement webServiceToolbar = modelService.find("com.kms.katalon.composer.toolbar.webservice", application);
        MHandledToolItem toolItem = (MHandledToolItem) modelService
                .find("com.kms.katalon.composer.webservice.handledtoolitem.importopenapi", webServiceToolbar);
        MMenu menu = toolItem.getMenu();
        Menu menuWidget = (Menu) menu.getWidget();
        if (!menuWidget.isVisible()) {
            menuWidget.setVisible(true);
        }
    }
}
