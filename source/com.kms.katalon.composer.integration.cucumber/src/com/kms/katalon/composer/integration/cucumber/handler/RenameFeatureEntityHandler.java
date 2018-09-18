package com.kms.katalon.composer.integration.cucumber.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.cucumber.dialog.RenameFeatureEntityDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class RenameFeatureEntityHandler {
    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (object instanceof SystemFileTreeEntity) {
                    execute((SystemFileTreeEntity) object);
                }
            }
        });
    }

    private void execute(SystemFileTreeEntity testListenerTreeEntity) {
        try {
            SystemFileController testListenerController = SystemFileController.getInstance();

            FolderTreeEntity parentTreeFolder = (FolderTreeEntity) testListenerTreeEntity
                    .getParent();
            SystemFileEntity renamedFile = testListenerTreeEntity.getObject();
            FolderEntity parentFolder = parentTreeFolder.getObject();
            RenameFeatureEntityDialog dialog = new RenameFeatureEntityDialog(parentShell,
                    testListenerTreeEntity.getObject(),
                    testListenerController.getSiblingFiles(renamedFile, parentFolder));
            if (dialog.open() != RenameFeatureEntityDialog.OK) {
                return;
            }
            String newName = dialog.getNewName();
            if (renamedFile.getName().equals(newName)) {
                return;
            }

            IFile iFile = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
                    .getFile(Path.fromOSString(renamedFile.getRelativePath()));

            SystemFileEntity newTestListener = testListenerController.renameSystemFile(newName,
                    renamedFile);
            testListenerTreeEntity.setObject(newTestListener);

            iFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, testListenerTreeEntity);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, testListenerTreeEntity);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e,
                    "Unable to rename Feature file", e.getMessage());
            LoggerSingleton.logError(e);
        }
    }
}
