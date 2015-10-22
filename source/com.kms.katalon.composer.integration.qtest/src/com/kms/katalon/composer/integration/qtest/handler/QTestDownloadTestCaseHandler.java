package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.job.DownloadTestCaseJob;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;

public class QTestDownloadTestCaseHandler extends AbstractQTestHandler {
    @Inject
    private ESelectionService selectionService;

    @Inject
    private UISynchronize sync;

    /**
     * @return true if the selected tree item is a test case folder and it is
     *         being integrated with qTest or is a test case root folder.
     */
    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            
            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
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

    @Execute
    public void execute() {
        try {
            Object selectedObject = getFirstSelectedObject(selectionService);
            if (selectedObject instanceof FolderEntity) {
                FolderEntity folder = (FolderEntity) selectedObject;
                if (folder.getFolderType() == FolderType.TESTCASE) {
                    List<IntegratedFileEntity> testCaseEntities = new ArrayList<IntegratedFileEntity>();
                    testCaseEntities.add(folder);
                    DownloadTestCaseJob job = new DownloadTestCaseJob(sync);
                    job.setFileEntities(testCaseEntities);
                    job.doTask();
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
