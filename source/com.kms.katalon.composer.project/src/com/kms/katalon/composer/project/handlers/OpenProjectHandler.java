package com.kms.katalon.composer.project.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.dialog.WalkthroughDialog;
import com.kms.katalon.composer.project.dialog.WalkthroughItem;
import com.kms.katalon.composer.project.dialog.WalkthroughItem.SecondaryLinkItem;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.integration.analytics.configuration.AnalyticsSettingProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;

public class OpenProjectHandler {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EModelService modelService;

    @Inject
    private EPartService partService;

    @Inject
    private MApplication application;

    @Inject
    private UISynchronize sync;

    @Execute
    public void execute(Shell shell) {
        try {
            DirectoryDialog directoryDialog = new DirectoryDialog(shell);
            directoryDialog.open();

            if (directoryDialog.getFilterPath() != null) {
                File projectDirectory = new File(directoryDialog.getFilterPath());
                if (projectDirectory != null && projectDirectory.exists() && projectDirectory.isDirectory()) {
                    File projectFile = getProjectFile(projectDirectory);

                    if (projectFile != null) {
                        if (!CloseProjectHandler.closeCurrentProject(partService, modelService, application,
                                eventBroker))
                            return;
                        openProjectEventHandler(shell, projectFile.getAbsolutePath());
                    } else {
                        MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                                StringConstants.HAND_WARN_MSG_NO_PROJ_FOUND);
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.HAND_ERROR_MSG_CANNOT_OPEN_PROJ);
        }

    }

    public static File getProjectFile(File projectDirectory) {
        for (File file : projectDirectory.listFiles()) {
            if (('.' + FilenameUtils.getExtension(file.getAbsolutePath()))
                    .equals(ProjectEntity.getProjectFileExtension())) {
                return file;
            }
        }
        return null;
    }

    public static List<File> getProjectFiles(File projectDirectory) {
        List<File> childProjectFiles = new ArrayList<>();
        for (File file : projectDirectory.listFiles()) {
            if (file.isDirectory()) {
                File projectFile = getProjectFile(file);
                if (projectFile != null) {
                    childProjectFiles.add(projectFile);
                }
            }
        }
        return childProjectFiles;
    }

    @Inject
    @Optional
    private void openProjectEventHandler(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
            @UIEventTopic(EventConstants.PROJECT_OPEN) final String projectPk)
            throws InvocationTargetException, InterruptedException {
        doOpenProject(shell, projectPk, sync, eventBroker, partService, modelService, application);
    }

    @Inject
    @Optional
    private void restoreOpenProjectEventHandler(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
            @UIEventTopic(EventConstants.PROJECT_OPEN_LATEST) final String projectPk)
            throws InvocationTargetException, InterruptedException {
        doOpenProject(shell, projectPk, sync, eventBroker, partService, modelService, application);

        eventBroker.post(EventConstants.PROJECT_RESTORE_SESSION, null);
    }

    public static void doOpenProject(Shell shell, final String projectPk, final UISynchronize syncService,
            final IEventBroker eventBrokerService, EPartService partService, final EModelService modelService,
            final MApplication application) throws InvocationTargetException, InterruptedException {
        if (!CloseProjectHandler.closeCurrentProject(partService, modelService, application, eventBrokerService)) {
            return;
        }
        new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask(StringConstants.HAND_OPENING_PROJ, 10);
                    SubMonitor progress = SubMonitor.convert(monitor, 10);
                    monitor.worked(1);
                    monitor.subTask(StringConstants.HAND_LOADING_PROJ);
                    final ProjectEntity project = ProjectController.getInstance().openProjectForUI(projectPk,
                            progress.newChild(7, SubMonitor.SUPPRESS_SUBTASK));                    
                    
                    monitor.subTask(StringConstants.HAND_REFRESHING_EXPLORER);
                    syncService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (project != null) {
                                    // Set project name on window title
                                    OpenProjectHandler.updateProjectTitle(project, modelService, application);
                                    Trackings.trackOpenProject(project);
                                    
                                    AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
                                            ProjectController.getInstance().getCurrentProject().getFolderLocation());

                                    AnalyticsTeam teamKA = analyticsSettingStore.getTeam();
                                    AnalyticsProject projectKA = analyticsSettingStore.getProject();
                                    AnalyticsSettingProject analyticsConfigutionProject = new AnalyticsSettingProject();

                                    if (teamKA.getId() != null && projectKA.getId() != null) {
                                        analyticsConfigutionProject.checkUserAccessProject();
                                    } 
                                }
                                eventBrokerService.post(EventConstants.EXPLORER_RELOAD_INPUT,
                                        TreeEntityUtil.getAllTreeEntity(project));
                            } catch (Exception e) {
                                LoggerSingleton.logError(e);
                            }
                        }
                    });

                    eventBrokerService.post(EventConstants.GLOBAL_VARIABLE_REFRESH, null);
                    monitor.worked(1);
                    LauncherManager.refresh();
                    eventBrokerService.post(EventConstants.JOB_REFRESH, null);
                    eventBrokerService.post(EventConstants.CONSOLE_LOG_RESET, null);
                    monitor.worked(1);

                    TimeUnit.SECONDS.sleep(1);
                    eventBrokerService.post(EventConstants.PROJECT_OPENED, null);
                    TimeUnit.SECONDS.sleep(1);
                    return;
                } catch (final Exception e) {
                    syncService.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_CANNOT_OPEN_PROJ,
                                    e.getClass().getSimpleName());
                        }
                    });
                    LoggerSingleton.logError(e);
                    return;
                } finally {
                    monitor.done();
                }
            }
        });
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if(project != null && project.getType().equals(ProjectType.WEBUI)){
    		ScopedPreferenceStore prefStore = PreferenceStoreManager
    				.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    		boolean shouldShowWalkthroughDialog = prefStore.getBoolean(PreferenceConstants.GENERAL_SHOW_WALKTHROUGH_DIALOG);
    		if (shouldShowWalkthroughDialog) {
    			Display.getDefault().asyncExec(() -> {
        			WalkthroughDialog walkthroughDialog = new WalkthroughDialog(
        					"WebUiWalkthroughDialog", "Walkthrough for beginners",
        					null, getItems(), "https://docs.katalon.com/katalon-studio/docs/index.html",
        					"I am an experienced user");
        			walkthroughDialog.open();
        			if(walkthroughDialog.isIgnore()){
        				prefStore.setValue(PreferenceConstants.GENERAL_SHOW_WALKTHROUGH_DIALOG, false);
        			}
    			});
    		}

        }
        
    }

    public static void updateProjectTitle(ProjectEntity projectEntity, EModelService modelService, MApplication app) {
        MWindow win = (MWindow) modelService.find(IdConstants.MAIN_WINDOW_ID, app);
        String versionTag = ApplicationInfo.versionTag();
        if (win != null) {
            win.setLabel(win.getLabel().split(" - ")[0] + " - " +
                (!StringUtils.isBlank(versionTag) ? versionTag + " - " : "") +
                projectEntity.getName() + " - [Location: "
                    + projectEntity.getFolderLocation() + "]");
            win.updateLocalization();
        }
    }

	private static List<WalkthroughItem> getItems() {
		List<WalkthroughItem> items = new ArrayList<>();
		WalkthroughItem createTestCase = new WalkthroughItem("Create a test case", EventConstants.TESTCASE_OPEN);
		List<SecondaryLinkItem> createTestCaseLinks = new ArrayList<>();
		createTestCaseLinks.add(new WalkthroughItem.SecondaryLinkItem("How to create test cases using script mode",
				"https://docs.katalon.com/katalon-studio/tutorials/create_test_case_using_record_playback.html"));
		createTestCaseLinks
				.add(new WalkthroughItem.SecondaryLinkItem("How to create test cases using Record & Playback",
						"https://docs.katalon.com/katalon-studio/tutorials/create_test_case_using_script_mode.html"));
		createTestCase.setSecondaryLinkItems(createTestCaseLinks);
		items.add(createTestCase);

		WalkthroughItem executeTestCase = new WalkthroughItem("Execute the test case",
				EventConstants.EXECUTE_TEST_CASE);
		List<SecondaryLinkItem> executeTestCaseLinks = new ArrayList<>();
		executeTestCaseLinks.add(new WalkthroughItem.SecondaryLinkItem("How to execute test cases",
				"https://docs.katalon.com/katalon-studio/docs/execute-a-test-case-or-a-test-suite.html"));
		executeTestCase.setSecondaryLinkItems(executeTestCaseLinks);
		items.add(executeTestCase);

		WalkthroughItem createTestSuite = new WalkthroughItem("Plan the test case to a test suite",
				EventConstants.TEST_SUITE_OPEN);
		List<SecondaryLinkItem> createTestSuiteLinks = new ArrayList<>();
		createTestSuiteLinks.add(new WalkthroughItem.SecondaryLinkItem("How to add test suites",
				"https://docs.katalon.com/katalon-studio/docs/test-suite.html"));
		createTestSuiteLinks.add(new WalkthroughItem.SecondaryLinkItem("How to design test suites",
				"https://docs.katalon.com/katalon-studio/docs/design-a-test-suite.html"));
		createTestSuite.setSecondaryLinkItems(createTestSuiteLinks);
		items.add(createTestSuite);

		WalkthroughItem executeTestSuite = new WalkthroughItem("Execute the test suite",
				EventConstants.EXECUTE_TEST_SUITE);
		List<SecondaryLinkItem> executeTestSuiteLinks = new ArrayList<>();
		executeTestSuiteLinks.add(new WalkthroughItem.SecondaryLinkItem("How to execute test suites",
				"https://docs.katalon.com/katalon-studio/docs/execute-a-test-case-or-a-test-suite.html"));
		executeTestSuiteLinks.add(new WalkthroughItem.SecondaryLinkItem("How to execute test suites in console mode",
				"https://docs.katalon.com/katalon-studio/docs/console-mode-execution.html#katalon-command-line-options"));
		executeTestSuite.setSecondaryLinkItems(executeTestSuiteLinks);
		items.add(executeTestSuite);

		return items;
	}
}
