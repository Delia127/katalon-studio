package com.kms.katalon.composer.integration.qtest.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.dialog.TestCaseRootSelectionDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class UploadTestCaseJob extends UploadJob {

	private UISynchronize sync;
	private boolean isMergeConfirmed;
	private boolean isUserCanceled;
	private QTestModule qTestSelectedModule;

	public UploadTestCaseJob(String name, UISynchronize sync) {
		super(name);
		setUser(true);
		this.sync = sync;
		isUserCanceled = false;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Uploading test cases...", getFileEntities().size());
		String projectDir = projectEntity.getFolderLocation();

		for (FileEntity fileEntity : getFileEntities()) {
			try {
				if (isUserCanceled) {
					return Status.CANCEL_STATUS;
				}

				isMergeConfirmed = false;
				if (fileEntity instanceof TestCaseEntity) {
					TestCaseEntity testCaseEntity = (TestCaseEntity) fileEntity;
					uploadTestCase(testCaseEntity, monitor, projectDir);
				} else if (fileEntity instanceof FolderEntity) {
					FolderEntity folderEntity = (FolderEntity) fileEntity;
					uploadFolder(folderEntity, monitor, projectDir);
				}
				monitor.worked(1);
			} catch (Exception e) {
				performErrorNotification(e);
				monitor.setCanceled(true);
				LoggerSingleton.logError(e);
				return Status.CANCEL_STATUS;
			}

		}
		return Status.OK_STATUS;
	}

	private void uploadFolder(FolderEntity folderEntity, IProgressMonitor monitor, String projectDir) throws Exception {
		String folderId = FolderController.getInstance().getIdForDisplay(folderEntity);
		monitor.subTask("Uploading " + folderId + "...");

		QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(folderEntity).getQTestProject();

		if (folderEntity.getParentFolder() == null) {
			// folder is root of test case
			// get moduleRoot from qTest and all its children for user can
			// select
			QTestModule moduleRoot = QTestIntegrationFolderManager.getModuleRoot(projectDir, qTestProject.getId());
			QTestIntegrationFolderManager.updateModule(projectDir, qTestProject.getId(), moduleRoot, true);

			performTestCaseRootSelection(moduleRoot);
			if (qTestSelectedModule != null) {
				IntegratedEntity folderIntegratedEntity = QTestIntegrationFolderManager
						.getFolderIntegratedEntityByQTestModule(qTestSelectedModule);
				folderEntity.getIntegratedEntities().add(folderIntegratedEntity);

				FolderController.getInstance().saveFolder(folderEntity);
			} else {
				isUserCanceled = true;
				return;
			}
		} else {
			QTestModule qTestParentModule = QTestIntegrationFolderManager.getQTestModuleByFolderEntity(projectDir,
					folderEntity.getParentFolder());

			QTestModule qTestModule = null;
			if (QTestSettingStore.isEnableCheckBeforeUploading(projectDir)) {
				QTestIntegrationFolderManager.updateModule(projectDir, qTestProject.getId(), qTestParentModule, false);

				for (QTestModule siblingQTestModule : qTestParentModule.getChildModules()) {
					if (!folderEntity.getName().equalsIgnoreCase(siblingQTestModule.getName())) continue;
					// let user choose merge or not
					performFolderDuplicatedConfirmation(folderId, siblingQTestModule);

					if (isMergeConfirmed) {
						qTestModule = siblingQTestModule;
						break;
					}
				}
			}

			if (qTestModule == null) {
				qTestModule = createNewQTestModule(qTestProject, qTestParentModule, projectDir, folderEntity);
			}

			folderEntity.getIntegratedEntities().add(
					QTestIntegrationFolderManager.getFolderIntegratedEntityByQTestModule(qTestModule));

			FolderController.getInstance().saveFolder(folderEntity);
		}
	}

	private void uploadTestCase(TestCaseEntity testCaseEntity, IProgressMonitor monitor, String projectDir)
			throws Exception {
		QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity).getQTestProject();
		String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCaseEntity);
		monitor.subTask("Uploading " + testCaseId + "...");

		QTestModule qTestParentModule = QTestIntegrationFolderManager.getQTestModuleByFolderEntity(projectDir,
				testCaseEntity.getParentFolder());

		QTestTestCase qTestTestCase = null;

		if (QTestSettingStore.isEnableCheckBeforeUploading(projectDir)) {
			QTestIntegrationFolderManager.updateModule(projectDir, qTestProject.getId(), qTestParentModule, false);

			for (QTestTestCase siblingQTestCase : qTestParentModule.getChildTestCases()) {
				if (!testCaseEntity.getName().equalsIgnoreCase(siblingQTestCase.getName())) continue;
				// let user choose merge or not
				performTestCaseDuplicatedConfirmation(testCaseId, siblingQTestCase);

				if (isMergeConfirmed) {
					qTestTestCase = siblingQTestCase;
					break;
				}
			}
		}

		if (qTestTestCase == null) {
			qTestTestCase = createNewQTestCase(qTestProject, qTestParentModule, projectDir, testCaseEntity);
		}

		if (qTestTestCase.getVersionId() == 0) {
			qTestTestCase.setVersionId(QTestIntegrationTestCaseManager.getTestCaseVersionId(projectDir,
					qTestProject.getId(), qTestTestCase.getId()));
		}

		testCaseEntity.getIntegratedEntities().add(
				QTestIntegrationTestCaseManager.getIntegratedEntityByQTestTestCase(qTestTestCase));

		TestCaseController.getInstance().updateTestCase(testCaseEntity);

		EventBrokerSingleton.getInstance().getEventBroker()
				.post(EventConstants.TESTCASE_UPDATED, new Object[] { testCaseEntity.getId(), testCaseEntity });
	}

	private QTestTestCase createNewQTestCase(QTestProject qTestProject, QTestModule qTestParentModule,
			String projectDir, TestCaseEntity testCaseEntity) throws Exception {
		return QTestIntegrationTestCaseManager.addTestCase(qTestProject, qTestParentModule.getId(),
				testCaseEntity.getName(), testCaseEntity.getDescription(), "", projectDir);
	}

	private QTestModule createNewQTestModule(QTestProject qTestProject, QTestModule qTestParentModule,
			String projectDir, FolderEntity folderEntity) {
		return QTestIntegrationFolderManager.createNewQTestTCFolder(projectDir, qTestProject.getId(),
				qTestParentModule.getId(), folderEntity.getName());
	}

	private void performErrorNotification(final Exception ex) {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				MultiStatusErrorDialog.showErrorDialog(ex, "Unable to upload test cases.", ex.getClass()
						.getSimpleName());
			}
		});
	}

	private void performTestCaseDuplicatedConfirmation(final String testCaseId, final QTestTestCase siblingQTestCase) {
		sync.syncExec(new Runnable() {

			@Override
			public void run() {
				isMergeConfirmed = MessageDialog.open(MessageDialog.QUESTION, null, "Test Case Duplication Detected",
						"System has detected that a test case on qTest with id: " + siblingQTestCase.getId()
								+ " has the same name as test case: " + testCaseId + ".\nDo you want to merge them?",
						SWT.NONE);
			}
		});
	}

	private void performFolderDuplicatedConfirmation(final String folderId, final QTestModule siblingQTestModule) {
		sync.syncExec(new Runnable() {

			@Override
			public void run() {
				isMergeConfirmed = MessageDialog.open(MessageDialog.QUESTION, null, "Folder Duplication Detected",
						"System has detected that a test folder on qTest with id: " + siblingQTestModule.getId()
								+ " has the same name as test case folder: " + folderId
								+ ".\nDo you want to merge them?", SWT.NONE);
			}
		});
	}

	/**
	 * Open a confirmation dialog that requires user choose test case root
	 * folder
	 * 
	 * @param moduleRoot
	 */
	private void performTestCaseRootSelection(final QTestModule moduleRoot) {
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				TestCaseRootSelectionDialog testCaseRootSelectionDialog = new TestCaseRootSelectionDialog(Display
						.getDefault().getActiveShell(), moduleRoot, false);
				if (testCaseRootSelectionDialog.open() == Dialog.OK) {
					qTestSelectedModule = testCaseRootSelectionDialog.getSelectedModule();
				} else {
					isUserCanceled = true;
				}
			}
		});
	}
}
