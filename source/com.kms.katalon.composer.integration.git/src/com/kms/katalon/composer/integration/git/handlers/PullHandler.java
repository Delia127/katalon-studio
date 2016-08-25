package com.kms.katalon.composer.integration.git.handlers;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.egit.ui.internal.actions.PullWithOptionsActionHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.git.components.wizards.CustomPullWizard;

@SuppressWarnings("restriction")
public class PullHandler extends AbstractGitProjectHandler {
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

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

    @Override
    public void execute() {
        Repository repo = getRepository();
        WizardDialog dialog = new WizardDialog(shell, new CustomPullWizard(repo));
        dialog.open();
    }
}
