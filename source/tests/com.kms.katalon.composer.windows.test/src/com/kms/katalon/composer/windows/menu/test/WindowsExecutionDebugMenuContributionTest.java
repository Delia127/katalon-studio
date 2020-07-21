package com.kms.katalon.composer.windows.menu.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;

import com.kms.katalon.composer.windows.menu.WindowsExecutionDebugMenuContribution;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class WindowsExecutionDebugMenuContributionTest {
    @Test
    @Ignore
    public void getLauchModeTest() {
        WindowsExecutionDebugMenuContribution menuContribution = new WindowsExecutionDebugMenuContribution();
        LaunchMode launchMode = menuContribution.getLaunchMode();
        assertEquals(LaunchMode.DEBUG, launchMode);
    }
}
