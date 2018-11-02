 
package com.kms.katalon.composer.integration.git.handlers;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.PartServiceImpl;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.egit.ui.internal.actions.BranchActionHandler;

import com.kms.katalon.composer.components.services.PartServiceSingleton;

@SuppressWarnings("restriction")
public class BranchHandler extends AbstractGitProjectHandler {
    @Inject
    private IEclipseContext context;
    
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
    
    @Override
    public void execute() {
//        EPartService partService = context.getActive(PartServiceImpl.class);
//        PartServiceSingleton.getInstance().setPartService(partService);
        super.execute();
    }
		
}