package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.SynchronizedConfirmationDialog;
import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;
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

public class UploadTestSuiteJob extends UploadJob {
    private List<TestSuiteQTestSuitePair> fPairs;
    private IQTestCredential fCredentials;

    public UploadTestSuiteJob(List<TestSuiteQTestSuitePair> pairs) {
        super(StringConstants.JOB_TITLE_UPLOAD_TEST_SUITE);
        setUser(true);
        fPairs = pairs;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            fCredentials = new QTestSettingCredential(getProjectDir());
            int total = fPairs.size();
            monitor.beginTask(StringConstants.JOB_TASK_UPLOAD_TEST_SUITE, total);
            for (TestSuiteQTestSuitePair pair : fPairs) {
                uploadTestSuite(new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK), pair);
            }
            return Status.OK_STATUS;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }
    }

    private IStatus uploadTestSuite(IProgressMonitor monitor, TestSuiteQTestSuitePair pair) {
        try {
            TestSuiteEntity testSuite = pair.getTestSuite();
            List<QTestSuite> unUploadedQTestSuites = pair.getQTestSuites();
            String testSuiteId = TestSuiteController.getInstance().getIdForDisplay(testSuite);

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
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                QTestSuite newQTestSuite = null;

                final QTestSuite duplicatedQTestSuite = QTestIntegrationTestSuiteManager.getDuplicatedTestSuiteOnQTest(
                        fCredentials, testSuite.getName(), qTestSuite.getParent(), repo.getQTestProject());

                if (duplicatedQTestSuite != null) {
                    SynchronizedConfirmationDialog dialog = getConfirmedDialog(duplicatedQTestSuite, testSuite);
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
                monitor.worked(1);
            }

            // Save test suite and sent notification event
            monitor.subTask(StringConstants.JOB_SUB_TASK_UPDATING_TEST_SUITE_ENTITY);
            QTestIntegrationUtil.updateFileIntegratedEntity(testSuite,
                    QTestIntegrationTestSuiteManager.getIntegratedEntityByTestSuiteList(allQTestSuites));
            TestSuiteController.getInstance().updateTestSuite(testSuite);
            EventBrokerSingleton.getInstance().getEventBroker()
                    .post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });
            monitor.worked(1);
            
            return Status.OK_STATUS;
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }
    }

    private int indexOf(QTestSuite qTestSuite, List<QTestSuite> qTestSuiteCollection) {
        for (int index = 0; index < qTestSuiteCollection.size(); index++) {
            if (qTestSuite.getParent().getId() == qTestSuiteCollection.get(index).getParent().getId()) {
                return index;
            }
        }
        return -1;
    }

    private SynchronizedConfirmationDialog getConfirmedDialog(final QTestSuite qTestSuite,
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
