package com.kms.katalon.tracking.service;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.model.TrackInfo;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;
import com.kms.katalon.tracking.osgi.service.ServiceConsumer;

public class Trackings {
    
    private static TrackingService trackingService = new TrackingService();
    
    public static void trackOpenApplication(boolean isAnonymous, String runningMode) {
        trackAction("openApplication", isAnonymous, "runningMode", runningMode);
    }
    
    public static void trackProjectStatistics(ProjectEntity project, boolean isAnonymous, String runningMode) {
        trackUsageData(project, isAnonymous, runningMode, "collectStatistics");
    }
    
    public static void trackOpenProject(ProjectEntity project) {
        trackUsageData(project, false, "gui", "openProject");
        trackOpenObject("project");
    }
    
    private static void trackUsageData(ProjectEntity project, 
            boolean isAnonymous,
            String runningMode,
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
            
            TrackInfo trackInfo = TrackInfo
                    .create()
                    .eventName(TrackEvents.KATALON_STUDIO_TRACK)
                    .anonymous(isAnonymous)
                    .properties(properties);
            
            trackingService.track(trackInfo);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }
    
    private static JsonObject collectProjectStatistics(ProjectEntity project) throws Exception {
        IProjectStatisticsCollector collector = ServiceConsumer.getProjectStatisticsCollector();
        
        ProjectStatistics statistics = collector.collect(project);
        
        JsonObject statisticsObject = JsonUtil.toJsonObject(statistics);
        
        return statisticsObject;
    }
    
    public static void trackOpenFirstTime() {
        TrackInfo trackInfo = TrackInfo
                .create()
                .eventName(TrackEvents.KATALON_OPEN_FIRST_TIME)
                .anonymous(true);
        
        trackingService.track(trackInfo);
    }
    
    public static void trackSpy(String type) {
        trackUserAction("spy", "type", type);
    }

    public static void trackWebRecord(WebUIDriverType browserType, boolean useActiveBrowser) {
        trackUserAction("record", "type", "web", "browserType", browserType.toString(), "active", useActiveBrowser);
    }
    
    public static void trackRecord(String type) {
        trackUserAction("record", "type", type);
    }
    
    public static void trackExecuteTestCase(String launchMode, String driverType) {
        trackUserAction("executeTestCase", "launchMode", launchMode, "driver", driverType);
    }
    
    public static void trackExecuteTestSuiteInGuiMode(String launchMode, String driverType) {
        trackUserAction("executeTestSuite", "runningMode", "gui", "launchMode", launchMode, "driver", driverType);
    }
    
    public static void trackExecuteTestSuiteInConsoleMode(boolean isAnonymous, String driverType) {
        trackAction("executeTestSuite", isAnonymous, "runningMode", "console", "driver", driverType);
    }
    
    public static void trackExecuteTestSuiteCollectionInGuiMode() {
        trackUserAction("executeTestSuiteCollection", "runningMode", "gui");
    }
    
    public static void trackExecuteTestSuiteCollectionInConsoleMode(boolean isAnonymous) {
        trackAction("executeTestSuiteCollection", isAnonymous, "runningMode", "console");
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
    
    public static void trackCreatingProject() {
        trackUserAction("newProject");
    }
    
    public static void trackCreatingSampleProject(String sampleProjectType, String newProjectId) {
        trackUserAction(
                "newProject", "sampleProjectType", sampleProjectType, "newProjectId", newProjectId);
    }
    
    public static void trackCreatingSampleProject(String sampleProjectType) {
        trackUserAction(
                "newProject", "sampleProjectType", sampleProjectType);
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
    
    public static void trackOpenWebRecord(Boolean continueRecording) {
        if (continueRecording != null) {
            trackUserAction("openRecord", "type", "web", "continue", continueRecording ? "yes" : "no");
        } else {
            trackUserAction("openRecord", "type", "web");
        }
    }
    
    public static void trackOpenMobileRecord() {
        trackUserAction("openRecord", "type", "mobile");
    }
    
    public static void trackCloseRecord(String type, String closeButton, int numberOfTestSteps) {
        if ("ok".equals(closeButton)) {
            trackUserAction("closeRecord", "type", type, "closePopup", closeButton, "numberOfTestSteps", String.valueOf(numberOfTestSteps));
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
    
    public static void trackTestWebServiceObject(boolean withVerification) {
        trackUserAction("testWebServiceObject", "verify", withVerification);
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
        }
        
        if (properties != null) {
            JsonUtil.mergeJsonObject(createJsonObject(properties), propertiesObject);
        }
        
        TrackInfo trackInfo = TrackInfo
                .create()
                .eventName(TrackEvents.KATALON_STUDIO_USED)
                .anonymous(isAnonymous)
                .properties(propertiesObject);
        
        trackingService.track(trackInfo);
    }
    
    private static JsonObject createJsonObject(Object... properties) {
        JsonObject jsonObject = new JsonObject();
        
        if (properties != null) {
            for (int i = 0; i < properties.length - 1; i += 2) {
                String key = (String)properties[i];
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
