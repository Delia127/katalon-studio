package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.job.DisintegrateTestCaseJob;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class QTestDisintegrateTestCaseHandler extends AbstractQTestHandler {

    @Inject
    private ESelectionService selectionService;

    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
            }

            if (selectedEntity instanceof TestCaseEntity) {
                return QTestIntegrationUtil.canBeDownloadedOrDisintegrated((IntegratedFileEntity) selectedEntity,
                        projectEntity);
            }

            if (selectedEntity instanceof FolderEntity) {
                FolderEntity folder = (FolderEntity) selectedEntity;
                if (folder.getFolderType() == FolderType.TESTCASE) {
                    return QTestIntegrationUtil.canBeDownloadedOrDisintegrated(folder, projectEntity);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    private List<IntegratedFileEntity> getIntegratedChildren(IntegratedFileEntity entity) {
        List<IntegratedFileEntity> integratedEntities = new ArrayList<IntegratedFileEntity>();
        integratedEntities.add((IntegratedFileEntity) entity);
        return integratedEntities;
    }

    private void disintegrateTestCases(List<IntegratedFileEntity> integratedTestCases) {
        DisintegrateTestCaseJob job = new DisintegrateTestCaseJob(true);
        job.setFileEntities(integratedTestCases);
        job.doTask();
    }

    @Execute
    public void execute() {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
            Object selectedObject = treeEntity.getObject();
            if (selectedObject instanceof TestCaseEntity) {
                if (MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                        StringConstants.DIA_CONFIRM_DISINTEGRATE_TEST_CASE)) {
                    disintegrateTestCases(getIntegratedChildren((IntegratedFileEntity) selectedObject));
                }
            } else if (selectedObject instanceof FolderEntity) {
                if (MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                        StringConstants.DIA_CONFIRM_DISINTEGRATE_TEST_CASE_FOLDER)) {
                    FolderEntity folder = (FolderEntity) selectedObject;
                    switch (folder.getFolderType()) {
                        case TESTCASE:
                            disintegrateTestCases(getIntegratedChildren(folder));
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
