 
package com.kms.katalon.composer.integration.git.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.egit.ui.internal.actions.BranchActionHandler;

@SuppressWarnings("restriction")
public class BranchHandler extends AbstractGitProjectHandler {

    private static final String BRANCH_COMMAND_ID = "org.eclipse.egit.ui.team.Branch"; 

    @Override
    public AbstractHandler getHandler() {
        BranchActionHandler handler = new BranchActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }

    @Override
    public String getEgitCommandId() {
        return BRANCH_COMMAND_ID;
    }
		
}