package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.settings.WebServicePreferenceStore;
import com.kms.katalon.composer.webservice.view.WSRequestPartUI;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenWebServiceRequestObjectHandler {

    @Inject
    MApplication application;

    @Inject
    EModelService modelService;

    @Inject
    IEclipseContext context;

    @Inject
    EPartService partService;

    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && WebServiceRequestEntity.class.isInstance(object)) {
                    openRequestObject((WebServiceRequestEntity) object);
                }
            }
        });

        eventBroker.subscribe(EventConstants.WORKSPACE_DRAFT_PART_CLOSED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                java.util.Optional<DraftWebServiceRequestEntity> optional = getDraftWebService(event);
                if (!optional.isPresent()) {
                    return;
                }

                WebServicePreferenceStore store = new WebServicePreferenceStore();
                try {
                    store.removeDraftRequest(optional.get(), ProjectController.getInstance().getCurrentProject());
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }
        });

        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_ITEM_BY_PART_ID, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                java.util.Optional<DraftWebServiceRequestEntity> optional = getDraftWebService(event);
                if (optional.isPresent()) {
                    try {
                        openDraftRequest(optional.get());
                    } catch (IOException | CoreException e) {
                        LoggerSingleton.logError(e);
                    }
                }
            }
        });

        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_DRAFT_WEBSERVICE, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (object instanceof DraftWebServiceRequestEntity) {
                    try {
                        openDraftRequest((DraftWebServiceRequestEntity) object);
                    } catch (IOException | CoreException e) {
                        MultiStatusErrorDialog.showErrorDialog("Unalbe to open request", e.getMessage(),
                                ExceptionsUtil.getStackTraceForThrowable(e));
                    }
                }
            }
        });
    }

    private java.util.Optional<DraftWebServiceRequestEntity> getDraftWebService(Event event) {
        Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        if (!(object instanceof String)) {
            return java.util.Optional.empty();
        }
        String partId = (String) object;
        if (!partId.startsWith(IdConstants.DRAFT_REQUEST_CONTENT_PART_ID_PREFIX)) {
            return java.util.Optional.empty();
        }
        String draftRequestUid = EntityPartUtil.getEntityIdFromPartId(partId,
                IdConstants.DRAFT_REQUEST_CONTENT_PART_ID_PREFIX);
        WebServicePreferenceStore store = new WebServicePreferenceStore();

        java.util.Optional<DraftWebServiceRequestEntity> optional = store
                .getDraftRequests(ProjectController.getInstance().getCurrentProject())
                .stream()
                .filter(request -> request.getDraftUid().equals(draftRequestUid))
                .findFirst();
        return optional;
    }

    @Inject
    @Optional
    private void getNotifications(
            @UIEventTopic(EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN) WebServiceRequestEntity entity) {
        openRequestObject(entity);
    }

    public void openRequestObject(WebServiceRequestEntity requestObject) {
        try {
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            String partId = EntityPartUtil.getTestObjectPartId(requestObject.getId());
            MPart mPart = (MPart) modelService.find(partId, application);
            if (stack != null) {
                if (mPart == null) {
                    WSRequestPartUI.create(requestObject, stack);
                    Trackings.trackOpenObject("webServiceRequest");
                } else {
                    stack.setSelectedElement(mPart);
                }
            }
        } catch (IOException | CoreException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.MSG_CANNOT_OPEN_REQUEST);
        }
    }

    public void openRequestHistoryObject(RequestHistoryEntity historyRequest) {
        DraftWebServiceRequestEntity draftWebServiceEntity = WebServicePreferenceStore.getGson().fromJson(
                WebServicePreferenceStore.getGson().toJson(historyRequest.getRequest()),
                DraftWebServiceRequestEntity.class);
        draftWebServiceEntity.setDraftUid(historyRequest.getUid());
        try {
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            String partId = EntityPartUtil.getDraftRequestPartId(draftWebServiceEntity.getDraftUid());
            if (stack != null) {
                MPart mPart = (MPart) modelService.find(partId, application);
                if (mPart == null) {
                    WSRequestPartUI.create(draftWebServiceEntity, stack);
                    Trackings.trackOpenDraftRequest(draftWebServiceEntity.getServiceType(), "history");
                } else {
                    stack.setSelectedElement(mPart);
                }
            }

            WebServicePreferenceStore store = new WebServicePreferenceStore();
            store.saveDraftRequest(draftWebServiceEntity, ProjectController.getInstance().getCurrentProject());
        } catch (IOException | CoreException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog("Unable to open request", e.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }

    public void openDraftRequest(DraftWebServiceRequestEntity draftRequest) throws IOException, CoreException {
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        String partId = EntityPartUtil.getDraftRequestPartId(draftRequest.getDraftUid());
        if (stack != null) {
            MPart mPart = (MPart) modelService.find(partId, application);
            if (mPart == null) {
                WSRequestPartUI.create(draftRequest, stack);
            } else {
                stack.setSelectedElement(mPart);
            }
            setSelectedExplorerPart();
        }
    }

    private void setSelectedExplorerPart() {
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_PARTSTACK_EXPLORER_ID, application);
        if (stack != null) {
            MPart requestHistoryPart = partService.findPart(IdConstants.COMPOSER_REQUEST_HISTORY_PART_ID);
            if (requestHistoryPart != null
                    && !stack.getSelectedElement().getElementId().equals(requestHistoryPart.getElementId())) {
                stack.setSelectedElement(requestHistoryPart);
            }
        }
    }
}
