package com.kms.katalon.composer.integration.qtest.jobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
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
import com.kms.katalon.integration.qtest.QTestConstants;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.entity.QTestProject;

public class DisintegrateTestCaseJob extends UploadJob {

	public DisintegrateTestCaseJob(String name) {
		super(name);
		setUser(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Disintegrating test cases...", getFileEntities().size());
		
		for (FileEntity fileEntity : getFileEntities()) {
			try {
				if (fileEntity instanceof TestCaseEntity) {
					TestCaseEntity testCaseEntity = (TestCaseEntity) fileEntity;
					String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCaseEntity);
					monitor.subTask("Disintegrating " + testCaseId + "...");
	
					IntegratedEntity testCaseIntegratedEntity = testCaseEntity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
					
					testCaseEntity.getIntegratedEntities().remove(testCaseIntegratedEntity);
					
					TestCaseController.getInstance().updateTestCase(testCaseEntity);					
					EventBrokerSingleton.getInstance().getEventBroker()
							.post(EventConstants.TESTCASE_UPDATED, new Object[] { testCaseEntity.getId(), testCaseEntity });
				} else if (fileEntity instanceof FolderEntity){
					FolderEntity folderEntity = (FolderEntity) fileEntity;
					if (folderEntity.getFolderType() != FolderType.TESTCASE) continue;

					String folderId = FolderController.getInstance().getIdForDisplay(folderEntity);
					monitor.subTask("Disintegrating " + folderId + "...");
					IntegratedEntity folderIntegratedEntity = folderEntity
							.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
					
					if (folderIntegratedEntity != null) {
						TestCaseRepo repo = QTestIntegrationUtil.getTestCaseRepo(folderEntity);
						if (repo != null) {
							if (repo.getFolderId().equals(folderId)) {
								removeFolderIdFromProject(folderId, repo.getQTestProject());
							}
						}
						
						folderEntity.getIntegratedEntities().remove(folderIntegratedEntity);

						FolderController.getInstance().saveFolder(folderEntity);
					}
					
					//Remove all descendant test cases or folders by removing qTest integrated entity in file system
					//No need to remove qTest Test Case because their qTest Module has been removed.
					for (Object childTestCaseEntityObject : FolderController.getInstance().getAllDescentdantEntities(
							folderEntity)) {
						if (!(childTestCaseEntityObject instanceof IntegratedFileEntity)) continue;

						if (childTestCaseEntityObject instanceof TestCaseEntity) {							
							TestCaseEntity testCaseEntity = (TestCaseEntity) childTestCaseEntityObject;
							IntegratedEntity testCaseIntegratedEntity = testCaseEntity
									.getIntegratedEntity(QTestConstants.PRODUCT_NAME);

							if (testCaseIntegratedEntity != null) {
								testCaseEntity.getIntegratedEntities().remove(testCaseIntegratedEntity);

								TestCaseController.getInstance().updateTestCase(testCaseEntity);
								EventBrokerSingleton.getInstance().getEventBroker()
										.post(EventConstants.TESTCASE_UPDATED,
												new Object[] { testCaseEntity.getId(), testCaseEntity });
							}
						} else {
							FolderEntity childFolderEntity = (FolderEntity) childTestCaseEntityObject;
							IntegratedEntity childFolderIntegratedEntity = childFolderEntity
									.getIntegratedEntity(QTestConstants.PRODUCT_NAME);

							if (folderIntegratedEntity != null) {
								childFolderEntity.getIntegratedEntities().remove(childFolderIntegratedEntity);

								FolderController.getInstance().saveFolder(childFolderEntity);
							}
						}
					}

				}
				monitor.worked(1);
			} catch (Exception e) {
				monitor.setCanceled(true);
				return Status.CANCEL_STATUS;
			}

		}
		return Status.OK_STATUS;
	}
	
	private void removeFolderIdFromProject(String folderId, QTestProject qTestProject) throws Exception {
		IntegratedEntity projectIntegratedEntity = projectEntity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
		List<QTestProject> qTestProjects = QTestIntegrationProjectManager.getQTestProjectsByIntegratedEntity(projectIntegratedEntity);
		
		for (QTestProject systemQTestProject : qTestProjects) {
			if (systemQTestProject.equals(qTestProject)) {
				systemQTestProject.getTestCaseFolderIds().remove(folderId);
			}
		}		
		IntegratedEntity projectNewIntegratedEntity = QTestIntegrationProjectManager
				.getIntegratedEntityByQTestProjects(qTestProjects);

		projectEntity = (ProjectEntity) updateFileIntegratedEntity(projectEntity,
				projectNewIntegratedEntity);

		ProjectController.getInstance().updateProject(projectEntity);
	}
	
	private IntegratedFileEntity updateFileIntegratedEntity(IntegratedFileEntity entity, IntegratedEntity newIntegrated) {
		IntegratedEntity oldIntegrated = entity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);

		// Otherwise, add the new one to integrated list
		int index = 0;
		if (oldIntegrated == null) {
			index = entity.getIntegratedEntities().size();
		} else {
			index = entity.getIntegratedEntities().indexOf(oldIntegrated);
			entity.getIntegratedEntities().remove(index);
		}

		if (index >= entity.getIntegratedEntities().size()) {
			entity.getIntegratedEntities().add(newIntegrated);
		} else {
			entity.getIntegratedEntities().add(index, oldIntegrated);
		}

		return entity;
	}

}
