package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.egit.ui.internal.actions.PushActionHandler;

@SuppressWarnings("restriction")
public class PushHandler extends AbstractGitProjectHandler {
    private static final String PUSH_COMMAND_ID = "org.eclipse.egit.ui.team.Push";

    @Override
    public String getEgitCommandId() {
        return PUSH_COMMAND_ID;
    }

    @Override
    public AbstractHandler getHandler() {
        PushActionHandler handler = new PushActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }
}
