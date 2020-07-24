package com.kms.katalon.composer.execution.jobs;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.application.helper.UserProfileHelper;
import com.kms.katalon.application.userprofile.UserProfile;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.ExecuteFirstTestSuccessfullyDialog;
import com.kms.katalon.composer.execution.dialog.ExecuteFirstTestUnsuccessfullyDialog;
import com.kms.katalon.composer.execution.exceptions.JobCancelException;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.entity.project.QuickStartProjectType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;

public class ExecuteTestCaseJob extends Job {
    protected final UISynchronize sync;

    protected IRunConfiguration runConfig;

    protected final TestCaseEntity testCase;

    protected final LaunchMode launchMode;

    private AbstractExecutionHandler handler;

    protected boolean isCanceled;

    public ExecuteTestCaseJob(String name, TestCaseEntity testCase, LaunchMode launchMode,
            UISynchronize sync, AbstractExecutionHandler handler) {
        super(name);
        this.testCase = testCase;
        this.launchMode = launchMode;
        this.sync = sync;
        this.handler = handler;
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
            if (isCanceled) {
                return Status.CANCEL_STATUS;
            }
            validateJobProgressMonitor(monitor);

            startLauncher();

            //Trackings.trackExecuteTestCase(launchMode.toString(), runConfig.getName());
            
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
            showFirstExecuteGuidingDialog();
//            UsageInfoCollector
//                    .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.RUN_SCRIPT, RunningMode.GUI));
        }
    }
    
    private void showFirstExecuteGuidingDialog() {
        UserProfile currentProfile1 = UserProfileHelper.getCurrentProfile();
        boolean isPreferredWebUITesting = currentProfile1.getPreferredTestingType() == QuickStartProjectType.WEBUI;
        boolean hasDoneFirstRunPassAndFail = currentProfile1.isDoneRunFirstTestCasePass() && currentProfile1.isDoneRunFirstTestCaseFail();
        if (!currentProfile1.isNewUser() || !isPreferredWebUITesting || hasDoneFirstRunPassAndFail) {
            return;
        }

        List<ILauncher> lauchers = LauncherManager.getInstance().getRunningLaunchers();
        ILauncher firstExecution = lauchers.get(0);
        if (firstExecution != null) {
            Thread waitForExecuteThread = new Thread(() -> {
                while (firstExecution.getStatus() != LauncherStatus.DONE
                        && firstExecution.getStatus() != LauncherStatus.TERMINATED) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (firstExecution.getStatus() == LauncherStatus.DONE) {
                    UserProfile currentProfile = UserProfileHelper.getCurrentProfile();
                    currentProfile.setDoneRunFirstTestCase(true);
                    UserProfileHelper.saveProfile(currentProfile);

                    UISynchronizeService.syncExec(() -> {
                        ILauncherResult result = firstExecution.getResult();
                        if (result.getReturnCode() == 0) { // Success
                            if (currentProfile.isDoneRunFirstTestCasePass()) {
                                return;
                            }

                            currentProfile.setDoneRunFirstTestCasePass(true);
                            UserProfileHelper.saveProfile(currentProfile);

                            ExecuteFirstTestSuccessfullyDialog congratulationDialog = new ExecuteFirstTestSuccessfullyDialog(
                                    Display.getCurrent().getActiveShell());
                            congratulationDialog.open();
                        } else {
                            if (currentProfile.isDoneRunFirstTestCaseFail()) {
                                return;
                            }

                            currentProfile.setDoneRunFirstTestCaseFail(true);
                            UserProfileHelper.saveProfile(currentProfile);

                            ExecuteFirstTestUnsuccessfullyDialog troubleshotDialog = new ExecuteFirstTestUnsuccessfullyDialog(
                                    Display.getCurrent().getActiveShell());
                            troubleshotDialog.open();
                        }
                    });
                }
            });
            waitForExecuteThread.start();
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

    protected void buildScripts() {
        UISynchronizeService.syncExec(() -> {
            try {
                runConfig = handler.buildRunConfiguration(testCase.getProject().getFolderLocation());
                if (runConfig == null) {
                    isCanceled = true;
                    return;
                }
                runConfig.build(testCase, new TestCaseExecutedEntity(testCase));
            } catch (IOException | ExecutionException | InterruptedException e) {
                LoggerSingleton.logError(e);
                isCanceled = true;
            }
        });
    }

    private static void validateJobProgressMonitor(IProgressMonitor monitor) throws JobCancelException {
        if (monitor.isCanceled()) {
            throw new JobCancelException();
        }
    }
}
