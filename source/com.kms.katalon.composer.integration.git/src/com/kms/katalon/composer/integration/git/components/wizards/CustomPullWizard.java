package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.core.op.PullOperation.PullReferenceConfig;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.pull.PullOperationUI;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.kms.katalon.composer.components.log.LoggerSingleton;

/**
 * A wizard to allow to specify a pull operation with options
 */
@SuppressWarnings("restriction")
public class CustomPullWizard extends Wizard {

    private final Repository repository;

    private CustomPullWizardPage page;

    private CustomRepositorySelectionPage addRemotePage;

    /**
     * @param repo
     * the repository
     */
    public CustomPullWizard(final Repository repo) {
        this.repository = repo;
        setWindowTitle(UIText.PullWizardPage_PageTitle);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        try {
            final List<RemoteConfig> remotes = RemoteConfig.getAllRemoteConfigs(repository.getConfig());
            if (remotes.isEmpty()) {
                addRemotePage = new CustomRepositorySelectionPage();
                addPage(addRemotePage);
            }
            page = new CustomPullWizardPage(this.repository) {
                @Override
                public void setVisible(boolean visible) {
                    super.setVisible(visible);
                    if (!visible) {
                        return;
                    }
                    try {
                        RemoteConfig rc;
                        if (addRemotePage != null) {
                            rc = new RemoteConfig(repository.getConfig(), Constants.DEFAULT_REMOTE_NAME);
                            rc.addURI(addRemotePage.getSelection().getURI());
                        } else {
                            rc = remotes.get(0);
                        }
                        setSelectedRemote(rc);
                    } catch (URISyntaxException e) {
                        LoggerSingleton.logError(e);
                    }
                }
            };
            addPage(page);
        } catch (URISyntaxException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performFinish() {
        try {
            if (this.addRemotePage != null) {
                storeCredentials(addRemotePage);
                URIish uri = addRemotePage.getSelection().getURI();
                configureNewRemote(uri);
            }
            configureUpstream();
            startPull();
            return true;
        } catch (IOException e) {
            Activator.logError(e.getMessage(), e);
            return false;
        } catch (URISyntaxException e) {
            Activator.logError(e.getMessage(), e);
            return false;
        }
    }

    private void storeCredentials(CustomRepositorySelectionPage remotePage) {
        if (!remotePage.getStoreInSecureStore()) {
            return;
        }
        URIish uri = remotePage.getSelection().getURI();
        if (uri != null) {
            SecureStoreUtils.storeCredentials(remotePage.getCredentials(), uri);
        }
    }

    private void configureNewRemote(URIish uri) throws URISyntaxException, IOException {
        StoredConfig config = repository.getConfig();
        String remoteName = this.page.getRemoteConfig().getName();
        RemoteConfig remoteConfig = new RemoteConfig(config, remoteName);
        remoteConfig.addURI(uri);
        RefSpec defaultFetchSpec = new RefSpec().setForceUpdate(true).setSourceDestination(Constants.R_HEADS + "*", //$NON-NLS-1$
                Constants.R_REMOTES + remoteName + "/*"); //$NON-NLS-1$
        remoteConfig.addFetchRefSpec(defaultFetchSpec);
        remoteConfig.update(config);
        config.save();
    }

    private void configureUpstream() throws IOException {
        String fullBranch = this.repository.getFullBranch();
        if (fullBranch == null || !fullBranch.startsWith(Constants.R_HEADS)) {
            // Don't configure upstream for detached HEAD
            return;
        }
        String remoteName = page.getRemoteConfig().getName();
        String fullRemoteBranchName = page.getFullRemoteReference();

        String localBranchName = repository.getBranch();
        StoredConfig config = repository.getConfig();
        config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName, ConfigConstants.CONFIG_KEY_REMOTE,
                remoteName);
        config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName, ConfigConstants.CONFIG_KEY_MERGE,
                fullRemoteBranchName);
        if (this.page.isRebaseSelected()) {
            config.setBoolean(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName,
                    ConfigConstants.CONFIG_KEY_REBASE, true);
        } else {
            // Make sure we overwrite any previous configuration
            config.unset(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName, ConfigConstants.CONFIG_KEY_REBASE);
        }

        config.save();
    }

    private void startPull() {
        Map<Repository, PullReferenceConfig> repos = new HashMap<>(1);
        PullReferenceConfig config = new PullReferenceConfig(page.getRemoteConfig().getName(),
                page.getFullRemoteReference(), page.getUpstreamConfig());
        repos.put(repository, config);
        PullOperationUI pullOperationUI = new PullOperationUI(repos);
        pullOperationUI.start();
    }

}
