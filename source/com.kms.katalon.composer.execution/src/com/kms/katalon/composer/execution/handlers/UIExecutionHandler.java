package com.kms.katalon.composer.execution.handlers;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.addons.minmax.TrimStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.FrameworkUtil;

import com.katalon.platform.api.exception.PlatformException;
import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.exceptions.JobCancelException;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExtensionRequiredException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.tracking.service.Trackings;

public class UIExecutionHandler {
    
    public void launch(ILauncher launcher) {
        Job job = new Job("Launching test execution") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Launching test execution...", 5);
    
                    monitor.subTask(StringConstants.HAND_JOB_ACTIVATING_VIEWERS);
                    openConsoleLog();
                    validateJobProgressMonitor(monitor);
                    monitor.worked(1);

                    monitor.subTask("Scheduling launcher...");
                    LauncherManager launcherManager = LauncherManager.getInstance();
                    launcherManager.addLauncher(launcher);
                    
                    monitor.worked(1);
                    
                    monitor.done();
                    
                    return Status.OK_STATUS;
                } catch (JobCancelException e) {
                    return Status.CANCEL_STATUS;
                } catch (final Exception e) {
                    return new Status(Status.WARNING,
                            FrameworkUtil.getBundle(AbstractExecutionHandler.class).getSymbolicName(), e.getMessage(),
                            e);
                } finally {
                    monitor.done();
                }
            }
            
        };
        job.setUser(true);
        job.schedule();

        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                super.done(event);
                if (event.getResult() != null && event.getResult().matches(Status.WARNING)) {
                    UISynchronizeService.asyncExec(() -> {
                        if (event.getResult().getException() == null) {
                            MessageDialog.openInformation(null, StringConstants.WARN_TITLE,
                                    event.getResult().getMessage());
                        } else {
                            MultiStatusErrorDialog.showErrorDialog(
                                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_SUITE,
                                    event.getResult().getMessage(),
                                    ExceptionsUtil.getStackTraceForThrowable(event.getResult().getException()));
                        }
                    });
                }

                job.removeJobChangeListener(this);
            }
        });
    }

    public void executeTestSuite(final TestSuiteEntity testSuite, final LaunchMode launchMode,
            final IRunConfiguration runConfig) throws Exception {
        if (testSuite == null) {
            return;
        }

        Job job = new Job(StringConstants.HAND_JOB_LAUNCHING_TEST_SUITE) {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    monitor.beginTask(StringConstants.HAND_JOB_LAUNCHING_TEST_SUITE, 5);
                    monitor.subTask(StringConstants.HAND_JOB_VALIDATING_TEST_SUITE);
                    // back-up

                    final TestSuiteExecutedEntity testSuiteExecutedEntity = new TestSuiteExecutedEntity(testSuite);
                    monitor.subTask("Preparing test cases...");
                    testSuiteExecutedEntity.prepareTestCases();

                    final int totalTestCases = testSuiteExecutedEntity.getTotalTestCases();
                    if (totalTestCases > 0) {
                        monitor.subTask(StringConstants.HAND_JOB_ACTIVATING_VIEWERS);
                        openConsoleLog();
                        validateJobProgressMonitor(monitor);
                        monitor.worked(1);

                        monitor.subTask(StringConstants.HAND_JOB_BUILDING_SCRIPTS);
                        runConfig.build(testSuite, testSuiteExecutedEntity);
                        validateJobProgressMonitor(monitor);
                        monitor.worked(1);

                        monitor.subTask(StringConstants.HAND_JOB_LAUNCHING_TEST_SUITE);
                        LauncherManager launcherManager = LauncherManager.getInstance();
                        ILauncher launcher = new IDELauncher(launcherManager, runConfig, launchMode);
                        launcherManager.addLauncher(launcher);

                        // trackEmailAfterExecution(testSuiteExecutedEntity.getEmailSettings().getEmailConfig().isSendEmailTestFailedOnly());

                        monitor.worked(1);

                        monitor.done();
                        return Status.OK_STATUS;
                    } else {
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
                                        StringConstants.WARN_TITLE,
                                        StringConstants.HAND_WARN_MSG_NO_TEST_CASE_SELECTED);
                            }
                        });
                        return Status.CANCEL_STATUS;
                    }

                } catch (JobCancelException e) {
                    return Status.CANCEL_STATUS;
                } catch (ExtensionRequiredException e) {
                    return new Status(Status.WARNING,
                            FrameworkUtil.getBundle(AbstractExecutionHandler.class).getSymbolicName(),
                            e.getMessage());
                } catch (PlatformException e) {
                    return new Status(Status.WARNING,
                            FrameworkUtil.getBundle(AbstractExecutionHandler.class).getSymbolicName(),
                            e.getDetailMessage(), e);
                } catch (final Exception e) {
                    return new Status(Status.WARNING,
                            FrameworkUtil.getBundle(AbstractExecutionHandler.class).getSymbolicName(), e.getMessage(),
                            e);
                } finally {
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.schedule();

        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                super.done(event);
                if (event.getResult() != null && event.getResult().matches(Status.WARNING)) {
                    UISynchronizeService.asyncExec(() -> {
                        if (event.getResult().getException() == null) {
                            MessageDialog.openInformation(null, StringConstants.WARN_TITLE,
                                    event.getResult().getMessage());
                        } else {
                            MultiStatusErrorDialog.showErrorDialog(
                                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_SUITE,
                                    event.getResult().getMessage(),
                                    ExceptionsUtil.getStackTraceForThrowable(event.getResult().getException()));
                        }
                    });
                }

                job.removeJobChangeListener(this);
            }
        });
    }

    /**
     * Open LogViewerPart and its partStack
     * 
     * @param numTestCasesRun number of test cases will be executed
     */
    public static void openConsoleLog() {
        // turn off pop-up messages on system console when execute the script
        IPreferenceStore debugUIPreferences = DebugUIPlugin.getDefault().getPreferenceStore();
        debugUIPreferences.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
        debugUIPreferences.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        debugUIPreferences.setValue(IDebugUIConstants.PREF_AUTO_REMOVE_OLD_LAUNCHES, false);

        UISynchronizeService.asyncExec(new Runnable() {
            @Override
            public void run() {
                EModelService modelService = ModelServiceSingleton.getInstance().getModelService();
                MApplication application = ApplicationSingleton.getInstance().getApplication();
                
                // set console partStack is visible
                List<MPerspectiveStack> psList = modelService.findElements(application, null, MPerspectiveStack.class,
                        null);

                MPartStack consolePartStack = (MPartStack) modelService.find(IdConstants.CONSOLE_PART_STACK_ID,
                        psList.get(0).getSelectedElement());

                // set console partStack visible
                consolePartStack.getTags().remove("Minimized");
                consolePartStack.setVisible(true);
                if (!consolePartStack.isToBeRendered()) {
                    consolePartStack.setToBeRendered(true);
                }

                // set current page of console partStack is log viewer
                MPart consoleLogPart = (MPart) modelService.find(IdConstants.IDE_CONSOLE_LOG_PART_ID, consolePartStack);
                if (consoleLogPart != null && consolePartStack.getSelectedElement() != consoleLogPart) {
                    consolePartStack.setSelectedElement(consoleLogPart);
                }

                // set outline partStack is visible
                MPartStack rightPartStack = (MPartStack) modelService.find(IdConstants.OUTLINE_PARTSTACK_ID,
                        application);

                // set current page of outline partStack is job viewer
                MPart jobViewerPart = (MPart) modelService.find(IdConstants.JOBVIEWER_PART_ID, rightPartStack);
                if (rightPartStack.getSelectedElement() != jobViewerPart) {
                    rightPartStack.setSelectedElement(jobViewerPart);
                }

                ToolControlImpl toolControl = (ToolControlImpl) modelService.find(IdConstants.OUTLINE_TRIMSTACK_ID,
                        application);
                if (toolControl.getObject() != null) {
                    TrimStack trimStack = (TrimStack) toolControl.getObject();
                    trimStack.showStack(true);
                }
            }
        });
    }

    public UISynchronize getSync() {
        return UISynchronizeService.getInstance().getSync();
    }

    void validateJobProgressMonitor(IProgressMonitor monitor) throws JobCancelException {
        if (monitor.isCanceled()) {
            throw new JobCancelException();
        }
    }
}
