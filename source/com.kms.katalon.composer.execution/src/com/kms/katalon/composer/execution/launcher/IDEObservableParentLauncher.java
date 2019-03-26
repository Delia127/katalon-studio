package com.kms.katalon.composer.execution.launcher;

import java.util.List;

/**
 * Launchers that can contains sub-launchers
 *
 */
public interface IDEObservableParentLauncher extends IDEObservableLauncher {
    public List<IDEObservableLauncher> getObservableLaunchers();
}
