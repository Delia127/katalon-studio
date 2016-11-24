package com.kms.katalon.composer.mobile.execution.menu;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class IosDebugExecutionDynamicContribution extends IosExecutionDynamicContribution {
    @Override
    protected LaunchMode getLaunchMode() {
        return LaunchMode.DEBUG;
    }
}
