
package com.kms.katalon.composer.webui.execution.menu;

import com.kms.katalon.execution.launcher.model.LaunchMode;

public class EdgeChromiumDebugExecutionDynamicContribution extends EdgeChromiumExecutionDynamicContribution {
    @Override
    protected LaunchMode getLaunchMode() {
        return LaunchMode.DEBUG;
    }
}