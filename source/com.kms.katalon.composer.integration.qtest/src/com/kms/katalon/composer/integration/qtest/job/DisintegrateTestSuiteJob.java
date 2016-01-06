package com.kms.katalon.composer.integration.qtest.job;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.StatusUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;

public class DisintegrateTestSuiteJob extends QTestJob {

    private List<TestSuiteEntity> fTestSuites;

    public DisintegrateTestSuiteJob(List<TestSuiteEntity> testSuites) {
        super(StringConstants.JOB_TITLE_DISINTEGRATE_TEST_SUITE);
        fTestSuites = testSuites;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            monitor.beginTask(StringConstants.JOB_TASK_DISINTEGRATE_TEST_SUITE, fTestSuites.size());
            for (TestSuiteEntity testSuite : fTestSuites) {
                String testSuiteId = null;
                try {
                    testSuiteId = testSuite.getIdForDisplay();
                    List<QTestSuite> qTestSuites = QTestIntegrationTestSuiteManager
                            .getQTestSuiteListByIntegratedEntity(QTestIntegrationUtil.getIntegratedEntity(testSuite));

                    for (QTestSuite availableQTestSuite : qTestSuites) {
                        if (availableQTestSuite.getId() > 0) {
                            availableQTestSuite.setId(0);
                            availableQTestSuite.setPid("");
                            availableQTestSuite.setSelected(false);
                            availableQTestSuite.getTestRuns().clear();
                        }
                    }

                    QTestIntegrationUtil.updateFileIntegratedEntity(testSuite,
                            QTestIntegrationTestSuiteManager.getIntegratedEntityByTestSuiteList(qTestSuites));
                    TestSuiteController.getInstance().updateTestSuite(testSuite);
                    EventBrokerSingleton.getInstance().getEventBroker()
                            .post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), testSuite });

                } catch (QTestInvalidFormatException e) {
                    MessageDialog.openError(null, StringConstants.WARN,
                            MessageFormat.format(StringConstants.JOB_MSG_TEST_SUITE_INVALID_FORMAT, testSuiteId));
                    return StatusUtil.getErrorStatus(getClass(), e);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    return StatusUtil.getErrorStatus(getClass(), e);
                }

            }
            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

}
