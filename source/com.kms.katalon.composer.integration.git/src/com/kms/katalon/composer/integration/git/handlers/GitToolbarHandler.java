package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;

import com.kms.katalon.composer.integration.git.preference.GitPreferenceUtil;
import com.kms.katalon.preferences.internal.GitToolbarExecutableStatus;

public class GitToolbarHandler {

    @CanExecute
    private boolean canExecute() {
        boolean isGitEnabled = GitPreferenceUtil.isGitEnabled();
        GitToolbarExecutableStatus.setValue(isGitEnabled);
        return isGitEnabled;
    }
}
