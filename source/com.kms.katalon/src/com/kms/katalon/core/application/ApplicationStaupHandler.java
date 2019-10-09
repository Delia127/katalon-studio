package com.kms.katalon.core.application;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.activation.dialog.ExpiredLicenseDialog;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.core.TrackingManager;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class ApplicationStaupHandler {

    private static IEventBroker eventBroker;

    private static ExpiredLicenseDialog expiredDialog;

    public static boolean checkActivation() throws Exception {
        eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
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

        expiredDialog = null;
        scheduleCheckLicense();

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

    public static void scheduleCheckLicense() {
        ActivationInfoCollector.scheduleCheckLicense(() -> {
            UISynchronizeService.syncExec(() -> {
                if (expiredDialog == null) {
                    expiredDialog = new ExpiredLicenseDialog(Display.getCurrent().getActiveShell());
                    expiredDialog.open();
                    closeKSAfter(300);
                }
            });
        }, () -> {
            ActivationInfoCollector.checkAndMarkActivatedForGUIMode();
        });
    }

    public static void closeKSAfter(long seconds) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                UISynchronizeService.syncExec(() -> {
                    eventBroker.send(EventConstants.PROJECT_CLOSE, null);
                    PlatformUI.getWorkbench().close();
                });
            } catch (Exception e) {
                LogUtil.logError(e, "Error when closing Katalon Studio");
            }
        }, seconds, 5, TimeUnit.SECONDS);
    }
}
