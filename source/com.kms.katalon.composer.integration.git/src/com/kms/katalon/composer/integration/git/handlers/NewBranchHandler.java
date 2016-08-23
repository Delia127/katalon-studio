package com.kms.katalon.composer.integration.git.handlers;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.egit.ui.internal.commit.command.CreateBranchHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.git.components.wizards.CustomCreateBranchWizard;

@SuppressWarnings("restriction")
public class NewBranchHandler extends AbstractGitProjectHandler {

    private static final String BRANCH_COMMAND_ID = CreateBranchHandler.ID;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

    @Override
    public AbstractHandler getHandler() {
        return new CreateBranchHandler();
    }

    @Override
    public String getEgitCommandId() {
        return BRANCH_COMMAND_ID;
    }

    @Execute
    public void execute() {
        WizardDialog dlg = new WizardDialog(shell, new CustomCreateBranchWizard(getRepository(), null));
        dlg.setHelpAvailable(false);
        dlg.open();
    }

}
