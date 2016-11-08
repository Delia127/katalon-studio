package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;

import com.kms.katalon.composer.integration.git.preference.GitPreferenceUtil;

public class GitToolbarHandler {

    @CanExecute
    private boolean canExecute() {
        return GitPreferenceUtil.isGitEnabled();
    }
}
