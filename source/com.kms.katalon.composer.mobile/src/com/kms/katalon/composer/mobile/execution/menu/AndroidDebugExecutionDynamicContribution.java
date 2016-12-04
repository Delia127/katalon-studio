package com.kms.katalon.composer.mobile.execution.menu;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class AndroidDebugExecutionDynamicContribution extends AndroidExecutionDynamicContribution {
    @Override
    protected LaunchMode getLaunchMode() {
        return LaunchMode.DEBUG;
    }
}
