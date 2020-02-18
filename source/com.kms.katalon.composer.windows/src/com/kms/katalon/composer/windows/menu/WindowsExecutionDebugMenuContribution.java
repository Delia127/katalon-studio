package com.kms.katalon.composer.windows.menu;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class WindowsExecutionDebugMenuContribution extends WindowsExecutionMenuContribution {
    @Override
    public LaunchMode getLaunchMode() {
        return LaunchMode.DEBUG;
    }
}
