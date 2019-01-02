package com.kms.katalon.platform.internal.controller;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.Integration;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.platform.internal.entity.TestCaseEntityImpl;

public class TestCaseControllerImpl implements com.katalon.platform.api.controller.TestCaseController {

    private static TestCaseController testCaseController = TestCaseController.getInstance();

    private static FolderController folderController = FolderController.getInstance();

    @Override
    public String getAvailableTestCaseName(com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, String name) throws ResourceException {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getProject(project.getId());
            if (projectEntity == null) {
                throw new ResourceException(MessageFormat.format("Project {0} doesn't exist", project.getId()));
            }
            FolderEntity folder = folderController.getFolderByDisplayId(projectEntity, parentFolder.getId());

            return testCaseController.getAvailableTestCaseName(folder, name);
        } catch (Exception e) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.TestCaseEntity getTestCase(
            com.katalon.platform.api.model.ProjectEntity project, String testCaseId) throws ResourceException {
        try {
            return new TestCaseEntityImpl(testCaseController.getTestCaseByDisplayId(testCaseId));
        } catch (ControllerException ex) {
            throw new ResourceException(ExceptionsUtil.getMessageForThrowable(ex));
        }
    }

    @Override
    public com.katalon.platform.api.model.TestCaseEntity newTestCase(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.FolderEntity parentFolder, 
            NewDescription newDescription) throws ResourceException {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            FolderEntity folder = folderController.getFolderByDisplayId(currentProject, parentFolder.getId());
            if (folder == null) {
                throw new ResourceException("Folder not found");
            }
            TestCaseEntity testCase = new TestCaseEntity();
            testCase.setName(newDescription.getName());
            testCase.setDescription(newDescription.getDescription());
            testCase.setComment(newDescription.getComment());
            testCase.setParentFolder(folder);
            testCase.setProject(currentProject);

            return new TestCaseEntityImpl(testCaseController.saveNewTestCase(testCase));
        } catch (ControllerException e) {
            throw new ResourceException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.TestCaseEntity updateIntegration(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.TestCaseEntity testCase,
            Integration integration) throws ResourceException {
        if (testCase == null) {
            throw new IllegalArgumentException("testCase cannot be null");
        }
        if (integration == null) {
            throw new IllegalArgumentException("integration cannot be null");
        }
        String integrationName = integration.getName();
        if (StringUtils.isEmpty(integrationName)) {
            throw new IllegalArgumentException("integration name cannot be null");
        }
        try {
            TestCaseEntity testCaseEntity = testCaseController.getTestCaseByDisplayId(testCase.getId());

            IntegratedEntity newIntegrated = new IntegratedEntity();
            newIntegrated.setProductName(integrationName);
            newIntegrated.setProperties(integration.getProperties());

            testCaseEntity.updateIntegratedEntity(newIntegrated);

            return new TestCaseEntityImpl(testCaseController.updateTestCase(testCaseEntity));
        } catch (Exception e) {
            throw new ResourceException(ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public com.katalon.platform.api.model.TestCaseEntity updateTestCase(
            com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.TestCaseEntity testCase,
            UpdateDescription updateDescription) throws ResourceException {
        try {
            TestCaseEntity testCaseEntity = testCaseController.getTestCaseByDisplayId(testCase.getId());

            testCaseEntity.setDescription(updateDescription.getDescription());
            testCaseEntity.setComment(updateDescription.getComment());

            TestCaseEntity updated = testCaseController.updateTestCase(testCaseEntity);

            return new TestCaseEntityImpl(updated);
        } catch (Exception e) {
            throw new ResourceException(ExceptionUtils.getMessage(e));
        }
    }

}
