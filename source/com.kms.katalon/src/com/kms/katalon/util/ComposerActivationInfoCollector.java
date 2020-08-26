package com.kms.katalon.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.activation.dialog.ActivationDialogV2;
import com.kms.katalon.activation.dialog.ActivationOfflineDialogV2;
import com.kms.katalon.activation.dialog.SignupDialog;
import com.kms.katalon.activation.dialog.SignupSurveyDialog;
import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.helper.UserProfileHelper;
import com.kms.katalon.application.userprofile.UserProfile;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.project.handlers.OpenProjectHandler;
import com.kms.katalon.composer.project.menu.SampleProjectParameterizedCommandBuilder;
import com.kms.katalon.composer.project.sample.SampleLocalProject;
import com.kms.katalon.composer.project.sample.SampleProject;
import com.kms.katalon.composer.project.sample.SampleProjectType;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProjectProvider;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.composer.quickstart.QuickCreateFirstWebUITestCase;
import com.kms.katalon.composer.quickstart.QuickPrepareProjectDialog;
import com.kms.katalon.composer.quickstart.QuickStartDialog;
import com.kms.katalon.composer.quickstart.QuickStartDialogV2;
import com.kms.katalon.composer.quickstart.WelcomeBackDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.NamingUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.project.QuickStartProjectType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.service.Trackings;

public class ComposerActivationInfoCollector extends ActivationInfoCollector {

    private static final long RANDOM_MIN = 78364164096L;

    private static final long RANDOM_MAX = 2821109907455L;

    private ComposerActivationInfoCollector() {
        super();
    }
    
    private static boolean isActivated;
    
    private static EventHandler openQuickCreateFirstTestCaseHandler = null;

    public static boolean checkActivation(boolean isStartup) throws InvocationTargetException, InterruptedException {
        Shell shell = Display.getCurrent().getActiveShell();
        new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                if (isStartup) {
                    monitor.beginTask(StringConstants.MSG_ACTIVATING, IProgressMonitor.UNKNOWN);
                } else {
                    //Logout
                    monitor.beginTask(StringConstants.MSG_CLEANING, IProgressMonitor.UNKNOWN);
                    try {
                        ActivationInfoCollector.postEndSession();
                        ActivationInfoCollector.releaseLicense();
                        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.ACTIVATION_DEACTIVATED, null);
                    } catch (Exception e) {
                        LogUtil.logError(e);
                    }
                    ApplicationInfo.cleanAll();
                }
                LogUtil.logInfo("Start checking and mark activated for GUI mode");
                isActivated = ActivationInfoCollector.checkAndMarkActivatedForGUIMode();
                LogUtil.logInfo("End checking and mark activated for GUI mode");
                monitor.done();
            }
        });

        if (!isActivated) {
            // Send anonymous info for the first time using
            Trackings.trackOpenFirstTime();
        }
        if (!isActivated) {
            if (checkActivationDialog()) {
                UserProfile currentProfile = UserProfileHelper.getCurrentProfile();
                if (!currentProfile.isDoneQuickStart()) {
                    showQuickStartDialogV2ForTheFirstTime();
                } else {
//                    showQuickPrepareProjectPopupForTheFirstTime();
                }
//                showFunctionsIntroductionForTheFirstTime();
                // openSignupSurveyDialog(Display.getCurrent().getActiveShell());
                return true;
            } else {
                return false;
            }
        }

        return true;
    }
    
    private static void openSignupSurveyDialog(Shell activeShell) {
        SignupSurveyDialog dialog = new SignupSurveyDialog(activeShell);
        dialog.open();
    }

    private static boolean checkActivationDialog() {
        //Please remove before create pull request 
        int result = new ActivationDialogV2(null).open();
        switch (result) {
            case ActivationDialogV2.OK:
                return true;
            case ActivationDialogV2.REQUEST_SIGNUP_CODE:
                return checkSignupDialog();
            case ActivationDialogV2.REQUEST_OFFLINE_CODE:
                return checkOfflineActivationDialog(false);
            default:
                return false;
        }
    }

    private static boolean checkOfflineActivationDialog(boolean navigateFromSignUp) {
        int result = new ActivationOfflineDialogV2(null, navigateFromSignUp).open();
        switch (result) {
            case ActivationOfflineDialogV2.OK:
                return true;
            case ActivationOfflineDialogV2.REQUEST_ONLINE_CODE:
                return checkActivationDialog();
            case ActivationOfflineDialogV2.REQUEST_SIGNUP_CODE:
                return checkSignupDialog();
            default:
                return false;
        }
    }

    private static boolean checkSignupDialog() {
        int result = new SignupDialog(null).open();
        switch (result) {
            case SignupDialog.OK:
                // SignupSurveyDialog dialog = new SignupSurveyDialog(null);
                // dialog.open();
                return true;
            case SignupDialog.REQUEST_ACTIVATION_CODE:
                return checkActivationDialog();
            case SignupDialog.REQUEST_OFFLINE_CODE:
                return checkOfflineActivationDialog(true);
            default:
                return false;
        }
    }

    private static void showQuickStartDialogV2ForTheFirstTime() {
        // Prevent to open recent project when the user is in the Quick Start flow
        ProjectController.getInstance().setOpenning(true);

        Shell mainShell = Display.getCurrent().getActiveShell();
        QuickStartDialogV2 quickStartDialog = new QuickStartDialogV2(mainShell);
        quickStartDialog.open();

        UserProfile currentProfile = UserProfileHelper.getCurrentProfile();
        currentProfile = UserProfileHelper.getCurrentProfile();
        currentProfile.setExperienceLevel(quickStartDialog.getUserLevel());
        currentProfile.setPreferredTestingType(quickStartDialog.getProjectType());
        UserProfileHelper.saveProfile(currentProfile);

        if (currentProfile.isOldUser()) {
            currentProfile = UserProfileHelper.getCurrentProfile();
            currentProfile.setDoneQuickStart(true);
            UserProfileHelper.saveProfile(currentProfile);

            WelcomeBackDialog welcomeBackDialog = new WelcomeBackDialog(mainShell);
            welcomeBackDialog.open();

            activeShell(mainShell);
            return;
        }

        if (!currentProfile.isDoneCreateFirstTestCase()) {
            switch (quickStartDialog.getProjectType()) {
                case WEBUI:
                    quickCreateFirstWebUIProject(mainShell);
                    break;
                case BDD: case MOBILE: case WEBSERVICE:
                    quickCreateFirstSampleProject(quickStartDialog.getProjectType());
                    break;
                default:
                    break;
            }
        }

        currentProfile = UserProfileHelper.getCurrentProfile();
        currentProfile.setDoneQuickStart(true);
        UserProfileHelper.saveProfile(currentProfile);
    }
    
    private static void quickCreateFirstWebUIProject(Shell mainShell) {
        try {
            String projectName = getFirstSampleProjectName(QuickStartProjectType.WEBUI);

            ProjectEntity firstProject = ProjectController.getInstance().addNewProject(projectName, StringUtils.EMPTY,
                    GlobalStringConstants.DEFAULT_PROJECT_LOCATION, true, true);
            firstProject.setType(ProjectType.WEBUI);
            ProjectController.getInstance().updateProject(firstProject);

            if (openQuickCreateFirstTestCaseHandler == null) {
                openQuickCreateFirstTestCaseHandler = (event) -> {
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(3000); // Wait for workbench initialization
                            } catch (InterruptedException error) {
                                // Just skip
                            }

                            UserProfile currentProfile = UserProfileHelper.getCurrentProfile();
                            if (!currentProfile.isNewUser()
                                    || currentProfile.isDoneCreateFirstTestCase()
                                    || !currentProfile.isPreferWebUI()) {
                                return;
                            }

                            UISynchronizeService.syncExec(() -> {
                                KatalonApplicationActivator.getTestOpsConfiguration().testOpsQuickIntergration();
                                QuickCreateFirstWebUITestCase quickCreateFirstTestCaseDialog = new QuickCreateFirstWebUITestCase(mainShell);
                                quickCreateFirstTestCaseDialog.open();

                                currentProfile.setDoneCreateFirstTestCase(true);
                                currentProfile.setPreferredBrowser(quickCreateFirstTestCaseDialog.getPreferredBrowser());
                                currentProfile.setPreferredSite(quickCreateFirstTestCaseDialog.getPreferredSite());
                                UserProfileHelper.saveProfile(currentProfile);

                                activeShell(mainShell);
                                EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.KATALON_RECORD, null);
                            });
                        }
                    });
                    thread.start();
                };
            }
            EventBrokerSingleton.getInstance().getEventBroker().unsubscribe(openQuickCreateFirstTestCaseHandler);
            EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.PROJECT_OPENED, openQuickCreateFirstTestCaseHandler);

            OpenProjectHandler.doOpenProject(mainShell, firstProject.getLocation(),
                    UISynchronizeService.getInstance().getSync(), EventBrokerSingleton.getInstance().getEventBroker(),
                    PartServiceSingleton.getInstance().getPartService(),
                    ModelServiceSingleton.getInstance().getModelService(),
                    ApplicationSingleton.getInstance().getApplication());
        } catch (Exception error) {
            MultiStatusErrorDialog.showErrorDialog(error, "Warning!", error.getMessage(),
                    Display.getCurrent().getActiveShell());
        }
    }
    
    private static void quickCreateFirstSampleProject(QuickStartProjectType quickStartProjectType) {
        List<SampleRemoteProject> remoteProjects = SampleRemoteProjectProvider.getCachedProjects();
        List<SampleLocalProject> localProjects = SampleProjectProvider.getInstance().getSampleProjects();
        
        List<SampleProject> sampleProjects = Stream.concat(remoteProjects.stream(), localProjects.stream())
                .collect(Collectors.toList());
        
        for (SampleProject sampleProject : sampleProjects) {
            if (isEqualProjectType(quickStartProjectType, sampleProject.getType())) {
                SampleProjectParameterizedCommandBuilder commandBuilder = new SampleProjectParameterizedCommandBuilder();
                try {
                    String firstProjectName = getFirstSampleProjectName(quickStartProjectType);
                    sampleProject.setSuggestedName(firstProjectName);
                    ParameterizedCommand command = sampleProject instanceof SampleRemoteProject
                            ? commandBuilder.createRemoteProjectParameterizedCommand((SampleRemoteProject) sampleProject)
                            : commandBuilder
                                    .createSampleLocalProjectParameterizedCommand((SampleLocalProject) sampleProject);
                    CommandUtil.autoHandleExecuteCommand(command);
                } catch (CommandException error) {
                    MultiStatusErrorDialog.showErrorDialog(error, "Warning!", error.getMessage(),
                            Display.getCurrent().getActiveShell());
                }
                break;
            }
        }
    }

    private static String getFirstSampleProjectName(QuickStartProjectType testingType) {
        String projectName = getProjectNameByType(testingType);
        File firstProjectFolder = new File(GlobalStringConstants.DEFAULT_PROJECT_LOCATION);
        firstProjectFolder.mkdirs();
        return NamingUtil.getUniqueFileName(projectName, firstProjectFolder.getAbsolutePath());
    }

    private static String getProjectNameByType(QuickStartProjectType testingType) {
        switch (testingType) {
            case WEBUI:
                return "My First Web UI Project";
            case MOBILE:
                return "My First Mobile Project";
            case WEBSERVICE:
                return "My First API Project";
            case BDD:
                return "My First BDD Project";
            default:
                return null;
        }
    }
    
    private static void activeShell(Shell shell) {
        if (shell != null) {
            shell.forceActive();
        } else {
            if (Display.getCurrent().getActiveShell() != null) {
                Display.getCurrent().getActiveShell().forceActive();
            }
        }
    }

    private static boolean isEqualProjectType(QuickStartProjectType quickStartProjectType,
            SampleProjectType sampleProjectType) {
        return sampleProjectType == SampleProjectType.WEBUI && quickStartProjectType == QuickStartProjectType.WEBUI
                || sampleProjectType == SampleProjectType.MOBILE && quickStartProjectType == QuickStartProjectType.MOBILE
                || sampleProjectType == SampleProjectType.WS && quickStartProjectType == QuickStartProjectType.WEBSERVICE
                || sampleProjectType == SampleProjectType.MIXED && quickStartProjectType == QuickStartProjectType.BDD;
    }

    private static void showFunctionsIntroductionForTheFirstTime() {
        QuickStartDialog quickStartDialog = new QuickStartDialog(Display.getCurrent().getActiveShell());
        quickStartDialog.open();
        
//        RecommendPluginsDialog recommendPlugins = new RecommendPluginsDialog(Display.getCurrent().getActiveShell());
//
//        recommendPlugins.open();
//        recommendPlugins.installPressed();
    }
    
    private static void showQuickPrepareProjectPopupForTheFirstTime() {
        QuickPrepareProjectDialog quickCreateProjectDialog = new QuickPrepareProjectDialog(
                Display.getCurrent().getActiveShell());
        quickCreateProjectDialog.open();
    }

    public static String genRequestActivationInfo() {
        String requestCodePropName = ApplicationStringConstants.REQUEST_CODE_PROP_NAME;
        String requestActivationCode = ApplicationInfo.getAppProperty(requestCodePropName);

        if (requestActivationCode == null || requestActivationCode.trim().length() < 1) {
            requestActivationCode = genRequestActivateOfflineCode();
            ApplicationInfo.setAppProperty(requestCodePropName, requestActivationCode, true);
        }
        return requestActivationCode;
    }

    private static String genRequestActivateOfflineCode() {
        Random random = new Random();
        long num = RANDOM_MIN + (long) ((RANDOM_MAX - RANDOM_MIN) * random.nextFloat());
        return Long.toString(num, 36).toUpperCase();
    }
}
