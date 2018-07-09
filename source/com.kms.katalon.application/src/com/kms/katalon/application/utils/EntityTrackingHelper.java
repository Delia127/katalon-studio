package com.kms.katalon.application.utils;

import java.util.concurrent.Executors;

import com.kms.katalon.constants.UsagePropertyConstant;
import com.kms.katalon.logging.LogUtil;

public class EntityTrackingHelper {
    public static void trackProjectCreated() {
        trackEntityCreated(UsagePropertyConstant.KEY_NUM_PROJECT_CREATED);
    }
    
    public static void trackTestCaseCreated() {
        trackEntityCreated(UsagePropertyConstant.KEY_NUM_TEST_CASE_CREATED);
    }

    private static void trackEntityCreated(String key) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                int previousNumOfEntityCreated = 0;
                try {
                    previousNumOfEntityCreated = Integer.parseInt(ApplicationInfo.getAppProperty(key));
                } catch (NumberFormatException e) {
                    LogUtil.logError(e);
                }
                ApplicationInfo.setAppProperty(key, String.valueOf(previousNumOfEntityCreated + 1), true);
            }
        });
    }
}
