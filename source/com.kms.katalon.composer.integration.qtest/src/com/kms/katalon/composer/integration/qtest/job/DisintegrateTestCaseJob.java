package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.StatusUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.model.TestCaseRepo;

public class DisintegrateTestCaseJob extends QTestJob {

    private boolean fCleanRepo;

    public DisintegrateTestCaseJob(boolean cleanRepo) {
        super(StringConstants.JOB_TITLE_DISINTEGRATE_TEST_CASE);
        setUser(true);
        fCleanRepo = cleanRepo;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(StringConstants.JOB_TASK_DISINTEGRATE_TEST_CASE, getFileEntities().size());

        for (FileEntity fileEntity : getFileEntities()) {
            try {
                if (fileEntity instanceof TestCaseEntity) {
                    TestCaseEntity testCaseEntity = (TestCaseEntity) fileEntity;
                    String testCaseId = testCaseEntity.getIdForDisplay();
                    monitor.subTask(
                            MessageFormat.format(StringConstants.JOB_SUB_TASK_DISINTEGRATE_TEST_CASE, testCaseId));

                    IntegratedEntity testCaseIntegratedEntity = QTestIntegrationUtil
                            .getIntegratedEntity(testCaseEntity);

                    testCaseEntity.getIntegratedEntities().remove(testCaseIntegratedEntity);

                    TestCaseController.getInstance().updateTestCase(testCaseEntity);
                    getEventBroker().post(EventConstants.TESTCASE_UPDATED,
                            new Object[] { testCaseEntity.getId(), testCaseEntity });
                    getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                            TreeEntityUtil.getTestCaseTreeEntity(testCaseEntity, projectEntity));
                } else if (fileEntity instanceof FolderEntity) {
                    FolderEntity folderEntity = (FolderEntity) fileEntity;
                    if (folderEntity.getFolderType() != FolderType.TESTCASE) {
                        continue;
                    }

                    String folderId = folderEntity.getIdForDisplay();
                    monitor.subTask(
                            MessageFormat.format(StringConstants.JOB_SUB_TASK_DISINTEGRATE_TEST_CASE, folderId));
                    IntegratedEntity folderIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(folderEntity);

                    if (folderIntegratedEntity != null) {
                        TestCaseRepo repo = QTestIntegrationUtil.getTestCaseRepo(folderEntity, projectEntity);
                        if (repo != null && repo.getFolderId().equals(folderId)) {
                            if (fCleanRepo) {
                                removeTestCaseRepoFromProject(folderId, repo.getQTestProject());
                                saveFolder(folderEntity, folderIntegratedEntity);
                            }
                        } else {
                            saveFolder(folderEntity, folderIntegratedEntity);
                        }
                    }

                    // Remove all descendant test cases or folders by removing qTest integrated entity in file system
                    // No need to remove qTest Test Case because their qTest Module has been removed.
                    for (Object childTestCaseEntityObject : FolderController.getInstance()
                            .getAllDescentdantEntities(folderEntity)) {
                        if (!(childTestCaseEntityObject instanceof IntegratedFileEntity)) {
                            continue;
                        }

                        if (childTestCaseEntityObject instanceof TestCaseEntity) {
                            TestCaseEntity testCaseEntity = (TestCaseEntity) childTestCaseEntityObject;
                            IntegratedEntity testCaseIntegratedEntity = QTestIntegrationUtil
                                    .getIntegratedEntity(testCaseEntity);

                            if (testCaseIntegratedEntity != null) {
                                testCaseEntity.getIntegratedEntities().remove(testCaseIntegratedEntity);

                                TestCaseController.getInstance().updateTestCase(testCaseEntity);
                                getEventBroker().post(EventConstants.TESTCASE_UPDATED,
                                        new Object[] { testCaseEntity.getId(), testCaseEntity });
                            }
                        } else {
                            FolderEntity childFolderEntity = (FolderEntity) childTestCaseEntityObject;
                            IntegratedEntity childFolderIntegratedEntity = QTestIntegrationUtil
                                    .getIntegratedEntity(childFolderEntity);
                            if (folderIntegratedEntity == null) {
                                continue;
                            }
                            childFolderEntity.getIntegratedEntities().remove(childFolderIntegratedEntity);

                            FolderController.getInstance().saveFolder(childFolderEntity);
                        }
                        
                        getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                                TreeEntityUtil.createSelectedTreeEntityHierachy(folderEntity,
                                        FolderController.getInstance().getTestCaseRoot(projectEntity)));
                    }

                }
                monitor.worked(1);
            } catch (Exception e) {
                monitor.setCanceled(true);
                return StatusUtil.getErrorStatus(getClass(), e);
            }

        }
        return Status.OK_STATUS;
    }

    private IEventBroker getEventBroker() {
        return EventBrokerSingleton.getInstance().getEventBroker();
    }

    private void saveFolder(FolderEntity folderEntity, IntegratedEntity folderIntegratedEntity) throws Exception {
        folderEntity.getIntegratedEntities().remove(folderIntegratedEntity);

        FolderController.getInstance().saveFolder(folderEntity);
    }

    /**
     * Removes test case folder that's id equals with the given <code>folderId</code> from the current
     * {@link ProjectEntity}.
     * 
     * @param folderId
     * @param qTestProject
     * @throws Exception
     */
    private void removeTestCaseRepoFromProject(String folderId, QTestProject qTestProject) throws Exception {
        IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(projectEntity);
        List<QTestProject> qTestProjects = QTestIntegrationProjectManager
                .getQTestProjectsByIntegratedEntity(projectIntegratedEntity);

        for (QTestProject systemQTestProject : qTestProjects) {
            if (systemQTestProject.equals(qTestProject)) {
                systemQTestProject.getTestCaseFolderIds().remove(folderId);
            }
        }
        IntegratedEntity projectNewIntegratedEntity = QTestIntegrationProjectManager
                .getIntegratedEntityByQTestProjects(qTestProjects);

        projectEntity = (ProjectEntity) QTestIntegrationUtil.updateFileIntegratedEntity(projectEntity,
                projectNewIntegratedEntity);

        ProjectController.getInstance().updateProject(projectEntity);
    }
}
