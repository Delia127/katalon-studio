package com.kms.katalon.core.application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.activation.dialog.ExpiredLicenseDialog;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.license.models.License;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.core.TrackingManager;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class ApplicationStaupHandler {
    
    private static IEventBroker eventBroker;
    
    private static ScheduledFuture<?> checkLicenseTask;
    
    private static ScheduledFuture<?> closeAppTask;
    
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
        
//        scheduleCheckLicense();
        
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
        checkLicenseTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            UISynchronizeService.syncExec(() -> {
                try {
                    StringBuilder errorMessage = new StringBuilder();
                    String jwsCode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE);
                    License license = ActivationInfoCollector.parseLicense(jwsCode, errorMessage);
                    
                    if (license == null || ActivationInfoCollector.isExpired(license)) {
                        closeKSAfter(30);
                        ExpiredLicenseDialog dialog = new ExpiredLicenseDialog(Display.getCurrent().getActiveShell());
                        dialog.open();
                        closeKS();
                    } else if (ActivationInfoCollector.isReachRenewTime(license)) {
                        ActivationInfoCollector.checkAndMarkActivatedForGUIMode();
                    }
                } catch (Exception e) {
                    LogUtil.logError(e, "Error when checking license");
                }
            });
        }, 0, 5, TimeUnit.SECONDS);
    }
    
    public static void closeKSAfter(long seconds) {
        closeAppTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                UISynchronizeService.syncExec(() -> {
                    closeKS();
                });
            } catch(Exception e) {
                LogUtil.logError(e, "Error when closing Katalon Studio");
            }
        }, seconds, 5, TimeUnit.SECONDS);
    }
    
    public static void closeKS() {
        UISynchronizeService.syncExec(() -> {
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);
            PlatformUI.getWorkbench().close(); 
            cleanupTask();
        });
    }
    
    private static void cleanupTask() {
        if (checkLicenseTask != null) {
            checkLicenseTask.cancel(false);
        }
        if (closeAppTask != null) {
            closeAppTask.cancel(false);
        }
    }
}
