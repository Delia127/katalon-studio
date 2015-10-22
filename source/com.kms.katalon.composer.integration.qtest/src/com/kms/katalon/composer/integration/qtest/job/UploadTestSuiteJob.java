package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class UploadTestSuiteJob extends UploadJob {

    private TestSuiteEntity fTestSuiteEntity;

    public UploadTestSuiteJob(TestSuiteEntity testSuiteEntity) {
        super(StringConstants.JOB_TITLE_UPLOAD_TEST_SUITE);
        setUser(true);
        fTestSuiteEntity = testSuiteEntity;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            List<QTestSuite> unuploadedQTestSuites = QTestIntegrationUtil.getUnuploadedQTestSuites(fTestSuiteEntity);
            String testSuiteId = TestSuiteController.getInstance().getIdForDisplay(fTestSuiteEntity);

            monitor.beginTask(MessageFormat.format(StringConstants.JOB_TASK_UPLOADING_TEST_SUITE_ENTITY,
                    getWrappedName(testSuiteId)), unuploadedQTestSuites.size() + 1);
            TestSuiteRepo repo = QTestIntegrationUtil.getTestSuiteRepo(fTestSuiteEntity, projectEntity);

            List<QTestSuite> allQTestSuites = QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(fTestSuiteEntity));

            for (QTestSuite qTestSuite : allQTestSuites) {
                monitor.subTask(getWrappedName(MessageFormat.format(StringConstants.JOB_SUB_TASK_UPLOADING_QTEST_SUITE,
                        qTestSuite.getParent().getTypeName(), qTestSuite.getParent().getName())) + "...");
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                QTestSuite newQTestSuite = QTestIntegrationTestSuiteManager.addTestSuite(getProjectDir(),
                        fTestSuiteEntity.getName(), qTestSuite.getParent(), repo.getQTestProject());

                qTestSuite.setId(newQTestSuite.getId());
                qTestSuite.setPid(newQTestSuite.getPid());
                monitor.worked(1);
            }

            // Save test suite and sent notification event
            monitor.subTask(StringConstants.JOB_SUB_TASK_UPDATING_TEST_SUITE_ENTITY);
            QTestIntegrationUtil.updateFileIntegratedEntity(fTestSuiteEntity,
                    QTestIntegrationTestSuiteManager.getIntegratedEntityByTestSuiteList(allQTestSuites));
            TestSuiteController.getInstance().updateTestSuite(fTestSuiteEntity);
            EventBrokerSingleton
                    .getInstance()
                    .getEventBroker()
                    .post(EventConstants.TEST_SUITE_UPDATED,
                            new Object[] { fTestSuiteEntity.getId(), fTestSuiteEntity });
            monitor.worked(1);

            return Status.OK_STATUS;
        } catch (QTestInvalidFormatException e) {
            LoggerSingleton.logError(e);
            return Status.CANCEL_STATUS;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
        }

    }

    private String getProjectDir() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        return projectEntity.getFolderLocation();
    }

}
