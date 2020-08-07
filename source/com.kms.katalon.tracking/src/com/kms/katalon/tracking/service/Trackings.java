package com.kms.katalon.tracking.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.model.TrackInfo;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;
import com.kms.katalon.tracking.osgi.service.ServiceConsumer;
import com.kms.katalon.util.SystemInformationUtil;

public class Trackings {

    private static TrackingService trackingService = new TrackingService();

    public static void trackOpenApplication(boolean isAnonymous, String runningMode) throws Exception {
        double cpu = 0.0;
        double percentageUsed, percentageUsedFormatted;
        long maxMemory, usedMemory, totalMemory, freeMemory;
        long freePhysicalMemorySize = 0L;
        long totalPhysicalMemorySize = 0L;
        cpu = SystemInformationUtil.getProcessCpuLoad();
        maxMemory = SystemInformationUtil.getMaxMemory();
        usedMemory = SystemInformationUtil.getUsedMemory();
        totalMemory = SystemInformationUtil.getTotalMemory();
        freeMemory = SystemInformationUtil.getFreeMemory();
        percentageUsed = SystemInformationUtil.getPercentageUsed();
        freePhysicalMemorySize = SystemInformationUtil.freePhysicalMemorySize();
        totalPhysicalMemorySize = SystemInformationUtil.totalPhysicalMemorySize();
        percentageUsedFormatted = SystemInformationUtil.getPercentageUsedFormatted();
        trackAction("openApplication", isAnonymous, "runningMode", runningMode, "percent_cpu", cpu, "max_memory",
                maxMemory, "used_memory", usedMemory, "total_memory", totalMemory, "free_memory", freeMemory,
                "percent_used", percentageUsed, "format_percent_used", percentageUsedFormatted,
                "freephysicalMemorySize", freePhysicalMemorySize, "totalphysicalMemorySize", totalPhysicalMemorySize);

    }

    public static void trackProjectStatistics(ProjectEntity project, boolean isAnonymous, String runningMode) {
        trackUsageData(project, isAnonymous, runningMode, "collectStatistics");
    }

    public static void trackOpenProject(ProjectEntity project) {
        trackUsageData(project, false, "gui", "openProject");
        trackOpenObject("project");
    }

    private static void trackUsageData(ProjectEntity project, boolean isAnonymous, String runningMode,
            String triggeredBy) {

        if (project == null) {
            return;
        }

        try {
            JsonObject statisticsObject = collectProjectStatistics(project);

            JsonObject properties = new JsonObject();
            properties.addProperty("triggeredBy", triggeredBy);
            properties.addProperty("runningMode", runningMode);
            
            KatalonPackage katalonPackage = KatalonApplication.getKatalonPackage();
            properties.addProperty("katalonPackage", katalonPackage.getPackageName());
            
            LicenseType licenseType = ActivationInfoCollector.getLicenseType();
            properties.addProperty("licenseType", licenseType.name());
            
            JsonUtil.mergeJsonObject(statisticsObject, properties);

            TrackInfo trackInfo = TrackInfo.create().eventName(TrackEvents.KATALON_STUDIO_TRACK).anonymous(isAnonymous)
                    .properties(properties);

            trackingService.track(trackInfo);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    // collect
    private static JsonObject collectProjectStatistics(ProjectEntity project) throws Exception {
        IProjectStatisticsCollector collector = ServiceConsumer.getProjectStatisticsCollector();

        ProjectStatistics statistics = collector.collect(project);

        JsonObject statisticsObject = JsonUtil.toJsonObject(statistics);

        return statisticsObject;
    }

    public static void trackOpenFirstTime() {
        TrackInfo trackInfo = TrackInfo.create().eventName(TrackEvents.KATALON_OPEN_FIRST_TIME).anonymous(true);

        trackingService.track(trackInfo);
    }

    public static void trackQuickStartAction(String action) {
        trackUserAction("quickStartAction", "userAction", action);
    }

    public static void trackQuickStartOpen() {
        trackUserAction("ksu_quick_start_welcome_open");
    }

    public static void trackQuickStartFirstQuestion(boolean newUser, String projectType) {
        trackUserAction("ksu_quick_start_welcome_continue", "new_user", newUser, "project_type", projectType);
    }

    public static void trackQuickStartRecordOpen() {
        trackUserAction("ksu_quick_start_record_open");
    }

    public static void trackQuickStartStartRecord(String browser) {
        trackUserAction("ksu_quick_start_record_record", "browser", browser);
    }

    public static void trackQuickStartRunOpen() {
        trackUserAction("ksu_quick_start_run_open");
    }

    public static void trackQuickStartStartRun(String browser) {
        trackUserAction("ksu_quick_start_run_run", "browser", browser);
    }

    public static void trackQuickStartRunPass() {
        trackUserAction("ksu_quick_start_passed_run_open");
    }

    public static void trackQuickStartRunFail() {
        trackUserAction("ksu_quick_start_failed_run_open");
    }

    public static void trackQuickStartWelcomeBack() {
        trackUserAction("ksu_quick_start_greeting_old_user_open");
    }

    public static void trackQuickStartWelcomeBackOpenProject() {
        trackUserAction("ksu_quick_start_greeting_old_user_open_project");
    }

    public static void trackQuickStartWelcomeBackCloneProject() {
        trackUserAction("ksu_quick_start_greeting_old_user_clone_git");
    }

    public static void trackQuickStartWelcomeBackNewProject() {
        trackUserAction("ksu_quick_start_greeting_old_user_new_project");
    }

    public static void trackWebSpy() {
        trackUserAction("spyWeb");
    }

    public static void trackMobileSpy(String deviceType) {
        trackUserAction("spyMobile", "deviceType", deviceType);
    }
    
    public static void trackWindowsSpy() {
        trackUserAction("spyWindows");
    }

    public static void trackWebRecord(String browserType, boolean useActiveBrowser,
            String webLocatorConfig) {
        trackUserAction("recordWeb", "browserType", browserType, "active", useActiveBrowser,
                "webLocatorConfig", webLocatorConfig);
    }
    
    public static void trackMobileRecord(String deviceType) {
        trackUserAction("recordMobile", "deviceType", deviceType);
    }
    
    public static void trackWindowsRecord() {
        trackUserAction("recordWindows");
    }
    
    public static void trackWindowsNativeRecord() {
        trackUserAction("recordWindowsNative");
    }

    public static void trackExecuteTestCase(String launchMode, String driverType, String result, long duration,
            boolean isEnableSelfHealing, boolean isTriggerSelfHealing, String healingInfo) {
        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "launchMode", launchMode, "driver", driverType,
                "executionResult", result, "duration", duration, "enable_self_healing", isEnableSelfHealing,
                "trigger_self_healing", isTriggerSelfHealing
        }));
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }
        trackUserAction("executeTestCase", properties.toArray());
    }

    public static void trackExecuteTestSuiteInGuiMode(String launchMode, String driverType, String result,
            long duration, String retryStrategy, int numberOfRetry, boolean isEnableSelfHealing,
            boolean isTriggerSelfHealing, String healingInfo, int totalTestCases, int passedTestCases) {
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "runningMode", "gui", "launchMode", launchMode, "driver", driverType,
                "executionResult", result, "duration", duration, "retryStrategy", retryStrategy, "numberOfRerun",
                numberOfRetry, "enable_self_healing", isEnableSelfHealing, "trigger_self_healing",
                isTriggerSelfHealing, "total_test_cases", totalTestCases, "passed_test_cases", passedTestCases
        }));

        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }

        trackUserAction("executeTestSuite", properties.toArray());
    }

    public static void trackExecuteTestSuiteInConsoleMode(boolean isAnonymous, String driverType, String result,
            long duration, String retryStrategy, int numberOfRetry, boolean isEnableSelfHealing,
            boolean isTriggerSelfHealing, String healingInfo, int totalTestCases, int passedTestCases) {
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "runningMode", "console", "driver", driverType, "executionResult",
                result, "duration", duration, "retryStrategy", retryStrategy, "numberOfRerun",
                numberOfRetry, "enable_self_healing", isEnableSelfHealing, "trigger_self_healing",
                isTriggerSelfHealing, "total_test_cases", totalTestCases, "passed_test_cases", passedTestCases
        }));

        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }

        trackAction("executeTestSuite", isAnonymous, properties.toArray());
    }

    public static void trackExecuteSequentialTestSuiteCollectionInGuiMode(String result, long duration,
            boolean isEnableSelfHealing, boolean isTriggerSelfHealing, String healingInfo, int totalTestCases, int passedTestCases) {
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "runningMode", "gui", "executionResult", result, "duration",
                duration, "executionMode", "Sequential", "enable_self_healing", isEnableSelfHealing, "trigger_self_healing",
                isTriggerSelfHealing, "total_test_cases", totalTestCases, "passed_test_cases", passedTestCases
        }));

        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }

        trackUserAction("executeTestSuiteCollection", properties.toArray());
    }

    public static void trackExecuteParallelTestSuiteCollectionInGuiMode(String result, long duration,
            int maxConcurrentInstances, boolean isEnableSelfHealing, boolean isTriggerSelfHealing, String healingInfo, int totalTestCases, int passedTestCases) {
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "runningMode", "gui", "executionResult", result, "duration",
                duration, "executionMode", "Parallel", "maxConcurrent", maxConcurrentInstances,
                "enable_self_healing", isEnableSelfHealing, "trigger_self_healing",
                isTriggerSelfHealing, "total_test_cases", totalTestCases, "passed_test_cases", passedTestCases
        }));

        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }

        trackUserAction("executeTestSuiteCollection", properties.toArray());
    }

    public static void trackExecuteSequentialTestSuiteCollectionInConsoleMode(boolean isAnonymous, String result,
            long duration, boolean isEnableSelfHealing, boolean isTriggerSelfHealing, String healingInfo, int totalTestCases, int passedTestCases) {
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "runningMode", "console", "executionResult", result,
                "duration", duration, "executionMode", "Sequential",
                "enable_self_healing", isEnableSelfHealing, "trigger_self_healing",
                isTriggerSelfHealing, "total_test_cases", totalTestCases, "passed_test_cases", passedTestCases
        }));

        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }

        trackAction("executeTestSuiteCollection", isAnonymous, properties.toArray());
    }

    public static void trackExecuteParallelTestSuiteCollectionInConsoleMode(boolean isAnonymous, String result,
            long duration, int maxConcurrentInstances, boolean isEnableSelfHealing, boolean isTriggerSelfHealing, String healingInfo, int totalTestCases, int passedTestCases) {
        List<Object> properties = new ArrayList<Object>(Arrays.asList(new Object[] {
                "runningMode", "console", "executionResult", result,
                "duration", duration, "executionMode", "Parallel", "maxConcurrent", maxConcurrentInstances,
                "enable_self_healing", isEnableSelfHealing, "trigger_self_healing",
                isTriggerSelfHealing, "total_test_cases", totalTestCases, "passed_test_cases", passedTestCases
        }));

        boolean hasAnySuccessfulSelfHealed = StringUtils.isNotBlank(healingInfo);
        if (hasAnySuccessfulSelfHealed) {
            properties.add("successful_self_healing");
            properties.add(healingInfo);
        }

        trackAction("executeTestSuiteCollection", isAnonymous, properties.toArray());
    }

    public static void trackGenerateCmd() {
        trackUserAction("generateCmd");
    }

    public static void trackQuickOverview(String userClick) {
        trackUserAction("quickOverview", "userClick", userClick);
    }

    public static void trackCreatingObject(String objectType) {
        String action = "new" + StringUtils.capitalize(objectType);
        trackUserAction(action);
    }

    public static void trackCreatingProject(String newProjectId, ProjectType newProjectType) {
        trackUserAction("newProject", "newProjectId", newProjectId, "newProjectType", newProjectType.toString());
    }

    public static void trackCreatingSampleProject(String sampleProjectType, String newProjectId,
            ProjectType newProjectType) {
        trackUserAction("newProject", "sampleProjectType", sampleProjectType, "newProjectId", newProjectId,
                "newProjectType", newProjectType.toString());
    }

    public static void trackCreatingSampleProject(String sampleProjectType) {
        trackUserAction("newProject", "sampleProjectType", sampleProjectType);
    }

    public static void trackOpenDraftRequest(String webServiceType, String openBy) {
        trackUserAction("openDraftRequest", "requestType", webServiceType, "openBy", openBy);
    }

    public static void trackOpenObject(String objectType) {
        String action = "open" + StringUtils.capitalize(objectType);
        trackUserAction(action);
    }
    
    public static void trackSaveObject(String objectType) {
        String action = "save" + StringUtils.capitalize(objectType);
        trackUserAction(action);
    }

    public static void trackOpenHelp(String url) {
        trackUserAction("openHelp", "url", url);
    }

    public static void trackOpenWebSpy() {
        trackUserAction("openWebSpy");
    }
    
    public static void trackOpenMobileSpy(String deviceType) {
        trackUserAction("openMobileSpy", "deviceType", deviceType);
    }
    
    public static void trackOpenWindowsSpy() {
        trackUserAction("openWindowsSpy");
    }
    
    public static void trackSaveWebSpy(int numberOfSavedObjects) {
        trackUserAction("saveWebSpy", "numberOfSavedObjects", numberOfSavedObjects);
    }
    
    public static void trackSaveMobileSpy(String deviceType, int numberOfSavedObjects) {
        trackUserAction("saveMobileSpy", "deviceType", deviceType, "numberOfSavedObjects", numberOfSavedObjects);
    }
    
    public static void trackSaveWindowsSpy(int numberOfSavedObjects) {
        trackUserAction("saveWindowsSpy", "numberOfSavedObjects", numberOfSavedObjects);
    }

    public static void trackCloseWebSpy() {
        trackUserAction("closeWebSpy");
    }
    
    public static void trackCloseMobileSpy(String deviceType) {
        trackUserAction("closeMobileSpy", "deviceType", deviceType);
    }
    
    public static void trackCloseWindowsSpy(boolean isCancelled) {
        trackUserAction("closeWindowsSpy", "isCancelled", isCancelled);
    }

    public static void trackOpenWebRecord(boolean continueRecording, String webLocatorConfig) {
        trackUserAction("openWebRecord", "continue", continueRecording, "webLocatorConfig",
                webLocatorConfig);
    }

    public static void trackOpenMobileRecord(String deviceType) {
        trackUserAction("openMobileRecord", "deviceType", deviceType);
    }
    
    public static void trackOpenWindowsRecord() {
        trackUserAction("openWindowsRecord");
    }
    
    public static void trackOpenWindowsNativeRecord() {
        trackUserAction("openWindowsNativeRecord");
    }
    
    public static void trackOpenSelfHealingInsights() {
        trackUserAction("openSelfHealingInsights");
    }
    
    public static void trackApproveSelfHealingTestObjects(String approvedProposals) {
        trackUserAction("approveSelfHealingProposals", "approvedProposals", approvedProposals);
    }
    
    public static void trackDiscardSelfHealingTestObjects(String discardedProposals) {
        trackUserAction("discardSelfHealingProposals", "discardedProposals", discardedProposals);
    }

    public static void trackClickOnSelfHealingInsightsConfigure() {
        trackUserAction("ksu_self_healing_insights_configure_click");
    }

    public static void trackClickOnSelfHealingInsightsHelp() {
        trackUserAction("ksu_self_healing_insights_help_click");
    }

    public static void trackCloseWebRecordByOk(int numberOfTestSteps, String webLocatorConfig) {
        trackUserAction("closeWebRecord", "isCancelled", false, "numberOfTestSteps",
                String.valueOf(numberOfTestSteps), "webLocatorConfig", webLocatorConfig);
    }
    
    public static void trackCloseWebRecordByCancel(String webLocatorConfig) {
        trackUserAction("closeWebRecord", "isCancelled", true, "webLocatorConfig", webLocatorConfig);
    }
    
    public static void trackCloseMobileRecordByOk(String deviceType, int numberOfTestSteps) {
        trackUserAction("closeMobileRecord", "deviceType", deviceType, "isCancelled", false, "numberOfTestSteps",
                numberOfTestSteps);
    }
    
    public static void trackCloseMobileRecordByCancel(String deviceType) {
        trackUserAction("closeMobileRecord", "deviceType", deviceType, "isCancelled", true);
    }
    
    public static void trackCloseWindowsRecordByOk(int numberOfRecordedSteps) {
        trackUserAction("closeWindowsRecord", "isCancelled", false, "numberOfRecordedSteps", numberOfRecordedSteps);
    }
    
    public static void trackCloseWindowsRecordByCancel() {
        trackUserAction("closeWindowsRecord", "isCancelled", true);
    }
    
    public static void trackCloseWindowsNativeRecordByOk(int numberOfRecordedSteps) {
        trackUserAction("closeWindowsNativeRecord", "isCancelled", false, "numberOfRecordedSteps",
                numberOfRecordedSteps);
    }
    
    public static void trackCloseWindowsNativeRecordByCancel() {
        trackUserAction("closeWindowsNativeRecord", "isCancelled", true);
    }
    
    public static void trackWebRecordRunSteps(String type) {
        trackUserAction("recordRunSteps", "type", type);
    }

    public static void trackImportKeywords(String type) {
        trackUserAction("importKeywords", "type", type);
    }

    public static void trackExportKeywords() {
        trackUserAction("exportKeywords");
    }

    public static void trackForumSearch(String keyword) {
        trackUserAction("forumSearch", "keyword", keyword);
    }

    public static void trackQuickDiscussion() {
        trackUserAction("quickDiscussion");
    }

    public static void trackOpenKAIntegration(String objectType) {
        trackUserAction("openKAIntegration", "type", objectType);
    }

    public static void trackAddNewTestStep(String stepType) {
        trackUserAction("newTestStep", "type", stepType);
    }

    public static void trackTestWebServiceObject(boolean withVerification, boolean isDraftRequest) {
        trackUserAction("testWebServiceObject", "verify", withVerification, "isDraft", isDraftRequest);
    }

    public static void trackAddApiVariable() {
        trackUserAction("addApiVariable");
    }

    public static void trackOpenImportingSwagger() {
        trackUserAction("openImportingSwagger");
    }

    public static void trackOpenImportingPostman() {
        trackUserAction("openImportingPostman");
    }
    
    public static void trackOpenImportingWsdl() {
        trackUserAction("openImportingWSDL");
    }

    public static void trackImportSwagger(String importType) {
        trackUserAction("importSwagger", "type", importType);
    }
    
    public static void trackImportPostman(String importType) {
        trackUserAction("importPostman", "type", importType);
    }

    public static void trackImportWSDL(String importType) {
        trackUserAction("importWSDL", "type", importType);
    }

    public static void trackClickSavingDraftRequest() {
        trackUserAction("clickSavingDraftRequest");
    }

    public static void trackSaveDraftRequest() {
        trackUserAction("saveDraftRequest");
    }

    public static void trackClickDeletingDraftRequest() {
        trackUserAction("clickDeletingDraftRequest");
    }

    public static void trackDeleteDraftRequest(int numberOfDeletedRequests) {
        trackUserAction("deleteDraftRequest", "deletedRequestCount", numberOfDeletedRequests);
    }

    public static void trackClickAddingRequestToTestCase(boolean addToNewTestCase) {
        trackUserAction("clickAddingRequestToTestCase", "addType", addToNewTestCase ? "new" : "existing");
    }

    public static void trackAddRequestToTestCase(boolean addToNewTestCase) {
        trackUserAction("addRequestToTestCase", "addType", addToNewTestCase ? "new" : "existing");
    }

    public static void trackOpenTwitterDialog() {
        trackUserAction("openTwitterDialog");
    }

    public static void trackUserResponseForTwitterDialog(String option) {
        trackUserAction("responseTwitterDialog", "type", option);
    }

    public static void trackOpenLinkedJiraIssuesDialog() {
        trackUserAction("openLinkedJiraIssuesDialog");
    }

    public static void trackClickCreateNewJiraIssue() {
        trackUserAction("clickCreateNewJiraIssue");
    }

    public static void trackClickCreateJiraSubIssue() {
        trackUserAction("clickCreateSubJiraIssue");
    }

    public static void trackClickLinkToExistingJiraIssue() {
        trackUserAction("clickLinkToExistingJiraIssue");
    }

    public static void trackClickHarLink() {
        trackUserAction("clickHarLink");
    }

    public static void trackInAppSurveyRatingAndIdea(int numberOfStars, String userIdea) {
        trackUserAction("katalonStudioSurvey", "star", numberOfStars, "content", userIdea);
    }

    public static void trackInAppSurveyWillContinueToUse(boolean willContinueToUse, String quitUsingReason) {
        trackUserAction("willContinueToUseSurvey", "willContinueToUse", willContinueToUse, "quitUsingReason", quitUsingReason);
    }
    
    public static void trackClickWalkthroughDialogLink(String dialogId, String linkName, String link){
    	trackUserAction("clickWalkthroughLink", "dialogId", dialogId, "linkName", linkName, "link", link);
    }
    
    public static void trackClickWalkthroughIgnoreButton(String dialogId){
    	trackUserAction("clickWalkthroughIgnoreButton", "dialogId", dialogId);
    }

    public static void trackDownloadPlugin(String apiKey, long pluginId, String pluginName, String pluginVersion,
            RunningMode runningMode) {
        apiKey = StringUtils.isNotBlank(apiKey) ? apiKey : StringUtils.EMPTY;
        trackUserAction("downloadPlugin", "apiKey", apiKey, "pluginId", pluginId, "pluginName", pluginName,
                    "pluginVersion", pluginVersion, "runningMode", runningMode.toString());
    }
    
    public static void trackInstallPlugins(List<Long> installedPluginIds, String apiKey, RunningMode runningMode) {
        trackUserAction("installPlugins", "pluginIdList", JsonUtil.toJson(installedPluginIds), "apiKey",
                StringUtils.isNotBlank(apiKey) ? apiKey : StringUtils.EMPTY, "runningMode", runningMode.toString());
    }
    
    public static void trackUsePrivatePlugins(int customKeywordPluginCount, int idePluginCount) {
        trackUserAction("usePrivatePlugin", "customKeywordPluginsCount", customKeywordPluginCount, "idePluginsCount", idePluginCount);
    }
    
    public static void trackOpenExportTestArtifactsDialog() {
        trackUserAction("openExportTestArtifactsDialog");
    }
    
    public static void trackOpenImportTestArtifactsDialog() {
        trackUserAction("openImportTestArtifactsDialog");
    }

    public static void trackOpenConsoleView() {
        trackUserAction("openConsoleView");
    }
    
    public static void trackGitOperation(String operationName, String protocol) {
        trackUserAction("performGitOperation", "operationName", operationName, "protocol", protocol);
    }
    
    public static void trackUseSourceCodeForDebugging(String className) {
        trackUserAction("useSourceCodeForDebugging", "className", className);
    }
    
    public static void trackUseDatabaseConnectionForEnterpriseAccount(String dataSourceType) {
        trackUserAction("useDatabaseConnectionForEnterpriseAccount", "type", dataSourceType);
    }
    
    public static void trackUseAdditionalTestDataSource() {
        trackUserAction("useAdditionalTestDataSource");
    }
    
    public static void trackUnauthorizedAccessOfKSEFeatures(KSEFeature feature) {
        trackUserAction("unauthorizedAccessKSEFeature", "featureKey", feature.toString());
    }
    
    public static void trackOpenKSEBrochurePage() {
        trackUserAction("openKSEBrochurePage");
    }

    public static void trackFailedToSpyRecordDueToOutdatedChromeDriver() {
        trackUserAction("failedToSpyRecordDueToOutdatedChromeDriver");
    }
    
    public static void trackFailedToSpyRecordDueToOutdatedEdgeChromiumDriver() {
        trackUserAction("failedToSpyRecordDueToOutdatedEdgeChromiumDriver");
    }
    
    public static void trackUseAutoUpdateChromeDriver() {
        trackUserAction("autoUpdateChromeDriver");
    }

    public static void trackUseAutoUpdateEdgeChromiumDriver() {
        trackUserAction("autoUpdateEdgeChromiumDriver");
    }
    
    public static void trackClickOnExceptionDocInLogViewer(String link) {
        trackUserAction("clickOnExceptionLinkInLogViewer", "link", link);
    }
    
    private static void trackUserAction(String actionName, Object... properties) {
        trackAction(actionName, false, properties);
    }

    private static void trackAction(String actionName, boolean isAnonymous, Object... properties) {
        JsonObject propertiesObject = new JsonObject();
        propertiesObject.addProperty("action", actionName);

        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject != null) {
            propertiesObject.addProperty("projectId", currentProject.getUUID());
            propertiesObject.addProperty("projectType", currentProject.getType().toString());
        }
        
        KatalonPackage katalonPackage = KatalonApplication.getKatalonPackage();
        propertiesObject.addProperty("katalonPackage", katalonPackage.getPackageName());
        
        LicenseType licenseType = ActivationInfoCollector.getLicenseType();
        propertiesObject.addProperty("licenseType", licenseType.name());

        if (properties != null) {
            JsonUtil.mergeJsonObject(createJsonObject(properties), propertiesObject);
        }

        TrackInfo trackInfo = TrackInfo.create().eventName(TrackEvents.KATALON_STUDIO_USED).anonymous(isAnonymous)
                .properties(propertiesObject);

        trackingService.track(trackInfo);
    }

    private static JsonObject createJsonObject(Object... properties) {
        JsonObject jsonObject = new JsonObject();

        if (properties != null) {
            for (int i = 0; i < properties.length - 1; i += 2) {
                String key = (String) properties[i];
                Object value = properties[i + 1];

                if (value instanceof Character) {
                    jsonObject.addProperty(key, (Character) value);
                } else if (value instanceof String) {
                    jsonObject.addProperty(key, (String) value);
                } else if (value instanceof Number) {
                    jsonObject.addProperty(key, (Number) value);
                } else if (value instanceof Boolean) {
                    jsonObject.addProperty(key, (Boolean) value);
                }
            }
        }

        return jsonObject;
    }
    
    public static void trackClickOnTrialNotification(String message) {
        trackUserAction("clickOnTrialNotification", "message", message);
    }
    
    public static void trackClickOnTrialNotificationButton() {
        trackUserAction("clickOnTrialNotificationButton");
    }

    public static void trackClickImportSeleniumIde() {
        trackUserAction("clickImportSeleniumIde");
    }

    public static void trackImportSeleniumIdeResult(int numTestCases, int numTestSuites) {
        trackUserAction("importSeleniumIdeResult", "numOfImportedTestCases", numTestCases, "numOfImportedTestSuites",
                numTestSuites);
    }
}
