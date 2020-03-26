package com.kms.katalon.composer.explorer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class OpenUserFileHandler {
    
    private static final String GRADLE_FILE_EXTENSION = ".gradle";
    
    private static final String FEATURE_FILE_EXTENSION = ".feature";

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
    
    public static IEditorPart openEditor(UserFileEntity userFile) {
        try {
            IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(userFile.toFile().getName());
            if (desc == null || GRADLE_FILE_EXTENSION.equalsIgnoreCase(userFile.getFileExtension())) {
                desc = PlatformUI.getWorkbench().getEditorRegistry().findEditor("org.eclipse.ui.DefaultTextEditor");
            }
            
            IWorkbenchPage activePage = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage();

            IEditorPart editor = getEditor(userFile, desc, activePage);

            return editor;
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    private static IEditorPart getEditor(UserFileEntity userFile, IEditorDescriptor desc, IWorkbenchPage activePage)
            throws CoreException {
        IEditorInput editorInput = getEditorInput(userFile);
        IEditorPart editor = activePage.findEditor(editorInput);

        if (editor != null) {
            activePage.activate(editor);
        } else {
            editor = activePage.openEditor(editorInput, desc.getId());
        }

        return editor;
    }

    private static IEditorInput getEditorInput(UserFileEntity userFile) throws CoreException {
        String extension = userFile.getFileExtension();
        if (FEATURE_FILE_EXTENSION.equalsIgnoreCase(extension)) {
            return getFeatureFileEditorInput(userFile);
        }
        return getDefaultFileEditorInput(userFile);
    }

    private static IEditorInput getDefaultFileEditorInput(UserFileEntity userFile) throws CoreException {
        IFileStore fileStore = EFS.getStore(userFile.toFile().toURI());
        return new FileStoreEditorInput(fileStore);
    }

    private static IEditorInput getFeatureFileEditorInput(UserFileEntity userFile) throws PartInitException {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        IFile iFile = GroovyUtil.getGroovyProject(currentProject).getFile(userFile.getRelativePath());
        return new FileEditorInput(iFile);
    }
}
