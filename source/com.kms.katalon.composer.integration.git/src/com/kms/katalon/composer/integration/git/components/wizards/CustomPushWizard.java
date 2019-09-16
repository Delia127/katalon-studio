package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.PushOperation;
import org.eclipse.egit.core.op.PushOperationResult;
import org.eclipse.egit.core.op.PushOperationSpecification;
import org.eclipse.egit.core.securestorage.UserPasswordCredentials;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.components.RefSpecPage;
import org.eclipse.egit.ui.internal.components.RepositorySelection;
import org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;

import com.kms.katalon.composer.integration.git.internal.push.CustomConfirmationPage;
import com.kms.katalon.composer.integration.git.internal.push.CustomPushResultDialog;

/**
 * Wizard allowing user to specify all needed data to push to another repository
 * - including selection of remote repository and refs specifications.
 * <p>
 * Push operation is performed upon successful completion of this wizard.
 */
@SuppressWarnings("restriction")
public class CustomPushWizard extends Wizard {
    private static final String HELP_CONTEXT = "org.eclipse.egit.ui.PushWizard"; //$NON-NLS-1$

    private Repository localDb;

    private final CustomRepositorySelectionPage repoPage;

    private final RefSpecPage refSpecPage;

    private CustomConfirmationPage confirmPage;

    /**
     * Create push wizard for specified local repository.
     *
     * @param localDb
     * repository to push from.
     * @throws URISyntaxException
     * when configuration of this repository contains illegal URIs.
     */
    public CustomPushWizard(final Repository localDb) throws URISyntaxException {
        this.localDb = localDb;
        repoPage = new CustomRepositorySelectionPage(false, RemoteConfig.getAllRemoteConfigs(localDb.getConfig()), null);
        refSpecPage = new RefSpecPage(localDb, true) {
            @Override
            public void setVisible(boolean visible) {
                if (visible) {
                    setSelection(repoPage.getSelection());
                    setCredentials(repoPage.getCredentials());
                }
                super.setVisible(visible);
            }
        };
        refSpecPage.setHelpContext(HELP_CONTEXT);
        confirmPage = new CustomConfirmationPage(localDb) {
            @Override
            public void setVisible(boolean visible) {
                if (visible) {
                    setSelection(repoPage.getSelection(), refSpecPage.getRefSpecs());
                    setCredentials(repoPage.getCredentials());
                }
                super.setVisible(visible);
            }
        };
        confirmPage.setHelpContext(HELP_CONTEXT);
        setDefaultPageImageDescriptor(UIIcons.WIZBAN_PUSH);
        setNeedsProgressMonitor(true);
    }

    private static String getURIsString(final Collection<URIish> uris) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final URIish uri : uris) {
            if (first) {
                first = false;
            } else {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(uri);
        }
        return sb.toString();
    }

    @Override
    public void addPages() {
        addPage(repoPage);
        addPage(refSpecPage);
        addPage(confirmPage);
    }

    @Override
    public boolean canFinish() {
        if (getContainer().getCurrentPage() == repoPage) {
            RepositorySelection sel = repoPage.getSelection();
            if (sel.isConfigSelected()) {
                RemoteConfig config = sel.getConfig();
                return !config.getPushURIs().isEmpty() || !config.getURIs().isEmpty();
            }
        }
        return super.canFinish();
    }

    @Override
    public boolean performFinish() {
        boolean calledFromRepoPage = false;
        if (getContainer().getCurrentPage() == repoPage) {
            calledFromRepoPage = true;
        }
        if (repoPage.getSelection().isConfigSelected() && refSpecPage.isSaveRequested()) {
            saveRefSpecs();
        }

        if (repoPage.getStoreInSecureStore()
                && !SecureStoreUtils.storeCredentials(repoPage.getCredentials(), repoPage.getSelection().getURI())) {
            return false;
        }

        final PushOperation operation = createPushOperation(calledFromRepoPage);
        if (operation == null) {
            return false;
        }
        UserPasswordCredentials credentials = repoPage.getCredentials();
        if (credentials != null) {
            operation.setCredentialsProvider(new EGitCredentialsProvider(credentials.getUser(),
                    credentials.getPassword()));
        }
        final Job job = new PushJob(localDb, operation, null, getDestinationString(repoPage.getSelection()));

        job.setUser(true);
        job.schedule();
        return true;
    }

    @Override
    public String getWindowTitle() {
        final IWizardPage currentPage = getContainer().getCurrentPage();
        if (currentPage == repoPage || currentPage == null) {
            return UIText.PushWizard_windowTitleDefault;
        }
        return NLS.bind(UIText.PushWizard_windowTitleWithDestination, getDestinationString(repoPage.getSelection()));
    }

    private void saveRefSpecs() {
        final RemoteConfig rc = repoPage.getSelection().getConfig();
        rc.setPushRefSpecs(refSpecPage.getRefSpecs());
        final StoredConfig config = localDb.getConfig();
        rc.update(config);
        try {
            config.save();
        } catch (final IOException e) {
            ErrorDialog.openError(getShell(), UIText.PushWizard_cantSaveTitle, UIText.PushWizard_cantSaveMessage,
                    new Status(IStatus.WARNING, Activator.getPluginId(), e.getMessage(), e));
            // Continue, it's not critical.
        }
    }

    private PushOperation createPushOperation(boolean calledFromRepoPage) {
        try {
            PushOperationSpecification spec;
            RemoteConfig config = repoPage.getSelection().getConfig();
            if (calledFromRepoPage) {
                // obtain the push ref specs from the configuration
                // use our own list here, as the config returns a non-modifiable
                // list
                Collection<RefSpec> pushSpecs = new ArrayList<>();
                pushSpecs.addAll(config.getPushRefSpecs());
                Collection<RemoteRefUpdate> updates = Transport.findRemoteRefUpdatesFor(localDb, pushSpecs,
                        config.getFetchRefSpecs());
                spec = new PushOperationSpecification();
                for (URIish uri : repoPage.getSelection().getPushURIs()) {
                    spec.addURIRefUpdates(uri, CustomConfirmationPage.copyUpdates(updates));
                }
            } else if (confirmPage.isConfirmed()) {
                PushOperationResult confirmedResult = confirmPage.getConfirmedResult();
                spec = confirmedResult.deriveSpecification(false);
            } else {
                Collection<RefSpec> fetchSpecs;
                if (config != null) {
                    fetchSpecs = config.getFetchRefSpecs();
                } else {
                    fetchSpecs = null;
                }

                Collection<RemoteRefUpdate> updates = Transport.findRemoteRefUpdatesFor(localDb,
                        refSpecPage.getRefSpecs(), fetchSpecs);
                if (updates.isEmpty()) {
                    ErrorDialog.openError(getShell(), UIText.PushWizard_missingRefsTitle, null, new Status(
                            IStatus.ERROR, Activator.getPluginId(), UIText.PushWizard_missingRefsMessage));
                    return null;
                }

                spec = new PushOperationSpecification();
                for (URIish uri : repoPage.getSelection().getPushURIs()) {
                    spec.addURIRefUpdates(uri, CustomConfirmationPage.copyUpdates(updates));
                }
            }
            return new PushOperation(localDb, spec, false, Activator.getDefault()
                    .getPreferenceStore()
                    .getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT));
        } catch (IOException e) {
            ErrorDialog.openError(getShell(), UIText.PushWizard_cantPrepareUpdatesTitle,
                    UIText.PushWizard_cantPrepareUpdatesMessage,
                    new Status(IStatus.ERROR, Activator.getPluginId(), e.getMessage(), e));
            return null;
        }
    }

    static String getDestinationString(RepositorySelection repoSelection) {
        if (repoSelection.isConfigSelected()) {
            return repoSelection.getConfigName();
        } else {
            return repoSelection.getURI(true).toString();
        }
    }

    static class PushJob extends Job {
        private final PushOperation operation;

        private final PushOperationResult resultToCompare;

        private final String destinationString;

        private Repository localDb;

        public PushJob(final Repository localDb, final PushOperation operation,
                final PushOperationResult resultToCompare, final String destinationString) {
            super(NLS.bind(UIText.PushWizard_jobName, getURIsString(operation.getSpecification().getURIs())));
            this.operation = operation;
            this.resultToCompare = resultToCompare;
            this.destinationString = destinationString;
            this.localDb = localDb;
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                operation.run(monitor);
            } catch (final InvocationTargetException e) {
                return new Status(IStatus.ERROR, Activator.getPluginId(), UIText.PushJob_unexpectedError,
                        e.getCause());
            }

            final PushOperationResult result = operation.getOperationResult();
            if (!result.isSuccessfulConnectionForAnyURI()) {
                return new Status(IStatus.ERROR, Activator.getPluginId(), NLS.bind(UIText.PushJob_cantConnectToAny,
                        result.getErrorStringForAllURis()));
            }

            if (resultToCompare == null || !result.equals(resultToCompare)) {
                CustomPushResultDialog.show(localDb, result, destinationString, true, false);
            }
            return Status.OK_STATUS;
        }
    }
}
