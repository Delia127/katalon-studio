package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class EditorSavedHandler implements EventHandler {
    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.ECLIPSE_EDITOR_SAVED, this);
    }

    @Override
    public void handleEvent(Event event) {
        String topic = event.getTopic();
        Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        switch (topic) {
            case EventConstants.ECLIPSE_EDITOR_SAVED: {
                if (!(object instanceof MPart)) {
                    return;
                }
                final IFile file = ((FileEditorInput) (GroovyEditorUtil.getEditor((MPart) object).getEditorInput()))
                        .getFile();
                try {
                    KeywordController.getInstance().parseCustomKeywordFile(file,
                            ProjectController.getInstance().getCurrentProject());
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                break;
            }
        }
    }
}
