package com.kms.katalon.tracking.service;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.model.TrackInfo;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;
import com.kms.katalon.tracking.osgi.service.ServiceConsumer;
import com.kms.katalon.util.SystemInformationUtil;

public class Trackings {

    private static TrackingService trackingService = new TrackingService();
    private static SystemInformationUtil system = new SystemInformationUtil();

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

    public static void trackSpy(String type) {
        trackUserAction("spy", "type", type);
    }

    public static void trackWebRecord(WebUIDriverType browserType, boolean useActiveBrowser,
            SelectorMethod webLocatorConfig) {
        trackUserAction("record", "type", "web", "browserType", browserType.toString(), "active", useActiveBrowser,
                "webLocatorConfig", webLocatorConfig.toString());
    }

    public static void trackRecord(String type) {
        trackUserAction("record", "type", type);
    }

    public static void trackExecuteTestCase(String launchMode, String driverType, String result, long duration) {
        trackUserAction("executeTestCase", "launchMode", launchMode, "driver", driverType,"executionResult", result, "duration", duration);
    }

    public static void trackExecuteTestSuiteInGuiMode(String launchMode, String driverType, String result, long duration) {
        trackUserAction("executeTestSuite", "runningMode", "gui", "launchMode", launchMode, "driver", driverType, "executionResult", result, "duration", duration);
    }

    public static void trackExecuteTestSuiteInConsoleMode(boolean isAnonymous, String driverType, String result, long duration) {
        trackAction("executeTestSuite", isAnonymous, "runningMode", "console", "driver", driverType, "executionResult", result, "duration", duration);
    }
    
    public static void trackExecuteTestSuiteCollectionInGuiMode(String result, long duration) {
        trackUserAction("executeTestSuiteCollection", "runningMode", "gui", "executionResult", result, "duration", duration);
    }

    public static void trackExecuteTestSuiteCollectionInConsoleMode(boolean isAnonymous, String result, long duration) {
        trackAction("executeTestSuiteCollection", isAnonymous, "runningMode", "console", "executionResult", result, "duration", duration);
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

    public static void trackOpenHelp(String url) {
        trackUserAction("openHelp", "url", url);
    }

    public static void trackOpenSpy(String type) {
        trackUserAction("openSpy", "type", type);
    }

    public static void trackSaveSpy(String type, int numberOfSavedObjects) {
        trackUserAction("saveSpy", "type", type, "numberOfSavedObjects", Integer.valueOf(numberOfSavedObjects));
    }

    public static void trackCloseSpy(String type) {
        trackUserAction("closeSpy", "type", type);
    }

    public static void trackOpenWebRecord(Boolean continueRecording, SelectorMethod webLocatorConfig) {
        if (continueRecording != null) {
            trackUserAction("openRecord", "type", "web", "continue", continueRecording ? "yes" : "no",
                    "webLocatorConfig", webLocatorConfig.toString());
        } else {
            trackUserAction("openRecord", "type", "web", "webLocatorConfig", webLocatorConfig.toString());
        }
    }

    public static void trackOpenMobileRecord() {
        trackUserAction("openRecord", "type", "mobile");
    }

    public static void trackCloseWebRecord(String closeButton, int numberOfTestSteps, SelectorMethod webLocatorConfig) {
        if ("ok".equals(closeButton)) {
            trackUserAction("closeRecord", "type", "web", "closePopup", closeButton, "numberOfTestSteps",
                    String.valueOf(numberOfTestSteps), "webLocatorConfig", webLocatorConfig.toString());
        } else {
            trackUserAction("closeRecord", "type", "web", "closePopup", closeButton, "webLocatorConfig",
                    webLocatorConfig.toString());
        }
    }

    public static void trackCloseRecord(String type, String closeButton, int numberOfTestSteps) {
        if ("ok".equals(closeButton)) {
            trackUserAction("closeRecord", "type", type, "closePopup", closeButton, "numberOfTestSteps",
                    String.valueOf(numberOfTestSteps));
        } else {
            trackUserAction("closeRecord", "type", type, "closePopup", closeButton);
        }
    }

    public static void trackRecordRunSteps(String type) {
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
    
    public static void trackClickWalkthroughDialogLink(String dialogId, String linkName, String link){
    	trackUserAction("clickWalkthroughLink", "dialogId", dialogId, "linkName", linkName, "link", link);
    }
    
    public static void trackClickWalkthroughIgnoreButton(String dialogId){
    	trackUserAction("clickWalkthroughIgnoreButton", "dialogId", dialogId);
    }
    
    public static void trackUserProfile(String userRole, String usage) {
        trackUserAction("userProfile", "role", userRole, "usage", usage);
    }

    public static void trackDownloadPlugin(String apiKey, long pluginId, String pluginName, String pluginVersion,
            RunningMode runningMode) {
        apiKey = StringUtils.isNotBlank(apiKey) ? apiKey : StringUtils.EMPTY;
        trackUserAction("downloadPlugin", "apiKey", apiKey, "pluginId", pluginId, "pluginName", pluginName,
                    "pluginVersion", pluginVersion, "runningMode", runningMode.toString());
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
}
