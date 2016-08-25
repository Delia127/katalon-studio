package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.egit.ui.internal.actions.PullWithOptionsActionHandler;

@SuppressWarnings("restriction")
public class PullHandler extends AbstractGitProjectHandler {
    private static final String PULL_WITH_OPTION_COMMAND_ID = "org.eclipse.egit.ui.team.PullWithOptions";

    @Override
    public String getEgitCommandId() {
        return PULL_WITH_OPTION_COMMAND_ID;
    }

    @Override
    public AbstractHandler getHandler() {
        PullWithOptionsActionHandler handler = new PullWithOptionsActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }
}
