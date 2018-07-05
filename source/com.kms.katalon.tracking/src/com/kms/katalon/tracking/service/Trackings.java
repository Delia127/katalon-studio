package com.kms.katalon.tracking.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ProjectStatistics;
import com.kms.katalon.tracking.model.TrackInfo;
import com.kms.katalon.tracking.osgi.service.IProjectStatisticsCollector;
import com.kms.katalon.tracking.osgi.service.ServiceConsumer;

public class Trackings {
    
    private static TrackingService trackingService = new TrackingService();
    
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    public static void trackOpenApplication(ProjectEntity project, boolean isAnonymous) {
        runAsync(() -> {
            try {
                IProjectStatisticsCollector collector = ServiceConsumer.getProjectStatisticsCollector();
                
                ProjectStatistics statistics = collector.collect(project);
                
                JsonObject statisticsObject = JsonUtil.toJsonObject(statistics);
                
                JsonObject properties = new JsonObject();
                properties.addProperty("triggeredBy", "openApplication");
                JsonUtil.mergeJsonObject(statisticsObject, properties);
                
                TrackInfo trackInfo = TrackInfo
                        .create()
                        .eventName(TrackEvents.KATALON_STUDIO_TRACK)
                        .anonymous(isAnonymous)
                        .properties(properties);
                
                System.out.println(properties);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    public static void trackQuickOverview(String userClick) {
        runAsync(() -> {
            trackAction(
                    "quickOverview", 
                    "userClick", userClick);
        });
    }
    
    public static void trackCreatingObject(String objectType) {
        runAsync(() -> {
            trackAction(
                "newObject",
                "type", objectType);
        });
    }
    
    public static void trackCreatingProject() {
        runAsync(() -> {
            trackCreatingObject("project");
        });
    }
    
    public static void trackCreatingSampleProject(String sampleProjectType, String projectId) {
        runAsync(() -> {
            trackAction(
                "newObject",
                "type", "project",
                "sampleProjectType", sampleProjectType,
                "projectId", projectId);
        });
    }
    
    public static void trackOpenObject(String objectType) {
        runAsync(() -> {
            trackAction(
                "openObject",
                "type", objectType);
        });
    }
    
    public static void trackOpenHelp(String url) {
        runAsync(() -> {
            trackAction(
                    "openObject",
                    "type", "help",
                    "url", url);
        });
    }
    
    public static void trackOpenSpy(String type) {
        trackAction(
                "openSpy",
                "type", type);
    }
    
    public static void trackSaveSpy(String type, int numberOfSavedObjects) {
        JsonObject propertiesObject = new JsonObject();
        propertiesObject.addProperty("action", "saveSpy");
        propertiesObject.addProperty("type", type);
        propertiesObject.addProperty("numberOfSavedObjects", numberOfSavedObjects);
        
        TrackInfo trackInfo = TrackInfo
                .create()
                .eventName(TrackEvents.KATALON_STUDIO_USED)
                .properties(propertiesObject);
        
        trackingService.track(trackInfo);
    }
    
    public static void trackCloseSpy(String type) {
        trackAction(
                "closeSpy",
                "type", type);
    }
    
    public static void trackOpenRecord(String type) {
        trackAction(
                "openRecord",
                "type", type);
    }
    
    private static void runAsync(Runnable task) {
        executorService.submit(task);
    }
    
    private static void trackAction(String actionName, String... properties) {
        JsonObject propertiesObject = new JsonObject();
        propertiesObject.addProperty("action", actionName);
        if (properties != null) {
            JsonUtil.mergeJsonObject(createJsonObject(properties), propertiesObject);
        }
        
        TrackInfo trackInfo = TrackInfo
                .create()
                .eventName(TrackEvents.KATALON_STUDIO_USED)
                .properties(propertiesObject);
        
        trackingService.track(trackInfo);
    }
    
    private static JsonObject createJsonObject(String... properties) {
        JsonObject jsonObject = new JsonObject();
        
        if (properties != null) {
            for (int i = 0; i < properties.length - 1; i += 2) {
                String key = (String)properties[i];
                String value = properties[i + 1];
                
                jsonObject.addProperty(key, value);
            }
        }

        return jsonObject;
    }
}
