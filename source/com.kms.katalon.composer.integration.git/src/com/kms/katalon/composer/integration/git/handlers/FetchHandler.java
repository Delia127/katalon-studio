package com.kms.katalon.composer.integration.git.handlers;

import java.net.URISyntaxException;

import javax.inject.Named;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.actions.FetchActionHandler;
import org.eclipse.egit.ui.internal.actions.SimpleFetchActionHandler;
import org.eclipse.egit.ui.internal.fetch.SimpleConfigureFetchDialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.git.components.wizards.CustomGitFetchWizard;
import com.kms.katalon.composer.integration.git.internal.fetch.CustomFetchOperationUI;

@SuppressWarnings("restriction")
public class FetchHandler extends AbstractGitProjectHandler {
    private static String FETCH_COMMAND_ID = "org.eclipse.egit.ui.team.Fetch";
    
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;

    @Override
    public String getEgitCommandId() {
        return FETCH_COMMAND_ID;
    }

    @Override
    public boolean canExecute() {
        return getCurrentProject() != null && (getHandler().isEnabled() || getSimpleHandler().isEnabled());
    }

    @Override
    public FetchActionHandler getHandler() {
        FetchActionHandler handler = new FetchActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }

    private SimpleFetchActionHandler getSimpleHandler() {
        SimpleFetchActionHandler handler = new SimpleFetchActionHandler();
        handler.setSelection(createIProjectSelection());
        return handler;
    }

    @Override
    public void execute() {
        SimpleFetchActionHandler simpleHandler = getSimpleHandler();
        if (simpleHandler.isEnabled()) {
            executeSimpleFetch(simpleHandler);
            return;
        }
        executeFetch();
    }

    private void executeFetch() {
        Repository repository = getRepository();
        if (repository == null) {
            return;
        }
        CustomGitFetchWizard fetchWizard;
        try {
            fetchWizard = new CustomGitFetchWizard(repository);
        } catch (URISyntaxException x) {
            ErrorDialog.openError(shell, UIText.FetchAction_wrongURITitle, UIText.FetchAction_wrongURIMessage,
                    new Status(IStatus.ERROR, Activator.getPluginId(), x.getMessage(), x));
            return;
        }
        WizardDialog dlg = new WizardDialog(shell, fetchWizard);
        dlg.setHelpAvailable(false);
        dlg.open();

    }

    private void executeSimpleFetch(SimpleFetchActionHandler simpleHandler) {
        final Repository repository = getRepository();
        if (repository == null) {
            return;
        }
        RemoteConfig config = SimpleConfigureFetchDialog.getConfiguredRemote(repository);
        if (config == null) {
            MessageDialog.openInformation(shell, UIText.SimpleFetchActionHandler_NothingToFetchDialogTitle,
                    UIText.SimpleFetchActionHandler_NothingToFetchDialogMessage);
            return;
        }

        new CustomFetchOperationUI(repository, config, Activator.getDefault()
                .getPreferenceStore()
                .getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT), false).start();
    }
}
