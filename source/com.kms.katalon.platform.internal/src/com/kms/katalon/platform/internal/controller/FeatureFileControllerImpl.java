package com.kms.katalon.platform.internal.controller;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.katalon.platform.api.exception.ResourceException;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.platform.internal.entity.SystemFileEntityImpl;

public class FeatureFileControllerImpl implements com.katalon.platform.api.controller.FeatureFileController {

    @Override
    public String getAvailableFeatureFileName(com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name) throws ResourceException {
        try {
            String baseName = FilenameUtils.getBaseName(name);
            String fileExtension = FilenameUtils.getExtension(name);
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            FolderEntity sourceFolder = FolderController.getInstance().getFolderByDisplayId(projectEntity,
                    parentFolder.getId());

            List<String> currentNames = SystemFileController.getInstance()
                    .getChildren(sourceFolder)
                    .stream()
                    .map(f -> f.getName())
                    .collect(Collectors.toList());
            String newName = String.format("%s.%s", baseName, fileExtension);
            int index = 0;

            while (currentNames.contains(newName)) {
                index += 1;
                newName = String.format("%s %d.%s", baseName, index, fileExtension);
            }
            return newName;
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.SystemFileEntity getFeatureFile(
            com.katalon.platform.api.model.ProjectEntity project, String featureFileName) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            FolderEntity featuresFolder = FolderController.getInstance().getFeatureRoot(projectEntity);
            File featureFile = new File(featuresFolder.getLocation(), featureFileName);
            SystemFileEntity systemFileEntity = SystemFileController.getInstance()
                    .getSystemFile(featureFile.getAbsolutePath(), projectEntity);
            return new SystemFileEntityImpl(systemFileEntity);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.SystemFileEntity newFeatureFile(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            FolderEntity sourceFolder = FolderController.getInstance().getFolderByDisplayId(projectEntity,
                    parentFolder.getId());
            SystemFileEntity systemFileEntity = SystemFileController.getInstance().newFile(name, "", sourceFolder);
            return new SystemFileEntityImpl(systemFileEntity);
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }
}
