package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.job.UploadTestCaseJob;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class QTestUploadTestCaseHandler extends AbstractQTestHandler {

    @Inject
    private UISynchronize sync;

    @Inject
    private ESelectionService selectionService;

    /**
     * @return <code>true</code> if selected item doesn't integrate with qTest
     *         or it has any child item that doesn't integrate with qTest.
     * @see QTestUploadTestCaseHandler#getNotIntegratedChildren(IntegratedFileEntity)
     *      Otherwise, return <code>false</code>
     */
    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            
            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
            }
            
            if (selectedEntity instanceof TestCaseEntity) {
                return QTestIntegrationUtil.canBeUploaded((IntegratedFileEntity) selectedEntity, projectEntity);
            }

            if (selectedEntity instanceof FolderEntity) {
                FolderEntity folder = (FolderEntity) selectedEntity;
                if (folder.getFolderType() == FolderType.TESTCASE) {
                    return QTestIntegrationUtil.canBeUploaded(folder, projectEntity);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;

    }

    /**
     * Get all children entities (includes the given entity itself if satisfied)
     * that don't integrate with qTest
     * 
     * @param entity
     *            : {@link TestCaseEntity or FolderEntity}
     * @return a collection of {@link IntegratedFileEntity}
     */
    private List<IntegratedFileEntity> getNotIntegratedChildren(IntegratedFileEntity entity) {
        boolean isNotIntegrated = QTestIntegrationUtil.getIntegratedEntity(entity) == null;
        
        List<IntegratedFileEntity> unIntegratedEntities = new ArrayList<IntegratedFileEntity>();
        
        if (isNotIntegrated) {
            unIntegratedEntities.add((IntegratedFileEntity) entity);
        }

        if (entity instanceof FolderEntity) {
            FolderEntity folderEntity = (FolderEntity) entity;
            try {
                for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
                    unIntegratedEntities.addAll(getNotIntegratedChildren((IntegratedFileEntity) childEntity));
                }

            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return Collections.emptyList();
            }

        }

        return unIntegratedEntities;
    }

    /**
     * Create an {@link UploadTestCaseJob}, set input and execute
     * 
     * @param unIntegratedTestCases
     */
    private void uploadTestCases(List<IntegratedFileEntity> unIntegratedTestCases) {
        UploadTestCaseJob job = new UploadTestCaseJob("Upload test cases", sync);
        job.setFileEntities(unIntegratedTestCases);
        job.doTask();
    }

    /**
     * Put parent of the given childEntity to the given uploadedEntites if its
     * parent doesn't integrate with qTest
     * 
     * @param childEntity
     * @param uploadedEntites
     */
    public static void addParentToUploadedEntities(IntegratedFileEntity childEntity,
            List<IntegratedFileEntity> uploadedEntites) {

        FolderEntity parentFolder = childEntity.getParentFolder();
        if (parentFolder != null) {
            IntegratedEntity parentIntegratedEntitity = QTestIntegrationUtil.getIntegratedEntity(parentFolder);
            if (parentIntegratedEntitity == null) {
                uploadedEntites.add(0, parentFolder);

                addParentToUploadedEntities(parentFolder, uploadedEntites);
            }
        }
    }

    @Execute
    public void execute() {
        try {
            Object selectedObject = getFirstSelectedObject(selectionService);
            if (selectedObject instanceof TestCaseEntity) {
                TestCaseEntity testCaseEntity = (TestCaseEntity) selectedObject;
                List<IntegratedFileEntity> uploadedEntities = new ArrayList<IntegratedFileEntity>();
                addParentToUploadedEntities(testCaseEntity, uploadedEntities);

                uploadedEntities.add(testCaseEntity);

                uploadTestCases(uploadedEntities);
            } else if (selectedObject instanceof FolderEntity) {
                FolderEntity folder = (FolderEntity) selectedObject;
                
                if (folder.getFolderType() != FolderType.TESTCASE) {
                    return;
                }
                
                List<IntegratedFileEntity> uploadedEntities = getNotIntegratedChildren(folder);
                uploadTestCases(uploadedEntities);
                if (QTestIntegrationUtil.getIntegratedEntity(folder) == null) {
                    addParentToUploadedEntities(folder, uploadedEntities);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
