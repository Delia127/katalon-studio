package com.kms.katalon.tracking.service;

import com.google.gson.JsonObject;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.model.TrackInfo;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;
import com.kms.katalon.tracking.osgi.service.ServiceConsumer;

public class Trackings {
    
    private static TrackingService trackingService = new TrackingService();
    
    public static void trackOpenApplication(ProjectEntity project, boolean isAnonymous, String runningMode) {
        trackUsageData(project, isAnonymous, runningMode, "openApplication");
    }
    
    public static void trackProjectStatistics(ProjectEntity project, boolean isAnonymous, String runningMode) {
        trackUsageData(project, isAnonymous, runningMode, "collectStatistics");
    }
    
    private static void trackUsageData(ProjectEntity project, 
            boolean isAnonymous,
            String runningMode,
            String triggeredBy) {
        
        if (project == null) {
            return;
        }
        
        try {
            IProjectStatisticsCollector collector = ServiceConsumer.getProjectStatisticsCollector();
            
            ProjectStatistics statistics = collector.collect(project);
            
            JsonObject statisticsObject = JsonUtil.toJsonObject(statistics);
            
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
    
    
    
    public static void trackOpenFirstTime() {
        TrackInfo trackInfo = TrackInfo
                .create()
                .eventName(TrackEvents.KATALON_OPEN_FIRST_TIME)
                .anonymous(true);
        
        trackingService.track(trackInfo);
    }
    
    public static void trackSave() {
        trackUserAction("save");
    }
    
    public static void trackSaveAll() {
        trackUserAction("saveAll");
    }
    
    public static void trackSpy(String type) {
        trackUserAction("spy", "type", type);
    }

    public static void trackRecord(String type) {
        trackUserAction("record", "type", type);
    }
    
    public static void trackExecuteTestCase(String launchMode) {
        trackUserAction("execute", "objectType", "testCase", "launchMode", launchMode);
    }
    
    public static void trackExecuteTestSuiteInGuiMode(String launchMode) {
        trackUserAction("execute", "objectType", "testSuite", "runningMode", "gui", "launchMode", launchMode);
    }
    
    public static void trackExecuteTestSuiteInConsoleMode(boolean isAnonymous) {
        trackAction("execute", isAnonymous, "objectType", "testSuite", "runningMode", "console");
    }
    
    public static void trackExecuteTestSuiteCollectionInGuiMode() {
        trackUserAction("execute", "objectType", "testSuiteCollection", "runningMode", "gui");
    }
    
    public static void trackExecuteTestSuiteCollectionInConsoleMode(boolean isAnonymous) {
        trackAction("execute", isAnonymous, "objectType", "testSuiteCollection", "runningMode", "console");
    }
    
    public static void trackGenerateCmd() {
        trackUserAction("generateCmd");
    }
    
    public static void trackQuickOverview(String userClick) {
        trackUserAction("quickOverview", "userClick", userClick);
    }
    
    public static void trackCreatingObject(String objectType) {
        trackUserAction("newObject", "type", objectType);
    }
    
    public static void trackCreatingProject() {
        trackCreatingObject("project");
    }
    
    public static void trackCreatingSampleProject(String sampleProjectType, String projectId) {
        trackUserAction(
                "newObject", "type", "project", "sampleProjectType", sampleProjectType, "projectId", projectId);
    }
    
    public static void trackCreatingSampleProject(String sampleProjectType) {
        trackUserAction(
                "newObject", "type", "project", "sampleProjectType", sampleProjectType);
    }
    
    public static void trackOpenObject(String objectType) {
        trackUserAction("openObject", "type", objectType);
    }
    
    public static void trackOpenHelp(String url) {
        trackUserAction("openObject", "type", "help", "url", url);
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
        
    private static void trackUserAction(String actionName, Object... properties) {
        trackAction(actionName, false, properties);
    }
    
    private static void trackAction(String actionName, boolean isAnonymous, Object... properties) {
        JsonObject propertiesObject = new JsonObject();
        propertiesObject.addProperty("action", actionName);
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
