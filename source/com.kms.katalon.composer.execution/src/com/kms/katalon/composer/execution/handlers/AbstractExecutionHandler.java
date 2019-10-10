package com.kms.katalon.composer.execution.handlers;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
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
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.addons.minmax.TrimStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.katalon.platform.api.exception.PlatformException;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.ExecutionProfileManager;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.ProblemMarkerConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.ProblemsViewDialog;
import com.kms.katalon.composer.execution.exceptions.JobCancelException;
import com.kms.katalon.composer.execution.jobs.ExecuteTestCaseJob;
import com.kms.katalon.composer.execution.launcher.IDELaunchShorcut;
import com.kms.katalon.composer.execution.launcher.IDELauncher;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testsuite.parts.ParentTestSuiteCompositePart;
import com.kms.katalon.composer.testsuite.parts.TestSuiteCompositePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.FeatureFileExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.exception.ExtensionRequiredException;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
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
                        if (editor != null && IdConstants.CUCUMBER_EDITOR_ID.equals(editor.getReference().getId())) {
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
                    && selectedPart.getObject() instanceof ParentTestSuiteCompositePart) {
                ParentTestSuiteCompositePart testSuiteComposite = (ParentTestSuiteCompositePart) selectedPart
                        .getObject();
                TestSuiteEntity originalTestSuite = testSuiteComposite.getOriginalTestSuite();
                if (originalTestSuite instanceof FilteringTestSuiteEntity) {
                    return originalTestSuite;
                }
                if (originalTestSuite.getTestSuiteTestCaseLinks().isEmpty()) {
                    if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                            StringConstants.HAND_TITLE_INFORMATION,
                            StringConstants.HAND_CONFIRM_MSG_NO_TEST_CASE_IN_TEST_SUITE)) {
                        ((TestSuiteCompositePart) testSuiteComposite).openAddTestCaseDialog();
                    }
                    return null;
                }
                return originalTestSuite;
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

                    try {
                        return SystemFileController.getInstance().getSystemFile(featureFilePath,
                                ProjectController.getInstance().getCurrentProject());
                    } catch (ControllerException e) {
                        LogUtil.logError(e);
                        return null;
                    }
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


    private String getProjectDir() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    public AbstractRunConfiguration buildRunConfiguration(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        TestSuiteRunnable runnable = new TestSuiteRunnable();
        UISynchronizeService.syncExec(runnable);
        return runnable.getRunConfiguration();
    }

	protected void execute(LaunchMode launchMode) throws Exception {
		Entity targetEntity = getExecutionTarget();

		if (targetEntity == null) {
			return;
		}

		IProject project = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject());
		List<IMarker> errorMarkers = ProblemMarkerConstants.findErrorMarkers(project);
		if (errorMarkers.size() != 0) {
			ProblemsViewDialog dialog = new ProblemsViewDialog(Display.getCurrent().getActiveShell());
			switch (dialog.open()) {
			case ProblemsViewDialog.SHOW_PROBLEM_ID: {
				openProlemsView();
				break;
			}

			case IDialogConstants.PROCEED_ID: {
				settingDebugUI();
				if (targetEntity instanceof TestCaseEntity) {
					TestCaseEntity testCase = (TestCaseEntity) targetEntity;
					executeTestCase(testCase, launchMode);
					eventBroker.post(EventConstants.EXECUTE_TEST_CASE, null);
				} else if (targetEntity instanceof TestSuiteEntity) {
					TestSuiteEntity testSuite = (TestSuiteEntity) targetEntity;
					executeTestSuite(testSuite, launchMode);
					eventBroker.post(EventConstants.EXECUTE_TEST_SUITE, null);
				} else if (targetEntity instanceof SystemFileEntity) {
					SystemFileEntity feature = (SystemFileEntity) targetEntity;
					executeFeatureFile(feature, launchMode);
				}
				break;
			}

			default:
				return;
			}
		}
	}

    public void settingDebugUI(){
        ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(IdConstants.DEBUG_UI_ID);
        boolean isFirstTimeSetup = store.getBoolean(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED);

        if (isFirstTimeSetup) {
            return;
        }
        
        store.setValue(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED, true);
        store.setValue(PreferenceConstants.CANCLE_DEBUG_UI, "always");
        try {
            store.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        
    }
    public void executeFeatureFile(final SystemFileEntity feature, final LaunchMode launchMode) throws Exception {
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
                    AbstractRunConfiguration runConfig = buildRunConfiguration(getProjectDir());
                    if (runConfig == null) {
                        return Status.CANCEL_STATUS;
                    }
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
                    UISynchronizeService.syncExec(() -> {
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

    public void executeTestCase(final TestCaseEntity testCase, final LaunchMode launchMode) throws Exception {
        if (testCase == null) {
            return;
        }
        Job job = new ExecuteTestCaseJob(StringConstants.HAND_JOB_LAUNCHING_TEST_CASE, testCase, launchMode,
                UISynchronizeService.getInstance().getSync(), this);
        job.setUser(true);
        job.schedule();
    }

    // protected void executeTestSuite(final TestSuiteEntity testSuite, final
    // BrowserType browser,
    // final int pageLoadTimeout, final LaunchMode launchMode) throws Exception
    // {
    public void executeTestSuite(final TestSuiteEntity testSuite, final LaunchMode launchMode) throws Exception {
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
                        AbstractRunConfiguration runConfig = buildRunConfiguration(getProjectDir());
                        if (runConfig == null) {
                            return Status.CANCEL_STATUS;
                        }
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

    // Open Problems View and its partStack
    public static void openProlemsView() {
        UISynchronizeService.asyncExec(new Runnable() {
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

                // set current page of console partStack is problems view
                MUIElement consoleLogPart = (MUIElement) modelService.find(IdConstants.IDE_PROBLEM_VIEW_PART_ID,
                        consolePartStack);
                if (consoleLogPart != null && consolePartStack.getSelectedElement() != consoleLogPart) {
                    consolePartStack.setSelectedElement((MStackElement) consoleLogPart);
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
    
    private class TestSuiteRunnable implements Runnable {
        
        private AbstractRunConfiguration runConfiguration;
        private Exception exception;

        @Override
        public void run() { 
            try {
                runConfiguration = (AbstractRunConfiguration) getRunConfigurationForExecution(
                        getProjectDir());


                if (runConfiguration == null) {
                    runConfiguration = null;
                    return;
                }
                runConfiguration.setExecutionProfile(ExecutionProfileManager.getInstance().getSelectedProfile());
            } catch (IOException | ExecutionException | InterruptedException e) {
                this.exception = e;
                runConfiguration = null;
            }
        }
        
        public AbstractRunConfiguration getRunConfiguration() {
            return runConfiguration;
        }
    }
}
