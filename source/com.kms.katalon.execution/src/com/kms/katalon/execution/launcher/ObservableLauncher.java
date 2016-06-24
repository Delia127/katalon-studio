package com.kms.katalon.execution.launcher;

import com.kms.katalon.execution.launcher.listener.LauncherListener;

public interface ObservableLauncher extends ILauncher {

    void addListener(LauncherListener l);

    void removeListener(LauncherListener l);
}
