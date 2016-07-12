package com.kms.katalon.composer.explorer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.IdConstants;

public class ReskinTextAreaHandler extends EventServiceAdapter {

    private static final String ACTIVE_TAG = "active";

    private static final String REMOVE_ITEM_TOPIC = UIEvents.ApplicationElement.TOPIC_ALL.replace("*", "")
            + UIEvents.ApplicationElement.TAGS + "/" + UIEvents.EventTypes.REMOVE;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @Inject
    public void registerEventHandler() {
        eventBroker.subscribe(REMOVE_ITEM_TOPIC, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (REMOVE_ITEM_TOPIC.equals(event.getTopic()) && isCTabFolderActiveTagRemoved(event)
                && isNotEditorTabItemRemoved(event)) {

            MPlaceholder textArea = (MPlaceholder) modelService.find(IdConstants.SHARE_AREA_ID, application);

            if (textArea == null || !textArea.isVisible()) {
                return;
            }
            reskinTextArea(textArea);
        }
    }

    private boolean isCTabFolderActiveTagRemoved(Event event) {
        return ACTIVE_TAG.equals(event.getProperty(UIEvents.EventTags.OLD_VALUE))
                && event.getProperty(UIEvents.EventTags.WIDGET) instanceof CTabFolder;
    }

    /**
     * Prevents re-skin CTabFolder editor when system is removing its children.
     */
    private boolean isNotEditorTabItemRemoved(Event event) {
        Object changedElement = event.getProperty(UIEvents.EventTags.ELEMENT);
        return (!(changedElement instanceof MPartStack))
                || !((MPartStack) changedElement).getElementId().startsWith(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID);
    }

    private void reskinTextArea(MPlaceholder textArea) {
        Composite editorTabFolder = (Composite) textArea.getRef().getWidget();
        if (editorTabFolder == null || editorTabFolder.isDisposed()) {
            return;
        }
        CTabFolder tabFolder = (CTabFolder) editorTabFolder.getChildren()[0];
        if (tabFolder.getItemCount() > 0) {
            tabFolder.reskin(SWT.ALL);
        }
    }
}
