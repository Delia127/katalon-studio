package com.kms.katalon.composer.execution.jobs;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.exceptions.JobCancelException;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.tracking.service.Trackings;

public class ExecuteTestCaseJob extends Job {
    protected final UISynchronize sync;

    protected final IRunConfiguration runConfig;

    protected final TestCaseEntity testCase;

    protected final LaunchMode launchMode;

    public ExecuteTestCaseJob(String name, IRunConfiguration runConfig, TestCaseEntity testCase, LaunchMode launchMode,
            UISynchronize sync) {
        super(name);
        this.runConfig = runConfig;
        this.testCase = testCase;
        this.launchMode = launchMode;
        this.sync = sync;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        try {
            monitor.beginTask(StringConstants.HAND_JOB_LAUNCHING_TEST_CASE, 3);

            monitor.subTask(StringConstants.HAND_JOB_ACTIVATING_VIEWERS);
            activateViewers();
            validateJobProgressMonitor(monitor);
            monitor.worked(1);

            monitor.subTask(StringConstants.HAND_JOB_BUILDING_SCRIPTS);

            buildScripts();
            validateJobProgressMonitor(monitor);

            startLauncher();
            
            Trackings.trackExecuteTestCase(launchMode.toString(), runConfig.getName());
            
            monitor.worked(1);

            return Status.OK_STATUS;
        } catch (JobCancelException e) {
            return Status.CANCEL_STATUS;
        } catch (final Exception e) {
            sync.syncExec(new Runnable() {
                @Override
                public void run() {
                    MultiStatusErrorDialog.showErrorDialog(e,
                            StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_CASE,
                            StringConstants.HAND_ERROR_MSG_REASON_WRONG_SYNTAX);
                }
            });
            return Status.CANCEL_STATUS;
        } finally {
            monitor.done();
//            UsageInfoCollector
//                    .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.RUN_SCRIPT, RunningMode.GUI));
        }
    }
    
    protected void startLauncher() {
        LauncherManager launcherManager = LauncherManager.getInstance();
        ILauncher launcher = new IDELauncher(launcherManager, runConfig, launchMode);
        launcherManager.addLauncher(launcher);
    }

    protected void activateViewers() {
        AbstractExecutionHandler.openConsoleLog();
    }

    protected void buildScripts() throws IOException, ExecutionException {
        runConfig.build(testCase, new TestCaseExecutedEntity(testCase));
    }

    private static void validateJobProgressMonitor(IProgressMonitor monitor) throws JobCancelException {
        if (monitor.isCanceled()) {
            throw new JobCancelException();
        }
    }
}
