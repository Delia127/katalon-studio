package com.kms.katalon.composer.windows.menu;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class WindowsExecutionDebugMenuContribution extends WindowsExecutionMenuContribution {
    @Override
    protected LaunchMode getLaunchMode() {
        return LaunchMode.DEBUG;
    }
}
