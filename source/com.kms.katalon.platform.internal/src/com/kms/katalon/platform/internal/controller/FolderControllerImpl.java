package com.kms.katalon.platform.internal.controller;

import java.text.MessageFormat;

import com.katalon.platform.api.exception.ResourceException;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.platform.internal.entity.FolderEntityImpl;

public class FolderControllerImpl implements com.katalon.platform.api.controller.FolderController {
    private static FolderController folderController = FolderController.getInstance();

    @Override
    public String getAvailableFolderName(com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity parentFolderEntity = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());
            if (parentFolderEntity == null) {
                throw new ResourceException(
                        MessageFormat.format("Parent folder {0} doesn't exist", parentFolder.getId()));
            }
            return folderController.getAvailableFolderName(parentFolderEntity, name);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.FolderEntity getFolder(com.katalon.platform.api.model.ProjectEntity project,
            String folderId) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity folderEntity = folderController.getFolderByDisplayId(projectEntity, folderId);
            return folderEntity != null ? new FolderEntityImpl(folderEntity) : null;
        } catch (Exception e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.FolderEntity newFolder(com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity parentFolderEntity = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());
            if (parentFolderEntity == null) {
                throw new ResourceException(
                        MessageFormat.format("Parent folder {0} doesn't exist", parentFolder.getId()));
            }
            return new FolderEntityImpl(folderController.addNewFolder(parentFolderEntity, name));
        } catch (Exception e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }
}
