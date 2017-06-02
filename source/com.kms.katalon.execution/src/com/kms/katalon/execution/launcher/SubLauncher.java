package com.kms.katalon.execution.launcher;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;

public interface SubLauncher extends ILauncher {
    RunConfigurationDescription getRunConfigurationDescription();
}
