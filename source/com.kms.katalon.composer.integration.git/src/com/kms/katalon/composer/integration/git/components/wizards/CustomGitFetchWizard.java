package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.securestorage.UserPasswordCredentials;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.components.RepositorySelection;
import org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;

/**
 * Wizard allowing user to specify all needed data to fetch from another
 * repository - including selection of remote repository, ref specifications,
 * annotated tags fetching strategy.
 * <p>
 * Fetch operation is performed upon successful completion of this wizard.
 */
@SuppressWarnings("restriction")
public class CustomGitFetchWizard extends Wizard {
    private final Repository localDb;

    private final CustomRepositorySelectionPage repoPage;

    private final CustomSourceBranchPage validSource;

    /**
     * Create wizard for provided local repository.
     *
     * @param localDb
     * local repository to fetch to.
     * @throws URISyntaxException
     * when configuration of this repository contains illegal URIs.
     */
    public CustomGitFetchWizard(final Repository localDb) throws URISyntaxException {
        this.localDb = localDb;
        final List<RemoteConfig> remotes = RemoteConfig.getAllRemoteConfigs(localDb.getConfig());
        repoPage = new CustomRepositorySelectionPage(true, remotes, null);
        validSource = new CustomSourceBranchPage() {
            @Override
            public void setVisible(boolean visible) {
                RepositorySelection selection = getRepositorySelection();
                if (selection != null && visible) {
                    setSelection(selection);
                    setCredentials(getCredentials());
                }
                super.setVisible(visible);
            }
        };
        setDefaultPageImageDescriptor(UIIcons.WIZBAN_FETCH);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        addPage(repoPage);
        addPage(validSource);
    }

    /**
     * @return the repository selected by the user or {@code null} if an error
     * occurred
     */
    protected RepositorySelection getRepositorySelection() {
        try {
            return (new RepositorySelection(new URIish(repoPage.getGitRepositoryInfo().getCloneUri()), null));
        } catch (URISyntaxException e) {
            Activator.error(UIText.GitImportWizard_errorParsingURI, e);
            return null;
        } catch (Exception e) {
            Activator.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @return the credentials
     */
    protected UserPasswordCredentials getCredentials() {
        try {
            return repoPage.getGitRepositoryInfo().getCredentials();
        } catch (Exception e) {
            Activator.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean canFinish() {
        if (getContainer().getCurrentPage() != repoPage) {
            return super.canFinish();
        }
        RepositorySelection sel = repoPage.getSelection();
        if (sel.isConfigSelected()) {
            RemoteConfig config = sel.getConfig();
            return (!config.getURIs().isEmpty() && !config.getFetchRefSpecs().isEmpty());
        }
        return super.canFinish();
    }

    @Override
    public boolean performFinish() {
        if (repoPage.getStoreInSecureStore()
                && !SecureStoreUtils.storeCredentials(repoPage.getCredentials(), repoPage.getSelection().getURI())) {
            return false;
        }

        int timeout = Activator.getDefault().getPreferenceStore().getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT);
        final RepositorySelection repoSelection = repoPage.getSelection();
        final FetchOperationUI op = new FetchOperationUI(localDb, repoSelection.getURI(false), getDefaultRefSpecs(),
                timeout, false);

        UserPasswordCredentials credentials = repoPage.getCredentials();
        if (credentials != null) {
            op.setCredentialsProvider(new EGitCredentialsProvider(credentials.getUser(), credentials.getPassword()));
        }
        op.start();
        saveConfig();
        return true;
    }

    private void saveConfig() {
        try {
            final RemoteConfig rc = new RemoteConfig(localDb.getConfig(), Constants.DEFAULT_REMOTE_NAME);
            rc.setFetchRefSpecs(getDefaultRefSpecs());
            rc.setTagOpt(TagOpt.FETCH_TAGS);
            rc.addURI(repoPage.getSelection().getURI());
            final StoredConfig config = localDb.getConfig();
            rc.update(config);
            config.save();
        } catch (final IOException | URISyntaxException e) {
            ErrorDialog.openError(getShell(), UIText.FetchWizard_cantSaveTitle, UIText.FetchWizard_cantSaveMessage,
                    new Status(IStatus.WARNING, Activator.getPluginId(), e.getMessage(), e));
            // Continue, it's not critical.
        }
    }

    @Override
    public String getWindowTitle() {
        final IWizardPage currentPage = getContainer().getCurrentPage();
        if (currentPage == repoPage || currentPage == null) {
            return UIText.FetchWizard_windowTitleDefault;
        }
        return NLS.bind(UIText.FetchWizard_windowTitleWithSource, getSourceString());
    }

    public static List<RefSpec> getDefaultRefSpecs() {
        List<RefSpec> refSpecs = new ArrayList<RefSpec>();
        refSpecs.add(Transport.REFSPEC_PUSH_ALL);
        refSpecs.add(Transport.REFSPEC_TAGS);
        return refSpecs;
    }

    private String getSourceString() {
        final RepositorySelection repoSelection = repoPage.getSelection();
        if (repoSelection.isConfigSelected()) {
            return repoSelection.getConfigName();
        }
        return repoSelection.getURI(false).toString();
    }
}
