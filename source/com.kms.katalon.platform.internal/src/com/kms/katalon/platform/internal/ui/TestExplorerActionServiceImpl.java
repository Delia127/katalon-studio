package com.kms.katalon.platform.internal.ui;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.ui.TestExplorerActionService;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class TestExplorerActionServiceImpl implements TestExplorerActionService {

    @Override
    public void refreshFolder(com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity folder) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity parentFolderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity,
                    folder.getId());
            if (parentFolderEntity == null) {
                throw new ResourceException(MessageFormat.format("Folder {0} doesn't exist", folder.getId()));
            }

            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                    TreeEntityUtil.getFolderTreeEntity(parentFolderEntity));
        } catch (ControllerException e) {
            throw new ResourceException("Could not refresh folder", e);
        }
    }

    @Override
    public void selectTestCases(com.katalon.platform.api.model.ProjectEntity project,
            List<com.katalon.platform.api.model.TestCaseEntity> testCases) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            Object[] testCaseTrees = testCases.stream().map(tc -> {
                try {
                    return TestCaseController.getInstance().getTestCaseByDisplayId(tc.getId());
                } catch (ControllerException e) {
                    return null;
                }
            }).filter(entity -> entity != null).map(entity -> {
                try {
                    return TreeEntityUtil.getTestCaseTreeEntity(entity, projectEntity);
                } catch (Exception e) {
                    return null;
                }
            }).filter(treeEntity -> treeEntity != null).collect(Collectors.toList()).toArray();

            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEMS,
                    testCaseTrees);
        } catch (ControllerException e) {
            throw new ResourceException("Could not set selected test cases on Tests Explorer ", e);
        }
    }

}
