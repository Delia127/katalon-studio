package com.kms.katalon.composer.integration.qtest.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.jobs.UploadTestCaseJob;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestUploadHandler {

    @Inject
    private UISynchronize sync;

    @Inject
    private ESelectionService selectionService;

    /**
     * @return <code>true</code> if selected item doesn't integrate with qTest
     *         or it has any child item that doesn't integrate with qTest.
     * @see QTestUploadHandler#getNotIntegratedChildren(IntegratedFileEntity)
     *      Otherwise, return <code>false</code>
     */
    @CanExecute
    public boolean canExecute() {
        try {
            if (ProjectController.getInstance().getCurrentProject() == null) return false;
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            String projectDir = projectEntity.getFolderLocation();
            if (!QTestSettingStore.isIntegrationActive(projectDir)) return false;
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects == null || selectedObjects.length > 1) return false;
            if (selectedObjects[0] instanceof ITreeEntity) {
                Object selectedEntity = ((ITreeEntity) selectedObjects[0]).getObject();
                if (selectedEntity instanceof TestCaseEntity) {
                    return QTestIntegrationUtil.canBeUploaded((IntegratedFileEntity) selectedEntity, projectEntity);
                }

                if (selectedEntity instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) selectedEntity;
                    if (folder.getFolderType() == FolderType.TESTCASE) {
                        return QTestIntegrationUtil.canBeUploaded(folder, projectEntity);
                    }
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
        boolean isNotIntegrated = (entity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME) == null);
        List<IntegratedFileEntity> unIntegratedEntities = new ArrayList<IntegratedFileEntity>();
        if (isNotIntegrated) unIntegratedEntities.add((IntegratedFileEntity) entity);

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
            IntegratedEntity parentIntegratedEntitity = parentFolder.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
            if (parentIntegratedEntitity == null) {
                uploadedEntites.add(0, parentFolder);

                addParentToUploadedEntities(parentFolder, uploadedEntites);
            }
        }
    }

    @Execute
    public void execute() {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
            Object selectedObject = treeEntity.getObject();
            if (selectedObject instanceof TestCaseEntity) {
                TestCaseEntity testCaseEntity = (TestCaseEntity) selectedObject;
                List<IntegratedFileEntity> uploadedEntities = new ArrayList<IntegratedFileEntity>();
                addParentToUploadedEntities(testCaseEntity, uploadedEntities);

                uploadedEntities.add(testCaseEntity);

                uploadTestCases(uploadedEntities);
            } else if (selectedObject instanceof FolderEntity) {
                FolderEntity folder = (FolderEntity) selectedObject;
                switch (folder.getFolderType()) {
                    case TESTCASE:
                        List<IntegratedFileEntity> uploadedEntities = getNotIntegratedChildren(folder);
                        uploadTestCases(uploadedEntities);
                        if (folder.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME) == null) {
                            addParentToUploadedEntities(folder, uploadedEntities);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
