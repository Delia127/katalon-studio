package com.kms.katalon.composer.components.impl.handler;

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
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.file.FileEntity;

public abstract class OpenFileEntityHandler<T extends FileEntity> implements PartActionHandler<T> {
    @Inject
    private MApplication application;

    @Inject
    private EModelService modelService;

    @Inject
    private EPartService partService;

    @Inject
    protected IEventBroker eventBroker;

    @PostConstruct
    protected void initialize() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventServiceAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (isElementInstanceOf(object)) {
                    execute((T) object);
                }
            }
        });
    }

    protected void execute(T fileEntity) {
        String partId = getPartId(fileEntity);
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);

        MPart mPart = (MPart) modelService.find(partId, application);
        if (mPart == null) {
            mPart = modelService.createModelElement(MPart.class);
            mPart.setElementId(partId);
            mPart.setLabel(fileEntity.getName());

            mPart.setIconURI(getIconURI());

            mPart.setContributionURI(getContributionURI());
            mPart.setCloseable(true);
            stack.getChildren().add(mPart);
        }

        if (mPart.getObject() == null) {
            mPart.setObject(fileEntity);
        }

        partService.showPart(mPart, PartState.ACTIVATE);
        stack.setSelectedElement(mPart);
    }

    protected abstract Class<? extends T> getEntityType();

    private boolean isElementInstanceOf(Object element) {
        Class<?> clazz = getEntityType();
        return clazz != null && clazz.isInstance(element);
    }
}
