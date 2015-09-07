package com.kms.katalon.composer.integration.qtest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestConstants;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.QTestIntegrationReportManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;

public class QTestIntegrationUtil {
	
	public static List<TestCaseRepo> getTestCaseRepositories(ProjectEntity projectEntity, List<QTestProject> qTestProjects) {
		List<TestCaseRepo> testCaseRepositories = new ArrayList<TestCaseRepo>();
		for (QTestProject qTestProject : qTestProjects) {
			for (String testCaseFolderId : qTestProject.getTestCaseFolderIds()) {
				TestCaseRepo repo = new TestCaseRepo();
				repo.setQTestProject(qTestProject);
				repo.setFolderId(testCaseFolderId);

				FolderEntity folderEntity = null;
				try {
					folderEntity = FolderController.getInstance().getFolderByDisplayId(projectEntity, testCaseFolderId);

					QTestModule qTestModule = null;

					if (folderEntity != null) {
						IntegratedEntity integratedFolderEntity = folderEntity
								.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
						if (integratedFolderEntity != null) {
							qTestModule = QTestIntegrationFolderManager
									.getQTestModuleByIntegratedEntity(integratedFolderEntity);
						}
					}

					if (qTestModule != null) {
						repo.setQTestModule(qTestModule);
					}
				} catch (Exception ex) {
					repo.setQTestModule(null);
				}
				testCaseRepositories.add(repo);
			}
		}
		return testCaseRepositories;
	}
	
	public static List<TestSuiteRepo> getTestSuiteRepositories(ProjectEntity projectEntity, List<QTestProject> qTestProjects) {
		List<TestSuiteRepo> testSuiteRepositories = new ArrayList<TestSuiteRepo>();
		for (QTestProject qTestProject : qTestProjects) {
			for (String testCaseFolderId : qTestProject.getTestSuiteFolderIds()) {
				TestSuiteRepo repo = new TestSuiteRepo();
				repo.setQTestProject(qTestProject);
				repo.setFolderId(testCaseFolderId);
				testSuiteRepositories.add(repo);
			}
		}
		
		return testSuiteRepositories;
	}
	
	public static TestCaseRepo getTestCaseRepo(IntegratedFileEntity entity) throws Exception {
		if (entity == null) return null;

		ProjectEntity projectEntity = entity.getProject();
		IntegratedEntity projectIntegratedEntity = projectEntity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
		if (projectIntegratedEntity == null) return null;
		List<QTestProject> qTestProjects = QTestIntegrationProjectManager
				.getQTestProjectsByIntegratedEntity(projectIntegratedEntity);
		String entityId = entity.getRelativePathForUI().replace(File.separator, "/");

		for (TestCaseRepo testCaseRepo : getTestCaseRepositories(projectEntity, qTestProjects)) {
			String repoFolderId = testCaseRepo.getFolderId();
			if (entityId.startsWith(repoFolderId + "/") || entityId.equals(repoFolderId)) {
				return testCaseRepo;
			}
		}

		return null;
	}
	
	public static TestSuiteRepo getTestSuiteRepo(IntegratedFileEntity entity) throws Exception {
		if (entity == null) return null;

		ProjectEntity projectEntity = entity.getProject();
		IntegratedEntity projectIntegratedEntity = projectEntity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
		if (projectIntegratedEntity == null) return null;
		List<QTestProject> qTestProjects = QTestIntegrationProjectManager
				.getQTestProjectsByIntegratedEntity(projectIntegratedEntity);
		String entityId = entity.getRelativePathForUI().replace(File.separator, "/");
		
		for (TestSuiteRepo testSuiteRepo : getTestSuiteRepositories(projectEntity, qTestProjects)) {
			String repoFolderId = testSuiteRepo.getFolderId();
			if (entityId.startsWith(repoFolderId + "/") || entityId.equals(repoFolderId)) {
				return testSuiteRepo;
			}
		}
		
		return null;
	}
	
	public static boolean canBeDownloadedOrDisintegrated(IntegratedFileEntity entity) throws Exception {
		boolean isIntegrated = (entity.getIntegratedEntity(QTestConstants.PRODUCT_NAME) != null);

		if (entity instanceof FolderEntity) {

			FolderEntity folderEntity = (FolderEntity) entity;
			if (isIntegrated) return true;
			
			if (getTestCaseRepo(entity) == null) return false;
			
			for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
				if (canBeDownloadedOrDisintegrated((IntegratedFileEntity) childEntity)) {
					return true;
				}
			}
			return false;

		} else {
			return isIntegrated;
		}
	}
	
	public static boolean canBeUploaded(IntegratedFileEntity entity) throws Exception {
		if (getTestCaseRepo(entity) == null) return false;
		
		boolean isNotIntegrated = (entity.getIntegratedEntity(QTestConstants.PRODUCT_NAME) == null);

		if (entity instanceof FolderEntity) {
			FolderEntity folderEntity = (FolderEntity) entity;
			for (FileEntity childEntity : FolderController.getInstance().getChildren(folderEntity)) {
				if (canBeUploaded((IntegratedFileEntity) childEntity)) {
					return true;
				}
			}

			return false;

		} else {
			return isNotIntegrated;
		}
	}
	
	public static String getTempDirPath() {
		String tempDir = ProjectController.getInstance().getTempDir();
		File qTestTempFolder = new File(tempDir, QTestConstants.PRODUCT_NAME);
		if (!qTestTempFolder.exists()) {
			qTestTempFolder.mkdirs();
		}
		return qTestTempFolder.getAbsolutePath();
	}
	
	public static IntegratedFileEntity updateFileIntegratedEntity(IntegratedFileEntity entity, IntegratedEntity newIntegrated) {
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
	
	public static void saveReportEntity(ReportEntity reportEntity, QTestLogUploadedPreview uploadedPreview) throws Exception {
		IntegratedEntity reportIntegratedEntity = reportEntity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
		QTestReport qTestReport = QTestIntegrationReportManager.getQTestReportByIntegratedEntity(reportIntegratedEntity);
		
		if (qTestReport == null) {
			qTestReport = new QTestReport();
		}
		
		qTestReport.getTestLogMap().put(uploadedPreview.getTestLogIndex(), uploadedPreview.getQTestLog());
		
		reportIntegratedEntity = QTestIntegrationReportManager.getIntegratedEntityByQTestReport(qTestReport);
		
		reportEntity = (ReportEntity) QTestIntegrationUtil.updateFileIntegratedEntity(reportEntity,
				reportIntegratedEntity);
		ReportController.getInstance().updateReport(reportEntity);	
	}
	
	/**
	 * Called by uploadTestCaseResult. Add testRun to qTestSuite and save the
	 * given qTestSuite into testSuiteEntity.
	 */
	public static void addNewTestRunToTestSuite(TestSuiteEntity testSuiteEntity, IntegratedEntity testSuiteIntegratedEntity,
			QTestSuite qTestSuite, QTestRun testRun, List<QTestSuite> qTestSuiteCollection) throws Exception {
		qTestSuite.getTestRuns().add(testRun);
		
		QTestIntegrationTestSuiteManager.addQTestSuiteToIntegratedEntity(qTestSuite, testSuiteIntegratedEntity, 
				qTestSuiteCollection.indexOf(qTestSuite));

		TestSuiteController.getInstance().updateTestSuite(testSuiteEntity);
	}
}
