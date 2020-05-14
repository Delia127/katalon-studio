package com.kms.katalon.composer.explorer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class OpenUserFileHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                try {
                    Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (object instanceof UserFileEntity) {
                        UserFileEntity fileEntity = (UserFileEntity) object;
                        openEditor(fileEntity);
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }
    
    public static IEditorPart openEditor(UserFileEntity object) {
        try {
            IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(object.toFile().getName());
            if (desc == null || desc.getId().equals("org.codehaus.groovy.eclipse.editor.GroovyEditor")) {
                desc = PlatformUI.getWorkbench().getEditorRegistry().findEditor("org.eclipse.ui.DefaultTextEditor");
            }
            
            IWorkbenchPage activePage = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage();
            IFileStore fileStore = EFS.getStore(object.toFile().toURI());
            IEditorPart editor = (IEditorPart) activePage.findEditor(new FileStoreEditorInput(fileStore));
            if (editor != null) {
                activePage.activate(editor);
            } else {
                editor = activePage.openEditor(new FileStoreEditorInput(fileStore), desc.getId()); 
            }
            
            return editor;
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
