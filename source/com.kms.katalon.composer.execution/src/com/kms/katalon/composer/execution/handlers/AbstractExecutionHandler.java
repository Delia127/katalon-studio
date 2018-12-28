package com.kms.katalon.composer.execution.handlers;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.ExecutionProfileManager;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.exceptions.JobCancelException;
import com.kms.katalon.composer.execution.jobs.ExecuteTestCaseJob;
import com.kms.katalon.composer.execution.launcher.IDELaunchShorcut;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testsuite.parts.TestSuiteCompositePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.FeatureFileExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.tracking.service.Trackings;




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

    /**
     * Cleans all run configuration before any execution is started
     */
    @Inject
    public void start() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                try {
                    IDELaunchShorcut.cleanAllConfigurations();
                } catch (CoreException e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        try {
            if (ProjectController.getInstance().getCurrentProject() != null) {
                MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                        application);
                if (composerStack == null)
                    return false;

                if (composerStack.isVisible() && composerStack.getSelectedElement() != null) {
                    MPart part = (MPart) composerStack.getSelectedElement();
                    String partElementId = part.getElementId();
                    if (partElementId.startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
                            || partElementId.startsWith(IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX)) {
                        return true;
                    }
                    if (partElementId.startsWith(IdConstants.COMPABILITY_EDITOR_ID)) {
                        CompatibilityEditor editor = (CompatibilityEditor) part.getObject();
                        if (IdConstants.CUCUMBER_EDITOR_ID.equals(editor.getReference().getId())) {
                            return true;
                        }
                    }
                }
                return false;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    protected LaunchMode getLaunchMode(ParameterizedCommand command) {
        String launchModeAsString = ObjectUtils
                .toString(command.getParameterMap().get(IdConstants.RUN_MODE_PARAMETER_ID));
        return LaunchMode.fromText(launchModeAsString);
    }

    @Execute
    public void execute(ParameterizedCommand command) {
        try {
            execute(getLaunchMode(command));
        } catch (ExecutionException e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR, e.getMessage());
        } catch (SWTException e) {
            // Ignore it
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR, MessageFormat
                    .format(StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_TEST_SCRIPT_ROOT_CAUSE, e.getMessage()));
            LoggerSingleton.logError(e);
        }
    }

    public static Entity getExecutionTarget() throws DALException {
        MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                application);
        MPart selectedPart = (MPart) composerStack.getSelectedElement();
        if (saveAllParts() && isAnyPartDirty()) {
            String partElementId = selectedPart.getElementId();
            // check the selected part is a test case or test suite part
            if (partElementId.startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
                    && selectedPart.getObject() instanceof TestCaseCompositePart) {
                TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) selectedPart.getObject();
                try {
                    testCaseCompositePart.validateScriptErrors();
                } catch (Exception e) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                            StringConstants.HAND_ERROR_MSG_ERROR_IN_SCRIPT);
                    return null;
                }
                if (testCaseCompositePart.isTestCaseEmpty()) {
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
                            StringConstants.HAND_TITLE_INFORMATION,
                            StringConstants.HAND_INFO_MSG_NO_TEST_STEP_IN_TEST_CASE);
                    return null;
                }
                return testCaseCompositePart.getOriginalTestCase();
            } else if (partElementId.startsWith(IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX)
                    && selectedPart.getObject() instanceof TestSuiteCompositePart) {
                TestSuiteCompositePart testSuiteComposite = (TestSuiteCompositePart) selectedPart.getObject();
                if (testSuiteComposite.getOriginalTestSuite().getTestSuiteTestCaseLinks().isEmpty()) {
                    if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                            StringConstants.HAND_TITLE_INFORMATION,
                            StringConstants.HAND_CONFIRM_MSG_NO_TEST_CASE_IN_TEST_SUITE)) {
                        testSuiteComposite.openAddTestCaseDialog();
                    }
                    return null;
                }
                return testSuiteComposite.getOriginalTestSuite();
            } else if (partElementId.startsWith(IdConstants.COMPABILITY_EDITOR_ID)) {
                CompatibilityEditor editor = (CompatibilityEditor) selectedPart.getObject();
                if (IdConstants.CUCUMBER_EDITOR_ID.equals(editor.getReference().getId())) {
                    FileEditorInput editorInput = (FileEditorInput) editor.getEditor().getEditorInput();
                    String featureFilePath = new File(editorInput.getFile().getRawLocationURI()).getAbsolutePath();

                    return SystemFileController.getInstance().getSystemFile(featureFilePath,
                            ProjectController.getInstance().getCurrentProject());
                }
            }
        }
        return null;
    }

    protected static boolean isAnyPartDirty() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDirtyEditors().length == 0
                && partService.getDirtyParts().isEmpty();
    }

    protected static boolean saveAllParts() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(false)
                && partService.saveAll(false);
    }

    protected abstract IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException;

    public void execute(LaunchMode launchMode) throws Exception {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();

        try {
            AbstractRunConfiguration runConfiguration = (AbstractRunConfiguration) getRunConfigurationForExecution(
                    projectDir);
           
            if (runConfiguration == null) {
                return;
            }
            runConfiguration.setExecutionProfile(ExecutionProfileManager.getInstance().getSelectedProfile());
            execute(launchMode, runConfiguration);
        } catch (InterruptedException ignored) {}
    }

    protected void execute(LaunchMode launchMode, IRunConfiguration runConfiguration) throws Exception {
        Entity targetEntity = getExecutionTarget();

        if (targetEntity == null) {
            return;
        }
        if (targetEntity instanceof TestCaseEntity) {
            TestCaseEntity testCase = (TestCaseEntity) targetEntity;
            executeTestCase(testCase, launchMode, runConfiguration);
        } else if (targetEntity instanceof TestSuiteEntity) {
            TestSuiteEntity testSuite = (TestSuiteEntity) targetEntity;
            executeTestSuite(testSuite, launchMode, runConfiguration);
        } else if (targetEntity instanceof SystemFileEntity) {
            SystemFileEntity feature = (SystemFileEntity) targetEntity;
            executeFeatureFile(feature, launchMode, runConfiguration);
        }
    }

    public void executeFeatureFile(final SystemFileEntity feature, final LaunchMode launchMode,
            final IRunConfiguration runConfig) throws Exception {
        Job job = new Job(ComposerExecutionMessageConstants.AbstractExecutionHandler_HAND_JOB_LAUNCHING_FEATURE_FILE) {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    monitor.beginTask(
                            ComposerExecutionMessageConstants.AbstractExecutionHandler_HAND_JOB_LAUNCHING_FEATURE_FILE,
                            3);

                    final FeatureFileExecutedEntity testSuiteExecutedEntity = new FeatureFileExecutedEntity(feature);
                    monitor.subTask(StringConstants.HAND_JOB_ACTIVATING_VIEWERS);
                    openConsoleLog();
                    validateJobProgressMonitor(monitor);
                    monitor.worked(1);

                    monitor.subTask(StringConstants.HAND_JOB_BUILDING_SCRIPTS);
                    runConfig.build(feature, testSuiteExecutedEntity);
                    validateJobProgressMonitor(monitor);
                    monitor.worked(1);

                    monitor.subTask(
                            ComposerExecutionMessageConstants.AbstractExecutionHandler_HAND_JOB_LAUNCHING_FEATURE_FILE);
                    LauncherManager launcherManager = LauncherManager.getInstance();
                    ILauncher launcher = new IDELauncher(launcherManager, runConfig, launchMode);
                    launcherManager.addLauncher(launcher);

                    monitor.worked(1);

                    monitor.done();
                    return Status.OK_STATUS;

                } catch (JobCancelException e) {
                    return Status.CANCEL_STATUS;
                } catch (final Exception e) {
                    sync.syncExec(() -> {
                        MultiStatusErrorDialog.showErrorDialog(e,
                                ComposerExecutionMessageConstants.AbstractExecutionHandler_HAND_MSG_UNABLE_TO_EXECUTE_FEATURE_FILE,
                                e.getMessage());
                    });

                    return Status.CANCEL_STATUS;
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    public void executeTestCase(final TestCaseEntity testCase, final LaunchMode launchMode,
            final IRunConfiguration runConfig) throws Exception {
        if (testCase == null) {
            return;
        }
        Job job = new ExecuteTestCaseJob(StringConstants.HAND_JOB_LAUNCHING_TEST_CASE, runConfig, testCase, launchMode,
                sync);
        job.setUser(true);
        job.schedule();
    }

    // protected void executeTestSuite(final TestSuiteEntity testSuite, final
    // BrowserType browser,
    // final int pageLoadTimeout, final LaunchMode launchMode) throws Exception
    // {
    public void executeTestSuite(final TestSuiteEntity testSuite, final LaunchMode launchMode,
            final IRunConfiguration runConfig) throws Exception {
        if (testSuite == null) {
            return;
        }

        Job job = new Job(StringConstants.HAND_JOB_LAUNCHING_TEST_SUITE) {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    monitor.beginTask(StringConstants.HAND_JOB_LAUNCHING_TEST_SUITE, 4);
                    monitor.subTask(StringConstants.HAND_JOB_VALIDATING_TEST_SUITE);
                    // back-up

                    final TestSuiteExecutedEntity testSuiteExecutedEntity = new TestSuiteExecutedEntity(testSuite);
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

                        trackTestSuiteExecution(launchMode, runConfig);
                        trackEmailAfterExecution(testSuiteExecutedEntity.getEmailSettings().getEmailConfig().isSendEmailTestFailedOnly());
                        

                        monitor.worked(1);

                        monitor.done();
                        return Status.OK_STATUS;
                    } else {
                        sync.syncExec(new Runnable() {
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
                } catch (final Exception e) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {

                            MultiStatusErrorDialog.showErrorDialog(e,
                                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_EXECUTE_SELECTED_TEST_SUITE,
                                    StringConstants.HAND_ERROR_MSG_REASON_INVALID_TEST_SUITE);
                        }
                    });

                    return Status.CANCEL_STATUS;
                } finally {
                    // UsageInfoCollector.collect(
                    // UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.RUN_SCRIPT, RunningMode.GUI));
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void trackTestSuiteExecution(LaunchMode launchMode, IRunConfiguration runConfig) {
        Trackings.trackExecuteTestSuiteInGuiMode(launchMode.toString(), runConfig.getName());
    }
    
    private void trackEmailAfterExecution(boolean testFailedOnly )
    {   
        Trackings.trackEmailAfterExecute(testFailedOnly);
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

        sync.syncExec(new Runnable() {
            @Override
            public void run() {
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
        return sync;
    }

    void validateJobProgressMonitor(IProgressMonitor monitor) throws JobCancelException {
        if (monitor.isCanceled()) {
            throw new JobCancelException();
        }
    }
}
