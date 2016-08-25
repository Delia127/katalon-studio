 
package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.egit.ui.internal.actions.FetchActionHandler;

@SuppressWarnings("restriction")
public class FetchHandler extends AbstractGitProjectHandler {
    private static final String FETCH_COMMAND_ID = "org.eclipse.egit.ui.team.Fetch";

    @Override
    public String getEgitCommandId() {
        return FETCH_COMMAND_ID;
    }

    @Override
    public AbstractHandler getHandler() {
        FetchActionHandler handler = new FetchActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    } 
    
}