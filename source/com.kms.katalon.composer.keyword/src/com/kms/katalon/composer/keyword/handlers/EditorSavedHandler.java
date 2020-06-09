package com.kms.katalon.composer.keyword.handlers;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.util.groovy.editor;
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
                MPart part = (MPart) object;
                if (!editor.isGroovyEditorPart(part)) {
                    return;
                }

                final IFile file = ((FileEditorInput) (editor.getEditor(part).getEditorInput())).getFile();
                Executors.newSingleThreadExecutor().submit(() -> {
                    if (!isScriptRightFormat(file)) {
                        return;
                    }

                    try {
                        KeywordController.getInstance().parseCustomKeywordFile(file,
                                ProjectController.getInstance().getCurrentProject());
                    } catch (Exception ex) {
                        LoggerSingleton.logError(ex);
                    }

                    UISynchronizeService.syncExec(() -> {
                        try {
                            refreshKeywordTreeEntity(file);
                        } catch (Exception ex) {
                            LoggerSingleton.logError(ex);
                        }
                    });
                });
                break;
            }
        }
    }

    private boolean isScriptRightFormat(final IFile file) {
        if (!ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
            return true;
        }

        try {
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
        } catch (OperationCanceledException | InterruptedException e) {
            return false;
        }

        return !scriptContainsErrors(file);
    }

    private boolean scriptContainsErrors(IFile file) {
        try {
            IMarker[] problemMarkers = file.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
                    IResource.DEPTH_ZERO);

            return problemMarkers != null && problemMarkers.length > 0;
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            return true;
        }
    }

    private void refreshKeywordTreeEntity(final IFile file) throws Exception {
        file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
        KeywordTreeEntity keywordTreeEntity = TreeEntityUtil.getKeywordTreeEntity(
                file.getProjectRelativePath().toString(), ProjectController.getInstance().getCurrentProject());
        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordTreeEntity);
    }
}
