package com.kms.katalon.plugin.service;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.logging.LogUtil;

public class LogService {
    
    private static LogService instance;
    
    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }
    
    private LogService() {}

    public void logInfo(String message) {
        if (ApplicationRunningMode.get() == RunningMode.GUI) {
            LoggerSingleton.logInfo(message);
        } else {
            LogUtil.printOutputLine(message);
        }
    }
    
    public void logError(Throwable e) {
        if (ApplicationRunningMode.get() == RunningMode.GUI) {
            LoggerSingleton.logError(e);
        } else {
            LogUtil.printAndLogError(e);
        }
    }
}
