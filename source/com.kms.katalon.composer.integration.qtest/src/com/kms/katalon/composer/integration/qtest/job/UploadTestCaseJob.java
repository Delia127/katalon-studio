package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.SynchronizedConfirmationDialog;
import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;
import com.kms.katalon.composer.components.impl.util.StatusUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;

public class UploadTestCaseJob extends QTestJob {

    private UISynchronize sync;

    private List<IntegratedFileEntity> uploadedEntities;

    private IQTestCredential credential;

    public UploadTestCaseJob(String name, UISynchronize sync) {
        super(name);
        setUser(true);
        this.sync = sync;
        credential = QTestSettingCredential.getCredential(getProjectDir());
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        uploadedEntities = new ArrayList<IntegratedFileEntity>();

        monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_CASE, getFileEntities().size());

        for (FileEntity fileEntity : getFileEntities()) {
            try {
                if (monitor.isCanceled()) {
                    return canceled();
                }

                if (fileEntity instanceof TestCaseEntity) {
                    TestCaseEntity testCaseEntity = (TestCaseEntity) fileEntity;
                    uploadTestCase(testCaseEntity, monitor);
                } else if (fileEntity instanceof FolderEntity) {
                    FolderEntity folderEntity = (FolderEntity) fileEntity;
                    uploadFolder(folderEntity, monitor);
                }
                monitor.worked(1);
            } catch (OperationCanceledException e) {
                return canceled();
            } catch (Exception e) {
                performErrorNotification(e);
                monitor.setCanceled(true);
                LoggerSingleton.logError(e);
                return StatusUtil.getErrorStatus(getClass(), e);
            }
        }
        return Status.OK_STATUS;
    }

    /**
     * Cancels the uploading progress. If there is any uploaded items, opens a confirmation to let user keep them or
     * not.
     * 
     * @return {@link Status#CANCEL_STATUS}
     */
    private IStatus canceled() {
        final int uploadedCount = uploadedEntities.size();
        if (uploadedCount == 0) {
            return Status.CANCEL_STATUS;
        }

        SynchronizedConfirmationDialog dialog = new SynchronizedConfirmationDialog() {
            @Override
            public void run() {
                boolean confirmed = MessageDialog.open(MessageDialog.QUESTION, null, StringConstants.CONFIRMATION,
                        MessageFormat.format(StringConstants.JOB_MSG_CONFIRM_CANCEL_UPLOAD, uploadedCount), SWT.NONE);
                setConfirmedValue(confirmed ? YesNoAllOptions.YES : YesNoAllOptions.NO);
            }
        };
        sync.syncExec(dialog);

        if (dialog.getConfirmedValue() == YesNoAllOptions.NO) {
            DisintegrateTestCaseJob job = new DisintegrateTestCaseJob(false);
            job.setFileEntities(uploadedEntities);
            job.doTask();
        }
        return Status.CANCEL_STATUS;
    }

    private void uploadFolder(FolderEntity folderEntity, IProgressMonitor monitor) throws Exception {
        String folderId = folderEntity.getIdForDisplay();
        monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_UPLOAD_TEST_CASE, folderId));

        QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(folderEntity, projectEntity).getQTestProject();

        QTestModule qTestParentModule = QTestIntegrationFolderManager.getQTestModuleByFolderEntity(folderEntity
                .getParentFolder());

        QTestModule qTestModule = null;

        QTestIntegrationFolderManager.updateModule(credential, qTestProject.getId(), qTestParentModule, false);

        for (QTestModule siblingQTestModule : qTestParentModule.getChildModules()) {
            if (!folderEntity.getName().equalsIgnoreCase(siblingQTestModule.getName())) {
                continue;
            }

            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            // let user choose merge or not
            SynchronizedConfirmationDialog dialog = performFolderDuplicatedConfirmation(folderId, siblingQTestModule);
            sync.syncExec(dialog);

            if (dialog.getConfirmedValue() == YesNoAllOptions.YES) {
                qTestModule = siblingQTestModule;
            }
            break;
        }

        if (qTestModule == null) {
            qTestModule = createNewQTestModule(qTestProject, qTestParentModule, folderEntity);
        }

        folderEntity.getIntegratedEntities().add(
                QTestIntegrationFolderManager.getFolderIntegratedEntityByQTestModule(qTestModule));

        FolderController.getInstance().saveFolder(folderEntity);
        uploadedEntities.add(folderEntity);
    }

    private void uploadTestCase(TestCaseEntity testCaseEntity, IProgressMonitor monitor) throws Exception {
        QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity, projectEntity)
                .getQTestProject();
        String testCaseId = testCaseEntity.getIdForDisplay();
        monitor.subTask(MessageFormat.format(StringConstants.JOB_SUB_TASK_UPLOAD_TEST_CASE, testCaseId));

        QTestModule qTestParentModule = QTestIntegrationFolderManager.getQTestModuleByFolderEntity(testCaseEntity
                .getParentFolder());

        // Returns if this test case under qTest root module.
        if (qTestParentModule == null || qTestParentModule.getParentId() <= 0) {
            return;
        }

        QTestTestCase qTestTestCase = null;

        QTestIntegrationFolderManager.updateModule(credential, qTestProject.getId(), qTestParentModule, false);

        for (QTestTestCase siblingQTestCase : qTestParentModule.getChildTestCases()) {
            if (!testCaseEntity.getName().equalsIgnoreCase(siblingQTestCase.getName())) continue;

            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            // let user choose merge or not
            SynchronizedConfirmationDialog dialog = performTestCaseDuplicatedConfirmation(testCaseId, siblingQTestCase);
            sync.syncExec(dialog);

            if (dialog.getConfirmedValue() == YesNoAllOptions.YES) {
                qTestTestCase = siblingQTestCase;
            }
            break;
        }

        if (qTestTestCase == null) {
            qTestTestCase = createNewQTestCase(qTestProject, qTestParentModule, testCaseEntity);
        }

        if (qTestTestCase.getVersionId() <= 0) {
            qTestTestCase.setVersionId(QTestIntegrationTestCaseManager.getTestCaseVersionId(credential,
                    qTestProject.getId(), qTestTestCase.getId()));
        }

        testCaseEntity.getIntegratedEntities().add(
                QTestIntegrationTestCaseManager.getIntegratedEntityByQTestTestCase(qTestTestCase));

        TestCaseController.getInstance().updateTestCase(testCaseEntity);
        uploadedEntities.add(testCaseEntity);

        getEventBroker().post(EventConstants.TESTCASE_UPDATED,
                new Object[] { testCaseEntity.getId(), testCaseEntity });
        getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                TreeEntityUtil.getTestCaseTreeEntity(testCaseEntity, projectEntity));
    }

    private IEventBroker getEventBroker() {
        return EventBrokerSingleton.getInstance().getEventBroker();
    }

    private QTestTestCase createNewQTestCase(QTestProject qTestProject, QTestModule qTestParentModule,
            TestCaseEntity testCaseEntity) throws Exception {
        return QTestIntegrationTestCaseManager.addTestCase(qTestProject, qTestParentModule.getId(),
                testCaseEntity.getName(), testCaseEntity.getDescription(), credential);
    }

    private QTestModule createNewQTestModule(QTestProject qTestProject, QTestModule qTestParentModule,
            FolderEntity folderEntity) throws QTestUnauthorizedException, QTestInvalidFormatException {
        return QTestIntegrationFolderManager.createNewQTestTCFolder(credential, qTestProject.getId(),
                qTestParentModule.getId(), folderEntity.getName());
    }

    private void performErrorNotification(final Exception ex) {
        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.DIA_MSG_UNABLE_UPLOAD_TEST_CASE, ex
                        .getClass().getSimpleName());
            }
        });
    }

    private SynchronizedConfirmationDialog performTestCaseDuplicatedConfirmation(final String testCaseId,
            final QTestTestCase siblingQTestCase) {
        return new SynchronizedConfirmationDialog() {
            @Override
            public void run() {
                boolean confirmed = MessageDialog.open(
                        MessageDialog.QUESTION,
                        null,
                        StringConstants.DIA_TITLE_TEST_CASE_DUPLICATION,
                        MessageFormat.format(StringConstants.DIA_MSG_CONFIRM_MERGE_UPLOADED_TEST_CASE,
                                siblingQTestCase.getId(), testCaseId), SWT.NONE);
                setConfirmedValue(confirmed ? YesNoAllOptions.YES : YesNoAllOptions.NO);
            }
        };
    }

    private SynchronizedConfirmationDialog performFolderDuplicatedConfirmation(final String folderId,
            final QTestModule siblingQTestModule) {
        return new SynchronizedConfirmationDialog() {
            @Override
            public void run() {
                boolean confirmed = MessageDialog.open(MessageDialog.QUESTION, null,
                        StringConstants.DIA_TITLE_FOLDER_DUPLICATION, MessageFormat.format(
                                StringConstants.DIA_MSG_CONFIRM_MERGE_UPLOADED_TEST_CASE_FOLDER,
                                siblingQTestModule.getId(), folderId), SWT.NONE);
                setConfirmedValue(confirmed ? YesNoAllOptions.YES : YesNoAllOptions.NO);
            }
        };
    }
}
