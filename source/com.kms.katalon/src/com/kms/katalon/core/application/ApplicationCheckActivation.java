package com.kms.katalon.core.application;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.tracking.core.TrackingManager;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class ApplicationCheckActivation {
    
    public static boolean checkActivation(final IEventBroker eventBroker) throws Exception {
        // if (VersionUtil.isInternalBuild()) {
        // return true;
        // }
        if (!(ComposerActivationInfoCollector.checkActivation())) {
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);
            PlatformUI.getWorkbench().close();
            return false;
        }

        // Executors.newSingleThreadExecutor().submit(() -> UsageInfoCollector
        // .collect(UsageInfoCollector.getActivatedUsageInfo(UsageActionTrigger.OPEN_APPLICATION,
        // RunningMode.GUI)));
        // sendEventForTracking();
        try {
            Trackings.trackOpenApplication(false, "gui");
        } catch (Exception ignored) {

        }

        return true;
    }

    public static void scheduleCollectingStatistics() {
        int trackingTime = TrackingManager.getInstance().getTrackingTime();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            Trackings.trackProjectStatistics(ProjectController.getInstance().getCurrentProject(),
                    !ActivationInfoCollector.isActivated(), "gui");
        }, trackingTime, trackingTime, TimeUnit.SECONDS);
    }
}
