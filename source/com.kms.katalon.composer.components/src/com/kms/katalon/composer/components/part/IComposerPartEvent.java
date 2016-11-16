package com.kms.katalon.composer.components.part;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import com.kms.katalon.constants.EventConstants;

public interface IComposerPartEvent extends IComposerPart {

    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event);

    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event);

    @PreDestroy
    public void onClose();

}
