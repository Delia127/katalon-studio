package com.kms.katalon.composer.testsuite.collection.part.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.part.launcher.IDETestSuiteCollectionLauncher;
import com.kms.katalon.composer.testsuite.collection.part.launcher.SubIDELauncher;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class TestSuiteCollectionBuilderJob extends Job {

    private TestSuiteCollectionEntity testSuiteCollectionEntity;

    public TestSuiteCollectionBuilderJob(TestSuiteCollectionEntity testSuiteCollectionEntity) {
        super(StringConstants.JOB_TITLE_TEST_SUITE_COLLECTION_BUILDER);
        this.testSuiteCollectionEntity = testSuiteCollectionEntity;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            int totalSize = testSuiteCollectionEntity.getTestSuiteRunConfigurations().size() + 1;
            monitor.beginTask(StringConstants.JOB_TASK_BUILDING_TEST_SUITE_COLLECTION, totalSize);
            TestSuiteCollectionExecutedEntity executedEntity = new TestSuiteCollectionExecutedEntity(
                    testSuiteCollectionEntity);
            List<SubIDELauncher> tsLaunchers = new ArrayList<>();
            for (TestSuiteRunConfiguration tsRunConfig : testSuiteCollectionEntity.getTestSuiteRunConfigurations()) {
                monitor.subTask(MessageFormat.format(StringConstants.JOB_TASK_BUILDING_LAUNCHER,
                        tsRunConfig.getTestSuiteEntity().getIdForDisplay()));
                monitor.worked(1);

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                if (!tsRunConfig.isRunEnabled()) {
                    continue;
                }

                SubIDELauncher subLauncher = buildLauncher(tsRunConfig);
                if (subLauncher == null) {
                    return Status.CANCEL_STATUS;
                }
                final TestSuiteExecutedEntity tsExecutedEntity = (TestSuiteExecutedEntity) subLauncher.getRunConfig()
                        .getExecutionSetting()
                        .getExecutedEntity();
                if (tsExecutedEntity.getTotalTestCases() == 0) {
                    openWarningDialogForEmptyTestSuite(tsExecutedEntity);
                    return Status.CANCEL_STATUS;
                }
                executedEntity.addTestSuiteExecutedEntity(tsExecutedEntity);
                tsLaunchers.add(subLauncher);
            }

            LauncherManager launcherManager = LauncherManager.getInstance();
            TestSuiteCollectionLauncher launcher = new IDETestSuiteCollectionLauncher(executedEntity, launcherManager,
                    tsLaunchers);
            launcherManager.addLauncher(launcher);
            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

    private void openWarningDialogForEmptyTestSuite(final TestSuiteExecutedEntity tsExecutedEntity) {
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openWarning(null, StringConstants.WARN,
                        MessageFormat.format(StringConstants.JOB_MSG_EMPTY_TEST_SUITE, tsExecutedEntity.getSourceId()));
            }
        });
    }

    private SubIDELauncher buildLauncher(final TestSuiteRunConfiguration tsRunConfig) {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            IRunConfiguration runConfig = RunConfigurationCollector.getInstance().getRunConfiguration(
                    tsRunConfig.getConfiguration().getRunConfigurationId(), projectDir);
            TestSuiteEntity testSuiteEntity = tsRunConfig.getTestSuiteEntity();
            runConfig.build(testSuiteEntity, new TestSuiteExecutedEntity(testSuiteEntity));
            return new SubIDELauncher(runConfig, LaunchMode.RUN);
        } catch (final Exception e) {
            UISynchronizeService.syncExec(new Runnable() {
                @Override
                public void run() {
                    MultiStatusErrorDialog.showErrorDialog(e, MessageFormat.format(
                            StringConstants.JOB_MSG_UNABLE_TO_EXECUTE_TEST_SUITE, tsRunConfig.getTestSuiteEntity()
                                    .getIdForDisplay()), e.getMessage());
                }
            });
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
