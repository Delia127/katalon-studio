package com.kms.katalon.composer.integration.git.handlers;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.internal.actions.PushActionHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.git.components.wizards.CustomPushBranchWizard;

@SuppressWarnings("restriction")
public class PushHandler extends AbstractGitProjectHandler {
    private static final String PUSH_COMMAND_ID = "org.eclipse.egit.ui.team.Push";
    
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

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
    
    @Override
    public void execute() {
        try {
            Repository repository = getRepository();
            CustomPushBranchWizard wizard = null;
            Ref ref = getBranchRef(repository);
            if (ref != null) {
                wizard = new CustomPushBranchWizard(repository, ref);
            } else {
                ObjectId id = repository.resolve(repository.getFullBranch());
                wizard = new CustomPushBranchWizard(repository, id);
            }
            WizardDialog dlg = new WizardDialog(shell, wizard);
            dlg.open();
        } catch (IOException | URISyntaxException ex) {
            Activator.handleError(ex.getLocalizedMessage(), ex, false);
            LoggerSingleton.logError(ex);
        }
    }
    
    private Ref getBranchRef(Repository repository) {
        try {
            String fullBranch = repository.getFullBranch();
            if (fullBranch != null && fullBranch.startsWith(Constants.R_HEADS)) {
                return repository.exactRef(fullBranch);
            }
        } catch (IOException e) {
            Activator.handleError(e.getLocalizedMessage(), e, false);
            LoggerSingleton.logError(e);
        }
        return null;
    }
}
