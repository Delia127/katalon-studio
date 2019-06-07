package com.kms.katalon.composer.testsuite.collection.part.job;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.MissingMobileDriverWarningDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.launcher.IDETestSuiteCollectionLauncher;
import com.kms.katalon.composer.execution.launcher.SubIDELauncher;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportItemDescription;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.launcher.ReportableLauncher;
import com.kms.katalon.execution.launcher.TestSuiteCollectionLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;
import com.kms.katalon.tracking.service.Trackings;

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

            ProjectEntity project = testSuiteCollectionEntity.getProject();
            ReportController reportController = ReportController.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String executionSessionId =  dateFormat.format(new Date());
            ReportCollectionEntity reportCollection = reportController.newReportCollection(project,
                    testSuiteCollectionEntity, executionSessionId, executedEntity.getId());

            List<ReportableLauncher> tsLaunchers = new ArrayList<>();
            boolean cancelInstallWebDriver = false;
            
            
            for (TestSuiteRunConfiguration tsRunConfig : testSuiteCollectionEntity.getTestSuiteRunConfigurations()) {
                if (!cancelInstallWebDriver) {
                    cancelInstallWebDriver = !checkInstallWebDriver(tsRunConfig);
                }
                monitor.subTask(MessageFormat.format(StringConstants.JOB_TASK_BUILDING_LAUNCHER,
                        tsRunConfig.getTestSuiteEntity().getIdForDisplay()));
                monitor.worked(1);

                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }

                if (!tsRunConfig.isRunEnabled()) {
                    continue;
                }

                SubIDELauncher subLauncher = buildLauncher(tsRunConfig, reportCollection, executionSessionId);
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
                    tsLaunchers, testSuiteCollectionEntity.getExecutionMode(), reportCollection);
            launcherManager.addLauncher(launcher);
            
            reportController.updateReportCollection(reportCollection);
            return Status.OK_STATUS;
        } catch (DALException e) {
            LoggerSingleton.logError(e);
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
//            UsageInfoCollector
//                    .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.RUN_SCRIPT, RunningMode.GUI));
        }
    }

    private boolean checkInstallWebDriver(TestSuiteRunConfiguration tsRunConfig) {
        return true;
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

    private SubIDELauncher buildLauncher(final TestSuiteRunConfiguration tsRunConfig,
            ReportCollectionEntity reportCollection, String executionSessionId) {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        try {
            RunConfigurationDescription configuration = tsRunConfig.getConfiguration();
            IRunConfiguration runConfig = RunConfigurationCollector.getInstance()
                    .getRunConfiguration(configuration.getRunConfigurationId(), projectDir, configuration);
            TestSuiteEntity testSuiteEntity = tsRunConfig.getTestSuiteEntity();
            TestSuiteExecutedEntity executedEntity = new TestSuiteExecutedEntity(testSuiteEntity);
            executedEntity.prepareTestCases();
            runConfig.setExecutionSessionId(executionSessionId);
            runConfig.build(testSuiteEntity, executedEntity);
            SubIDELauncher launcher = new SubIDELauncher(runConfig, LaunchMode.RUN, configuration);
            reportCollection.getReportItemDescriptions()
                    .add(ReportItemDescription.from(launcher.getReportEntity().getIdForDisplay(), configuration));
            return launcher;
        } catch (final MobileSetupException e) {
            UISynchronizeService.syncExec(() -> MissingMobileDriverWarningDialog
                    .showWarning(Display.getCurrent().getActiveShell(), e.getMessage()));
            return null;
        }
        catch (final Exception e) {
            UISynchronizeService.syncExec(new Runnable() {
                @Override
                public void run() {
                    MultiStatusErrorDialog.showErrorDialog(e,
                            MessageFormat.format(StringConstants.JOB_MSG_UNABLE_TO_EXECUTE_TEST_SUITE,
                                    tsRunConfig.getTestSuiteEntity().getIdForDisplay()),
                            e.getMessage());
                }
            });
            LoggerSingleton.logError(e);
            return null;
        }
    }
}
