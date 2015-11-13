package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.job.UploadTestSuiteJob;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteQTestSuitePair;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class QTestUploadTestSuiteHandler extends AbstractQTestHandler {
    @Inject
    private ESelectionService selectionService;

    private List<TestSuiteQTestSuitePair> fPairs;

    /**
     * @return true if the selected test suite has at least one {@link QTestSuite} that had not been uploaded.
     *         Otherwise, false.
     */
    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
            }

            // Returns if test suite isn't in any TestSuiteRepo.
            if (!QTestIntegrationUtil.canBeUploaded((IntegratedFileEntity) selectedEntity, projectEntity)) {
                return false;
            }

            fPairs = new ArrayList<TestSuiteQTestSuitePair>();
            if (selectedEntity instanceof TestSuiteEntity) {
                qualify((TestSuiteEntity) selectedEntity);
            } else if (selectedEntity instanceof FolderEntity) {
                FolderEntity folderEntity = (FolderEntity) selectedEntity;
                if (folderEntity.getFolderType() != FolderType.TESTSUITE) {
                    return false;
                }
                for (Object childObject : FolderController.getInstance().getAllDescentdantEntities(folderEntity)) {
                    if (childObject instanceof TestSuiteEntity) {
                        qualify((TestSuiteEntity) childObject);
                    }
                }
            }

            return fPairs.size() > 0;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }
    
    private void qualify(TestSuiteEntity testSuite) {
        try {
            List<QTestSuite> unUploadedQTestSuites = QTestIntegrationUtil
                    .getUnuploadedQTestSuites((TestSuiteEntity) testSuite);

            if (unUploadedQTestSuites.size() > 0) {
                TestSuiteQTestSuitePair pair = new TestSuiteQTestSuitePair((TestSuiteEntity) testSuite,
                        unUploadedQTestSuites);
                fPairs.add(pair);
            }
        } catch (QTestInvalidFormatException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Execute
    public void execute() {
        UploadTestSuiteJob job = new UploadTestSuiteJob(fPairs);
        job.doTask();
    }
}
