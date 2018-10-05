package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewRestRequestHandler {

    @Inject
    IEclipseContext context;
    
    @Inject
    EModelService modelService;
    
    @Inject
    MApplication application;

    @CanExecute
    public boolean canExecute() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        return currentProject != null && ProjectType.WEBSERVICE == currentProject.getType();
    }

    @Execute
    public void execute() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        DraftWebServiceRequestEntity draftWebService = ObjectRepositoryController.getInstance()
                .newDraftWebServiceEntity(currentProject);
        draftWebService.setServiceType(DraftWebServiceRequestEntity.RESTFUL);
        try {
            OpenWebServiceRequestObjectHandler handler = context.get(OpenWebServiceRequestObjectHandler.class);
            handler.openDraftRequest(draftWebService);
            Trackings.trackOpenDraftRequest(draftWebService.getServiceType(), "button");
        } catch (IOException | CoreException e) {
            MultiStatusErrorDialog.showErrorDialog("Unable to open request", e.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }
}
