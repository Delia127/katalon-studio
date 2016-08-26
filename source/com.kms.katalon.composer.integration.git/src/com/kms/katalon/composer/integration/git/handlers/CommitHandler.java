package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.egit.ui.internal.actions.CommitActionHandler;

@SuppressWarnings("restriction")
public class CommitHandler extends AbstractGitProjectHandler {
    private static final String COMMIT_COMMAND_ID = "org.eclipse.egit.ui.team.Commit";

    @Override
    public String getEgitCommandId() {
        return COMMIT_COMMAND_ID;
    }

    @Override
    public AbstractHandler getHandler() {
        CommitActionHandler handler = new CommitActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }
}
