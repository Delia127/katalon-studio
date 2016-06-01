package com.kms.katalon.composer.testsuite.collection.part.job;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
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
        super("Building Test Run");
        this.testSuiteCollectionEntity = testSuiteCollectionEntity;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            int totalSize = testSuiteCollectionEntity.getTestSuiteRunConfigurations().size() + 1;
            monitor.beginTask("Building test run...", totalSize);
            TestSuiteCollectionExecutedEntity executedEntity = new TestSuiteCollectionExecutedEntity(
                    testSuiteCollectionEntity);
            List<SubIDELauncher> tsLaunchers = new ArrayList<>();
            for (TestSuiteRunConfiguration tsRunConfig : testSuiteCollectionEntity.getTestSuiteRunConfigurations()) {
                monitor.subTask("Building launcher for " + tsRunConfig.getTestSuiteEntity().getIdForDisplay());
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
                executedEntity.addTestSuiteExecutedEntity((TestSuiteExecutedEntity) subLauncher.getRunConfig()
                        .getExecutionSetting()
                        .getExecutedEntity());
                tsLaunchers.add(subLauncher);
            }

            LauncherManager launcherManager = LauncherManager.getInstance();
            TestSuiteCollectionLauncher launcher = new IDETestSuiteCollectionLauncher(executedEntity,
                    launcherManager, tsLaunchers);
            launcherManager.addLauncher(launcher);
            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }
    }

    private SubIDELauncher buildLauncher(TestSuiteRunConfiguration tsRunConfig) {
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
                    MultiStatusErrorDialog.showErrorDialog(e, "Unable to execute test suite", e.getMessage());
                }
            });
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
