package com.kms.katalon.tracking.event.subscriber;

import java.util.Map;

import com.google.gson.JsonObject;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.collector.ProjectStatisticsCollector;
import com.kms.katalon.tracking.collector.ProjectStatisticsCollector.ProjectStatistics;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ISentEventInfo;

public class KatalonStudioTrackEventSubscriber extends AbstractTrackingEventSubscriber {
    
    @Override
    protected boolean accept(TrackingEvent event) {
        return event.getTrigger() == UsageActionTrigger.OPEN_APPLICATION
            || event.getTrigger() == UsageActionTrigger.COLLECT_STATISTICS;
    }

    @Override
    protected ISentEventInfo getSentEventInfo() {
        Map<String, Object> properties = (Map<String, Object>) trackingEvent.getData();
        boolean isAnonymous = (boolean) properties.get("isAnonymous");
        String runningMode = (String) properties.get("runningMode");
        
        ProjectStatistics statistics = collectProjectStatistics();
        
        KatalonStudioTrackEventInfo eventInfo = new KatalonStudioTrackEventInfo();
        eventInfo.setAnonymous(isAnonymous);
        eventInfo.setRunningMode(runningMode);
        eventInfo.setProjectStatistics(statistics);
        
        return eventInfo;
    }
    
    private ProjectStatistics collectProjectStatistics() {
        try {

            ProjectEntity project = ProjectController.getInstance().getCurrentProject();

            if (project != null) {
                ProjectStatisticsCollector collector = new ProjectStatisticsCollector(project);

                ProjectStatistics projectStatistics = collector.collect();

                return projectStatistics;
            } else {
                return null;
            }
        } catch (Exception e) {
            LogUtil.logError(e);
            return null;
        }
    }
    
    private class KatalonStudioTrackEventInfo implements ISentEventInfo {
        
        private boolean isAnonymous = false;
        
        private ProjectStatistics projectStatistics;

        private String runningMode;
        
        @Override
        public String getEventName() {
            return TrackEvents.KATALON_STUDIO_TRACK;
        }

        @Override
        public boolean isAnonymous() {
            return isAnonymous;
        }
        
        public void setAnonymous(boolean isAnonymous) {
            this.isAnonymous = isAnonymous;
        }
        
        public String getRunningMode() {
            return runningMode;
        }
        
        public void setRunningMode(String runningMode) {
            this.runningMode = runningMode;
        }

        @Override
        public JsonObject getPropertiesObject() {
            JsonObject properties = JsonUtil.toJsonObject(projectStatistics);
            properties.addProperty("triggeredBy", trackingEvent.getTrigger().getAction());
            properties.addProperty("runningMode", runningMode);
            return properties;
        }
        
        public void setProjectStatistics(ProjectStatistics projectStatistics) {
            this.projectStatistics = projectStatistics;
        }
    }
}
