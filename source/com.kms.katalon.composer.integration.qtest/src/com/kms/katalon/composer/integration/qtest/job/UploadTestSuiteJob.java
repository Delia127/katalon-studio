package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.SynchronizedConfirmationDialog;
import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;
import com.kms.katalon.composer.components.impl.util.StatusUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteQTestSuitePair;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;

public class UploadTestSuiteJob extends QTestJob {
    // Represents collection of TestSuiteQTestSuitePair that each item is ready to be uploaded.
    private List<TestSuiteQTestSuitePair> fUnuploadedPairs;

    // Represents collection of uploaded TestSuiteQTestSuitePair, used for reverting data if user cancel the progress.
    private List<TestSuiteQTestSuitePair> fUploadedPairs;

    private IQTestCredential fCredentials;

    public UploadTestSuiteJob(List<TestSuiteQTestSuitePair> pairs) {
        super(StringConstants.JOB_TITLE_UPLOAD_TEST_SUITE);
        setUser(true);
        fUnuploadedPairs = pairs;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            fCredentials = QTestSettingCredential.getCredential(getProjectDir());
            fUploadedPairs = new ArrayList<TestSuiteQTestSuitePair>();

            int total = fUnuploadedPairs.size();
            monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_SUITE, total);
            for (TestSuiteQTestSuitePair pair : fUnuploadedPairs) {
                if (monitor.isCanceled()) {
                    return canceled();
                }
                IStatus status = uploadTestSuite(new SubProgressMonitor(monitor, 1,
                        SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK), pair);
                if (status == Status.CANCEL_STATUS) {
                    return canceled();
                } else if (status.getSeverity() == Status.ERROR) {
                    return status;
                }
            }
            return Status.OK_STATUS;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StatusUtil.getErrorStatus(getClass(), e);
        } finally {
            monitor.done();
        }
    }

    /**
     * Cancels the uploading progress. If there is any uploaded items, opens a confirmation to let user keep them or
     * not.
     * 
     * @return {@link Status#CANCEL_STATUS}
     */
    private IStatus canceled() {
        final int uploadedCount = fUploadedPairs.size();
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

        UISynchronizeService.getInstance().getSync().syncExec(dialog);
        if (dialog.getConfirmedValue() == YesNoAllOptions.NO) {
            List<TestSuiteEntity> testSuiteEntities = new ArrayList<TestSuiteEntity>();
            for (TestSuiteQTestSuitePair pair : fUnuploadedPairs) {
                testSuiteEntities.add(pair.getTestSuite());
            }

            DisintegrateTestSuiteJob job = new DisintegrateTestSuiteJob(testSuiteEntities);
            job.schedule();
        }
        return Status.CANCEL_STATUS;
    }

    private IStatus uploadTestSuite(IProgressMonitor monitor, TestSuiteQTestSuitePair pair) {
        TestSuiteEntity testSuite = pair.getTestSuite();
        List<QTestSuite> unUploadedQTestSuites = pair.getQTestSuites();
        List<QTestSuite> uploadedQTestSuites = new ArrayList<QTestSuite>();
        try {

            String testSuiteId = testSuite.getIdForDisplay();

            monitor.beginTask(MessageFormat.format(StringConstants.JOB_TASK_UPLOADING_TEST_SUITE_ENTITY,
                    getWrappedName(testSuiteId)), unUploadedQTestSuites.size() + 1);
            TestSuiteRepo repo = QTestIntegrationUtil.getTestSuiteRepo(testSuite, projectEntity);

            List<QTestSuite> allQTestSuites = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(testSuite));

            for (QTestSuite qTestSuite : allQTestSuites) {
                if (indexOf(qTestSuite, unUploadedQTestSuites) < 0) {
                    continue;
                }
                monitor.subTask(getWrappedName(MessageFormat.format(StringConstants.JOB_SUB_TASK_UPLOADING_QTEST_SUITE,
                        qTestSuite.getParent().getTypeName(), qTestSuite.getParent().getName())) + "...");
                QTestSuite newQTestSuite = null;

                final QTestSuite duplicatedQTestSuite = QTestIntegrationTestSuiteManager.getDuplicatedTestSuiteOnQTest(
                        fCredentials, testSuite.getName(), qTestSuite.getParent(), repo.getQTestProject());

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }

                // Duplication detected, let user choose merge or not.
                if (duplicatedQTestSuite != null) {
                    SynchronizedConfirmationDialog dialog = getMergeConfirmedDialog(duplicatedQTestSuite, testSuite);
                    UISynchronizeService.getInstance().getSync().syncExec(dialog);

                    if (dialog.getConfirmedValue() == YesNoAllOptions.YES) {
                        newQTestSuite = duplicatedQTestSuite;
                    }
                }

                if (newQTestSuite == null) {
                    newQTestSuite = QTestIntegrationTestSuiteManager.uploadTestSuite(fCredentials, testSuite.getName(),
                            testSuite.getDescription(), qTestSuite.getParent(), repo.getQTestProject());
                }

                qTestSuite.setId(newQTestSuite.getId());
                qTestSuite.setPid(newQTestSuite.getPid());
                uploadedQTestSuites.add(qTestSuite);

                monitor.worked(1);
            }

            // Save test suite and sent notification event
            monitor.subTask(StringConstants.JOB_SUB_TASK_UPDATING_TEST_SUITE_ENTITY);
            QTestIntegrationUtil.updateFileIntegratedEntity(testSuite,
                    QTestIntegrationTestSuiteManager.getIntegratedEntityByTestSuiteList(allQTestSuites));
            TestSuiteController.getInstance().updateTestSuite(testSuite);
            getEventBroker()
                    .post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
            
            getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                    TreeEntityUtil.getTestSuiteTreeEntity(testSuite, projectEntity));
            monitor.worked(1);

            return Status.OK_STATUS;
        } catch (OperationCanceledException ex) {
            return Status.CANCEL_STATUS;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return StatusUtil.getErrorStatus(getClass(), ex);
        } finally {
            if (uploadedQTestSuites.size() > 0) {
                fUploadedPairs.add(new TestSuiteQTestSuitePair(testSuite, uploadedQTestSuites));
            }
            monitor.done();
        }
    }

    private IEventBroker getEventBroker() {
        return EventBrokerSingleton.getInstance().getEventBroker();
    }

    private int indexOf(QTestSuite qTestSuite, List<QTestSuite> qTestSuiteCollection) {
        for (int index = 0; index < qTestSuiteCollection.size(); index++) {
            if (qTestSuite.getParent().getId() == qTestSuiteCollection.get(index).getParent().getId()) {
                return index;
            }
        }
        return -1;
    }

    private SynchronizedConfirmationDialog getMergeConfirmedDialog(final QTestSuite qTestSuite,
            final TestSuiteEntity testSuite) {
        return new SynchronizedConfirmationDialog() {
            @Override
            public void run() {
                boolean confirmed = MessageDialog.openQuestion(null, StringConstants.DIA_TITLE_TEST_SUITE_DUPLICATION,
                        MessageFormat.format(StringConstants.DIA_MSG_CONFIRM_MERGE_UPLOADED_TEST_SUITE, Long
                                .toString(qTestSuite.getId()), testSuite.getName(), qTestSuite.getParent()
                                .getTypeName(), qTestSuite.getParent().getName()));
                if (confirmed) {
                    setConfirmedValue(YesNoAllOptions.YES);
                } else {
                    setConfirmedValue(YesNoAllOptions.NO);
                }
            }
        };
    }

}
