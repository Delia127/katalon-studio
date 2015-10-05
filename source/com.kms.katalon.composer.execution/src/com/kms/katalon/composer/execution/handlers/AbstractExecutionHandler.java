package com.kms.katalon.composer.execution.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.addons.minmax.TrimStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.listener.TestSuiteExecutionJobCompletedListener;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.parts.TestSuiteCompositePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.launcher.IDELauncher;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.util.ExecutionUtil;

@SuppressWarnings("restriction")
public abstract class AbstractExecutionHandler {

    @Inject
    protected static EPartService partService;

    @Inject
    protected static EModelService modelService;

    @Inject
    protected static ECommandService commandService;

    @Inject
    protected static EHandlerService handlerService;

    @Inject
    protected static MApplication application;

    @Inject
    protected static IEventBroker eventBroker;

    @Inject
    protected static UISynchronize sync;

    protected static TestSuiteExecutionJobCompletedListener testSuiteCompletedListener;

    @CanExecute
    public boolean canExecute() {
        try {
            if (ProjectController.getInstance().getCurrentProject() != null) {
                MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                        application);
                if (composerStack == null) return false;

                if (composerStack.isVisible() && composerStack.getSelectedElement() != null) {
                    MPart part = (MPart) composerStack.getSelectedElement();
                    String partElementId = part.getElementId();
                    if (partElementId.startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
                            || partElementId.startsWith(IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX)) {
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute() {
        try {
            execute(LaunchMode.RUN);
        } catch (SWTException e) {
            // Ignore it
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to execute test script.");
            LoggerSingleton.logError(e);
        }
    }

    public Entity getExecutionTarget() {
        MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                application);
        MPart selectedPart = (MPart) composerStack.getSelectedElement();
        if (partService.saveAll(true) && partService.getDirtyParts().isEmpty()) {
            String partElementId = selectedPart.getElementId();
            // check the selected part is a test case or test suite part
            if (partElementId.startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
                    && selectedPart.getObject() instanceof TestCaseCompositePart) {
                return ((TestCaseCompositePart) selectedPart.getObject()).getTestCase();
            } else if (partElementId.startsWith(IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX)
                    && selectedPart.getObject() instanceof TestSuiteCompositePart) {
                TestSuiteCompositePart testSuiteComposite = (TestSuiteCompositePart) selectedPart.getObject();

                if (testSuiteComposite.getOriginalTestSuite().getTestSuiteTestCaseLinks().isEmpty()) {
                    if (MessageDialog
                            .openQuestion(null, StringConstants.INFORMATION_TITLE,
                                    "The test suite didn't have any test case to run. Do you want to add some test cases?")) {
                        testSuiteComposite.openAddTestCaseDialog();
                    }
                    return null;
                }

                return testSuiteComposite.getOriginalTestSuite();
            }
        }
        return null;
    }

    protected abstract IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception;

    protected abstract IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception;

    public void execute(LaunchMode launchMode) throws Exception {
        Entity targetEntity = getExecutionTarget();

        if (targetEntity == null) {
            return;
        }

        if (targetEntity instanceof TestCaseEntity) {
            TestCaseEntity testCase = (TestCaseEntity) targetEntity;
            IRunConfiguration runConfiguration = getRunConfigurationForExecution(testCase);
            if (runConfiguration == null) {
                return;
            }
            executeTestCase(testCase, launchMode, runConfiguration);
        } else if (targetEntity instanceof TestSuiteEntity) {
            TestSuiteEntity testSuite = (TestSuiteEntity) targetEntity;

            IRunConfiguration runConfiguration = getRunConfigurationForExecution(testSuite);
            if (runConfiguration == null) {
                return;
            }
            executeTestSuite(testSuite, launchMode, runConfiguration, 0);
        }
    }

    protected static void executeTestCase(final TestCaseEntity testCase, final LaunchMode launchMode,
            final IRunConfiguration runConfig) throws Exception {
        if (testCase != null) {
            Job job = new Job("Launching test case...") {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    try {
                        monitor.beginTask("Launching test case...", 3);

                        monitor.subTask("Activating viewers...");
                        openConsoleLog();
                        monitor.worked(1);

                        monitor.subTask("Building scripts...");
                        final IDELauncher launcher = new IDELauncher(eventBroker, LoggerSingleton.getInstance()
                                .getLogger(), launchMode, runConfig);
                        monitor.worked(1);

                        monitor.subTask("Launching test case...");
                        launcher.setTotalTestCase(1);

                        sync.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    launcher.launch(testCase);
                                } catch (Exception e) {
                                    monitor.setCanceled(true);
                                }
                            }
                        });
                        monitor.worked(1);

                        monitor.done();
                        return Status.OK_STATUS;
                    } catch (final Exception e) {
                        sync.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MultiStatusErrorDialog.showErrorDialog(e,
                                        "Unable to execute the current selected test case.", "Wrong syntax");
                            }
                        });
                        return Status.CANCEL_STATUS;
                    }
                }
            };
            job.setUser(true);
            job.schedule();
        }
    }

    // protected void executeTestSuite(final TestSuiteEntity testSuite, final
    // BrowserType browser,
    // final int pageLoadTimeout, final LaunchMode launchMode) throws Exception
    // {
    public static void executeTestSuite(final TestSuiteEntity testSuite, final LaunchMode launchMode,
            final IRunConfiguration runConfig, final int reRunTime) throws Exception {
        if (testSuite == null) {
            return;
        }

        Job job = new Job("Launching test suite...") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Launching test suite...", 4);
                    monitor.subTask("Validating test suite...");
                    ProjectEntity project = testSuite.getProject();
                    final TestSuiteExecutedEntity testSuiteExecutedEntity = ExecutionUtil.loadTestDataForTestSuite(
                            testSuite, project);
                    final int totalTestCases = testSuiteExecutedEntity.getTotalTestCases();
                    if (totalTestCases > 0) {
                        monitor.subTask("Activating viewers...");
                        openConsoleLog();
                        monitor.worked(1);

                        monitor.subTask("Building scripts...");
                        final IDELauncher launcher = new IDELauncher(eventBroker, LoggerSingleton.getInstance()
                                .getLogger(), launchMode, runConfig);
                        monitor.worked(1);

                        monitor.subTask("Launching test suite...");
                        launcher.setTotalTestCase(totalTestCases);

                        sync.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    launcher.launch(testSuite, testSuiteExecutedEntity, reRunTime);
                                } catch (Exception e) {
                                    monitor.setCanceled(true);
                                    MultiStatusErrorDialog.showErrorDialog(e,
                                            "Unable to execute the current selected test suite.",
                                            "Test suite is not valid.");
                                }
                            }
                        });
                        monitor.worked(1);

                        monitor.done();
                        return Status.OK_STATUS;
                    } else {
                        sync.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog
                                        .openWarning(
                                                Display.getCurrent().getActiveShell(),
                                                StringConstants.WARN_TITLE,
                                                "There is no test case selected.\n"
                                                        + "Please select test cases you want to execute by checking their checkboxes at 'Run' column.");
                            }
                        });
                        return Status.CANCEL_STATUS;
                    }

                } catch (final Exception e) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {

                            MultiStatusErrorDialog.showErrorDialog(e,
                                    "Unable to execute the current selected test suite.", "Test suite is not valid.");
                        }
                    });

                    return Status.CANCEL_STATUS;
                }
            }
        };
        job.setUser(true);
        job.schedule();

        if (testSuiteCompletedListener == null) {
            testSuiteCompletedListener = new TestSuiteExecutionJobCompletedListener();
            eventBroker.subscribe(EventConstants.JOB_COMPLETED, testSuiteCompletedListener);
        }
    }

    /**
     * Open LogViewerPart and its partStack
     * 
     * @param numTestCasesRun
     *            : number of test cases will be executed
     */
    protected static void openConsoleLog() {
        // turn off pop-up messages on system console when execute the script
        IPreferenceStore debugUIPreferences = DebugUIPlugin.getDefault().getPreferenceStore();
        debugUIPreferences.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR, false);
        debugUIPreferences.setValue(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT, false);
        debugUIPreferences.setValue(IDebugUIConstants.PREF_AUTO_REMOVE_OLD_LAUNCHES, false);

        sync.syncExec(new Runnable() {
            @Override
            public void run() {
                // set console partStack is visible
                List<MPerspectiveStack> psList = modelService.findElements(application, null, MPerspectiveStack.class,
                        null);

                MPartStack consolePartStack = (MPartStack) modelService.find(IdConstants.CONSOLE_PARTSTACK_ID, psList
                        .get(0).getSelectedElement());

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
}
