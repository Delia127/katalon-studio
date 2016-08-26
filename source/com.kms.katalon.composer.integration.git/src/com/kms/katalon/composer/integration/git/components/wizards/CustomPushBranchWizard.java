package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.core.op.PushOperationResult;
import org.eclipse.egit.core.op.PushOperationSpecification;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.components.RepositorySelection;
import org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider;
import org.eclipse.egit.ui.internal.push.PushOperationUI;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.kms.katalon.composer.integration.git.internal.push.CustomConfirmationPage;

/**
 * A wizard dedicated to pushing a commit.
 */
@SuppressWarnings("restriction")
public class CustomPushBranchWizard extends Wizard {

    private final Repository repository;

    private final ObjectId commitToPush;

    /**
     * In case of detached HEAD, reference is null.
     */
    private final Ref ref;

    private CustomRepositorySelectionPage addRemotePage;

    private CustomPushBranchPage pushBranchPage;

    private CustomConfirmationPage confirmationPage;

    /**
     * @param repository
     * the repository the ref belongs to
     * @param ref
     * @throws URISyntaxException
     */
    public CustomPushBranchWizard(final Repository repository, Ref ref) throws URISyntaxException {
        this(repository, ref.getObjectId(), ref);
    }

    /**
     * @param repository
     * the repository commit belongs to
     * @param commitToPush
     * @throws URISyntaxException
     */
    public CustomPushBranchWizard(final Repository repository, ObjectId commitToPush) throws URISyntaxException {
        this(repository, commitToPush, null);
    }

    private CustomPushBranchWizard(final Repository repository, ObjectId commitToPush, Ref ref)
            throws URISyntaxException {
        this.repository = repository;
        this.commitToPush = commitToPush;
        this.ref = ref;
        assert (this.repository != null);
        assert (this.commitToPush != null);

        final List<RemoteConfig> allRemoteConfigs = RemoteConfig.getAllRemoteConfigs(repository.getConfig());
        if (allRemoteConfigs.isEmpty()) {
            addRemotePage = new CustomRepositorySelectionPage(true, allRemoteConfigs, null);
        }

        pushBranchPage = new CustomPushBranchPage(repository, commitToPush, ref) {
            @Override
            public void setVisible(boolean visible) {
                if (!visible) {
                    super.setVisible(visible);
                    return;
                }
                if (addRemotePage != null) {
                    setSelectedRemote(Constants.DEFAULT_REMOTE_NAME, addRemotePage.getSelection()
                            .getURI());
                } else {
                    RemoteConfig remoteConfig = allRemoteConfigs.get(0);
                    setSelectedRemote(remoteConfig.getName(), remoteConfig.getURIs().get(0));
                }
                super.setVisible(visible);
            }
        };

        confirmationPage = new CustomConfirmationPage(repository) {
            @Override
            public void setVisible(boolean visible) {
                setSelection(getRepositorySelection(), getRefSpecs());
                CustomRepositorySelectionPage remotePage = getAddRemotePage();
                if (remotePage != null) {
                    setCredentials(remotePage.getCredentials());
                }
                super.setVisible(visible);
            }
        };

        setDefaultPageImageDescriptor(UIIcons.WIZBAN_PUSH);
    }

    @Override
    public void addPages() {
        if (addRemotePage != null) {
            addPage(addRemotePage);
        }
        addPage(pushBranchPage);
        addPage(confirmationPage);
    }

    @Override
    public String getWindowTitle() {
        if (ref != null) {
            return MessageFormat.format(UIText.PushBranchWizard_WindowTitle,
                    Repository.shortenRefName(this.ref.getName()));
        }
        return UIText.PushCommitHandler_pushCommitTitle;
    }

    @Override
    public boolean canFinish() {
        return getContainer().getCurrentPage() == confirmationPage && confirmationPage.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        try {
            CustomRepositorySelectionPage remotePage = getAddRemotePage();
            if (remotePage != null) {
                storeCredentials(remotePage);
                URIish uri = remotePage.getSelection().getURI();
                configureNewRemote(uri);
            }
            if (pushBranchPage.isConfigureUpstreamSelected()) {
                configureUpstream();
            }
            startPush();
        } catch (IOException e) {
            confirmationPage.setErrorMessage(e.getMessage());
            return false;
        } catch (URISyntaxException e) {
            confirmationPage.setErrorMessage(e.getMessage());
            return false;
        }
        return true;
    }

    private CustomRepositorySelectionPage getAddRemotePage() {
        return addRemotePage;
    }

    private RepositorySelection getRepositorySelection() {
        CustomRepositorySelectionPage remotePage = getAddRemotePage();
        if (remotePage != null) {
            return remotePage.getSelection();
        }
        return new RepositorySelection(null, pushBranchPage.getRemoteConfig());
    }

    private List<RefSpec> getRefSpecs() {
        String src = this.ref != null ? this.ref.getName() : this.commitToPush.getName();
        String dst = pushBranchPage.getFullRemoteReference();
        RefSpec refSpec = new RefSpec().setSourceDestination(src, dst).setForceUpdate(
                pushBranchPage.isForceUpdateSelected());
        return Arrays.asList(refSpec);
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
        String remoteName = getRemoteName();
        RemoteConfig remoteConfig = new RemoteConfig(config, remoteName);
        remoteConfig.addURI(uri);
        RefSpec defaultFetchSpec = new RefSpec().setForceUpdate(true).setSourceDestination(Constants.R_HEADS + "*", //$NON-NLS-1$
                Constants.R_REMOTES + remoteName + "/*"); //$NON-NLS-1$
        remoteConfig.addFetchRefSpec(defaultFetchSpec);
        remoteConfig.update(config);
        config.save();
    }

    private void configureUpstream() throws IOException {
        if (this.ref == null) {
            // Don't configure upstream for detached HEAD
            return;
        }
        String remoteName = getRemoteName();
        String fullRemoteBranchName = pushBranchPage.getFullRemoteReference();
        String localBranchName = Repository.shortenRefName(this.ref.getName());

        StoredConfig config = repository.getConfig();
        config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName, ConfigConstants.CONFIG_KEY_REMOTE,
                remoteName);
        config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName, ConfigConstants.CONFIG_KEY_MERGE,
                fullRemoteBranchName);
        if (pushBranchPage.isRebaseSelected()) {
            config.setBoolean(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName,
                    ConfigConstants.CONFIG_KEY_REBASE, true);
        } else {
            // Make sure we overwrite any previous configuration
            config.unset(ConfigConstants.CONFIG_BRANCH_SECTION, localBranchName, ConfigConstants.CONFIG_KEY_REBASE);
        }

        config.save();
    }

    private void startPush() throws IOException {
        PushOperationResult result = confirmationPage.getConfirmedResult();
        PushOperationSpecification pushSpec = result.deriveSpecification(false);

        PushOperationUI pushOperationUI = new PushOperationUI(repository, pushSpec, false);
        pushOperationUI.setCredentialsProvider(new EGitCredentialsProvider());
        pushOperationUI.setShowConfigureButton(false);
        pushOperationUI.start();
    }

    private String getRemoteName() {
        return pushBranchPage.getRemoteConfig().getName();
    }
}
