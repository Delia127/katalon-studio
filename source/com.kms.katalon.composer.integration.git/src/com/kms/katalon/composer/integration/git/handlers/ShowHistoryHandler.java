package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.egit.ui.internal.actions.ShowHistoryActionHandler;

@SuppressWarnings("restriction")
public class ShowHistoryHandler extends AbstractGitProjectHandler {
    private static final String SHOW_HISTORY_COMMAND_ID = "org.eclipse.egit.ui.team.ShowHistory";

    @Override
    public String getEgitCommandId() {
        return SHOW_HISTORY_COMMAND_ID;
    }

    @Override
    public AbstractHandler getHandler() {
        ShowHistoryActionHandler handler = new ShowHistoryActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }

}
