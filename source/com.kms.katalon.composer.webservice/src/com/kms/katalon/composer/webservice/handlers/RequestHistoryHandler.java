package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.settings.WebServicePreferenceStore;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;

public class RequestHistoryHandler {
    private static final String REQUEST_HISTORY_CONTRIBUTION_URI = "bundleclass://"
            + "com.kms.katalon.composer.webservice/com.kms.katalon.composer.webservice.parts.RequestHistoryPart";

    private static final String REQUEST_HISTORY_ICON_URI = "platform:/"
            + "plugin/com.kms.katalon.composer.resources/icons/history_request_16.png";

    @Inject
    IEventBroker eventBroker;

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    private WebServicePreferenceStore store;

    private IRequestHistoryListener listener;

    @PostConstruct
    public void initialEventListeners() {
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                store = new WebServicePreferenceStore();
            }
        });

        eventBroker.subscribe(EventConstants.WS_VERIFICATION_FINISHED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                RequestHistoryEntity requestHistory = (RequestHistoryEntity) getObjects(event)[0];
                try {
                    addRequestHistory(requestHistory, ProjectController.getInstance().getCurrentProject());
                    if (listener != null) {
                        listener.addHistoryRequest(requestHistory);
                    }
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }
        });

        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                MPart mpart = partService.findPart(IdConstants.COMPOSER_REQUEST_HISTORY_PART_ID);
                if (project.getType() == ProjectType.WEBSERVICE) {
                    if (mpart == null) {
                        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_PARTSTACK_EXPLORER_ID,
                                application);
                        mpart = modelService.createModelElement(MPart.class);
                        mpart.setElementId(IdConstants.COMPOSER_REQUEST_HISTORY_PART_ID);
                        mpart.setLabel(ComposerWebserviceMessageConstants.RequestHistoryHandler_PA_TITLE_REQUEST_HISTORY);
                        mpart.setIconURI(REQUEST_HISTORY_ICON_URI);
                        mpart.setContributionURI(REQUEST_HISTORY_CONTRIBUTION_URI);
                        mpart.setCloseable(false);
                        stack.getChildren().add(mpart);

                        partService.showPart(mpart, PartState.ACTIVATE);
                        stack.setSelectedElement(mpart);
                    } else {
                        partService.showPart(mpart, PartState.ACTIVATE);
                    }
                } else {
                    if (mpart != null) {
                        partService.hidePart(mpart);
                    }
                }

                if (listener != null) {
                    listener.resetInput();
                }
            }
        });
    }

    public List<RequestHistoryEntity> getRequestHistoryEntities(ProjectEntity project) {
        return store.getHistoryRequestEntities(project);
    }

    public void removeRequestHistories(List<RequestHistoryEntity> removedRequest, ProjectEntity project)
            throws IOException {
        List<RequestHistoryEntity> requestHistoryEntities = store.getHistoryRequestEntities(project);
        requestHistoryEntities.removeAll(removedRequest);
        store.setRequestHistoryEntities(requestHistoryEntities, project);
        if (listener != null) {
            listener.removeHistoryRequests(removedRequest);
        }
    }

    public void addRequestHistory(RequestHistoryEntity requestHistory, ProjectEntity project) throws IOException {
        store.addRequestHistory(requestHistory, project);
    }

    public IRequestHistoryListener getListener() {
        return listener;
    }

    public void setListener(IRequestHistoryListener listener) {
        this.listener = listener;
    }
}
