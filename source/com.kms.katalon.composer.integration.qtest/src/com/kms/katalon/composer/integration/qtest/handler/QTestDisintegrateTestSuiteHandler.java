package com.kms.katalon.composer.integration.qtest.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.job.DisintegrateTestSuiteJob;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class QTestDisintegrateTestSuiteHandler extends AbstractQTestHandler {

    @Inject
    private ESelectionService selectionService;

    // Represents a list of test suite that each one can be dis-integrated.
    private List<TestSuiteEntity> fTestSuites;

    /**
     * @return true if the selected test suite has at least one {@link QTestSuite} that had been uploaded. Otherwise,
     * false.
     */
    @CanExecute
    public boolean canExecute() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity == null) {
                return false;
            }

            // Check if test suite is in any TestSuiteRepo or not.
            if (!QTestIntegrationUtil.canBeDownloadedOrDisintegrated((IntegratedFileEntity) selectedEntity,
                    projectEntity)) {
                return false;
            }

            fTestSuites = new ArrayList<TestSuiteEntity>();
            if (selectedEntity instanceof TestSuiteEntity) {
                if (getUploadedQTestSuites((TestSuiteEntity) selectedEntity).size() > 0) {
                    fTestSuites.add((TestSuiteEntity) selectedEntity);
                } else {
                    return false;
                }
            } else if (selectedEntity instanceof FolderEntity) {
                FolderEntity folderEntity = (FolderEntity) selectedEntity;
                if (folderEntity.getFolderType() != FolderType.TESTSUITE) {
                    return false;
                }

                for (Object childObject : FolderController.getInstance().getAllDescentdantEntities(folderEntity)) {
                    if (childObject instanceof TestSuiteEntity
                            && getUploadedQTestSuites((TestSuiteEntity) childObject).size() > 0) {
                        fTestSuites.add((TestSuiteEntity) childObject);
                    }
                }
            }

            return fTestSuites.size() > 0;

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    /**
     * Removes all {@link QTestSuite} in the current <code>testSuite</code>
     */
    @Execute
    public void execute() {
        if (fTestSuites == null) {
            return;
        }

        if (!MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                StringConstants.VIEW_CONFIRM_DISINTEGRATE_TEST_SUITE)) {
            return;
        }
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            Object selectedEntity = getFirstSelectedObject(selectionService);
            if (selectedEntity instanceof FolderEntity) {
                //Remove repo in project settings.
                FolderEntity folderEntity = (FolderEntity) selectedEntity;
                String folderId = folderEntity.getIdForDisplay();
                TestSuiteRepo repo = QTestIntegrationUtil.getTestSuiteRepo(folderEntity, projectEntity);
                
                IntegratedEntity folderIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(folderEntity);
                IntegratedEntity integratedProjectEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);
                List<TestSuiteRepo> testSuiteRepositories = QTestIntegrationUtil.getTestSuiteRepositories(projectEntity, QTestIntegrationProjectManager
                        .getQTestProjectsByIntegratedEntity(integratedProjectEntity));
                testSuiteRepositories.remove(repo);
                
                if (repo != null && repo.getFolderId().equals(folderId)) {
                    removeTestSuiteRepoFromProject(folderId, repo.getQTestProject());
                    saveFolder(folderEntity, folderIntegratedEntity);
                }
            }

            DisintegrateTestSuiteJob job = new DisintegrateTestSuiteJob(fTestSuites);
            job.schedule();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    /**
     * Returns a list of {@link QTestSuite} that each item has been uploaded of a {@link TestSuiteEntity}.
     * 
     * @param testSuite
     * @return
     * @throws QTestInvalidFormatException
     * : thrown if the test suite is invalid qTest integrated information.
     */
    private List<QTestSuite> getUploadedQTestSuites(TestSuiteEntity testSuite) throws QTestInvalidFormatException {
        List<QTestSuite> qTestSuites = QTestIntegrationTestSuiteManager
                .getQTestSuiteListByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(testSuite));
        List<QTestSuite> uploadedQTestSuites = new ArrayList<QTestSuite>();

        for (QTestSuite availableQTestSuite : qTestSuites) {
            if (availableQTestSuite.getId() > 0) {
                uploadedQTestSuites.add(availableQTestSuite);
            }
        }

        return uploadedQTestSuites;
    }

    /**
     * Removes test case folder that's id equals with the given <code>folderId</code> from the current
     * {@link ProjectEntity}.
     * 
     * @param folderId
     * @param qTestProject
     * @throws Exception
     */
    private void removeTestSuiteRepoFromProject(String folderId, QTestProject qTestProject) throws Exception {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);
        List<QTestProject> qTestProjects = QTestIntegrationProjectManager
                .getQTestProjectsByIntegratedEntity(projectIntegratedEntity);

        for (QTestProject systemQTestProject : qTestProjects) {
            if (systemQTestProject.equals(qTestProject)) {
                systemQTestProject.getTestSuiteFolderIds().remove(folderId);
            }
        }
        IntegratedEntity projectNewIntegratedEntity = QTestIntegrationProjectManager
                .getIntegratedEntityByQTestProjects(qTestProjects);

        projectEntity = (ProjectEntity) QTestIntegrationUtil.updateFileIntegratedEntity(projectEntity,
                projectNewIntegratedEntity);

        ProjectController.getInstance().updateProject(projectEntity);
    }

    private void saveFolder(FolderEntity folderEntity, IntegratedEntity folderIntegratedEntity) throws Exception {
        folderEntity.getIntegratedEntities().remove(folderIntegratedEntity);

        FolderController.getInstance().saveFolder(folderEntity);
    }
}
