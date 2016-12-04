package com.kms.katalon.composer.integration.kobiton.menu;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class KobitonDebugExecutionDynamicContribution extends KobitonExecutionDynamicContribution {
    @Override
    protected LaunchMode getLaunchMode() {
        return LaunchMode.DEBUG;
    }
}
