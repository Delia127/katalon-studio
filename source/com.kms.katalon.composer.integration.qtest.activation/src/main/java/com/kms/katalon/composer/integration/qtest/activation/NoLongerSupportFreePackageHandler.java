package com.kms.katalon.composer.integration.qtest.activation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class NoLongerSupportFreePackageHandler {

    @Inject
    IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                IntegratedEntity integratedEntity = currentProject.getIntegratedEntity("qTest");
                if (integratedEntity == null || integratedEntity.getProperties().isEmpty()) {
                    return;
                }

                NoLongerSupportFreePackageDialog dialog = new NoLongerSupportFreePackageDialog(
                        Display.getCurrent().getActiveShell());
                dialog.open();

                // KAT-3795 Allow user to continue using KS free package
                // eventBroker.post(EventConstants.PROJECT_CLOSE, null);
            }
        });
    }
}
