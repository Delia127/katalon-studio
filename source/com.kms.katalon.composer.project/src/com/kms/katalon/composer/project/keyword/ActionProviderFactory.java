package com.kms.katalon.composer.project.keyword;

import com.kms.katalon.core.keyword.IActionProvider;

public class ActionProviderFactory {
    private static ActionProviderFactory _instance;

    private IActionProvider actionProvider;

    public static ActionProviderFactory getInstance() {
        if (_instance == null) {
            _instance = new ActionProviderFactory();
        }
        return _instance;
    }

    public void setActionProvider(IActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public IActionProvider getActionProvider() {
        return actionProvider;
    }
}
