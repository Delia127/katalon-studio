package com.kms.katalon.console.application;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.equinox.app.IApplicationContext;
import org.greenrobot.eventbus.EventBus;

import com.kms.katalon.application.ApplicationStarter;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.console.constants.ConsoleMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.event.EventBusSingleton;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.core.TrackingManager;
import com.kms.katalon.tracking.service.Trackings;

public class ConsoleApplicationStarter implements ApplicationStarter {

    private void init() {
        System.out.println("> ConsoleApplicationStarter.java > init()");
        EventBusSingleton.getInstance().setEventBus(EventBus.builder().installDefaultEventBus());
        
        int trackingTime = TrackingManager.getInstance().getTrackingTime();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            Trackings.trackProjectStatistics(ProjectController.getInstance().getCurrentProject(), 
                    !ActivationInfoCollector.isActivated(), "console");
        }, trackingTime, trackingTime, TimeUnit.SECONDS);
        System.out.println("> ConsoleApplicationStarter.java > init() done");
    }

    @Override
    public int start(String[] arguments) {
        System.out.println("> ConsoleApplicationStarter.java > start()");
        System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, "");
        try {
            init();
            System.out.println("> ConsoleApplicationStarter.java > before ConsoleMain.launch(...)");
            return ConsoleMain.launch(arguments);
        } catch (Exception e) {
            LogUtil.printAndLogError(e, ConsoleMessageConstants.ERR_CONSOLE_MODE);
            return LauncherResult.RETURN_CODE_ERROR;
        }
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

}
