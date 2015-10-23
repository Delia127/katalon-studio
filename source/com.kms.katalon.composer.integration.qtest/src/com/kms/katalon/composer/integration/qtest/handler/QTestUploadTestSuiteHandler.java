package com.kms.katalon.composer.integration.qtest.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.job.UploadTestSuiteJob;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.entity.QTestSuite;

public class QTestUploadTestSuiteHandler extends AbstractQTestHandler {
    @Inject
    private ESelectionService selectionService;

    private TestSuiteEntity testSuite;

    /**
     * @return true if the selected test suite has at least one {@link QTestSuite} that had not been uploaded.
     * Otherwise, false.
     */
    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
            }
            
            if (!(selectedEntity instanceof TestSuiteEntity)) {
                return false;
            }

            // Returns if test suite isn't in any TestSuiteRepo.
            if (!QTestIntegrationUtil.canBeUploaded((IntegratedFileEntity) selectedEntity, projectEntity)) {
                return false;
            }

            testSuite = (TestSuiteEntity) selectedEntity;
            return QTestIntegrationUtil.getUnuploadedQTestSuites(testSuite).size() > 0;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute() {
        UploadTestSuiteJob job = new UploadTestSuiteJob(testSuite);
        job.doTask();
    }
}
