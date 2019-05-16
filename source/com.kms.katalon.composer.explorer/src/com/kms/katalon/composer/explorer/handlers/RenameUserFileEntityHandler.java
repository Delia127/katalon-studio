package com.kms.katalon.composer.explorer.handlers;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.dialogs.RenameUserFileEntityDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.UserFileController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class RenameUserFileEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof UserFileTreeEntity) {
                    execute((UserFileTreeEntity) object);
                }
            }
        });
    }

    private void execute(UserFileTreeEntity userFileTreeEntity) {
        try {
            UserFileController userFileController = UserFileController.getInstance();
            FolderTreeEntity parentTreeFolder = (FolderTreeEntity) userFileTreeEntity.getParent();
            UserFileEntity userFileEntity = (UserFileEntity) userFileTreeEntity.getObject();
            boolean isRoot = parentTreeFolder == null;
            List<String> existingFileNames;
            if (isRoot) {
                ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                existingFileNames = FolderController.getInstance().getRootFileOrFolderNames(project);
            } else { 
                FolderEntity parentFolder = parentTreeFolder.getObject();
                existingFileNames = userFileController.getSiblingFiles(userFileEntity, parentFolder).stream()
                        .map(f -> f.toFile().getName())
                        .collect(Collectors.toList());
            }
            RenameUserFileEntityDialog dialog = new RenameUserFileEntityDialog(parentShell, userFileEntity,
                    existingFileNames);
            if (dialog.open() == RenameUserFileEntityDialog.OK) {
                String newName = dialog.getNewFileName();
                String oldName = userFileEntity.getName();
                if (!StringUtils.isBlank(newName) && !newName.equals(oldName)) {
                    UserFileEntity newFileEntity = UserFileController.getInstance().renameFile(newName, userFileEntity);
                    userFileTreeEntity.setObject(newFileEntity);
                    eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, userFileTreeEntity);
                    eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, userFileTreeEntity);
                }
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to rename file", e.getMessage());
            LoggerSingleton.logError(e);
        }
    
    }
}
