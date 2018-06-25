package com.kms.katalon.composer.keyword.git;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.egit.core.internal.util.ProjectUtil;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.core.op.CloneOperation.PostCloneTask;
import org.eclipse.egit.core.op.ConfigureFetchAfterCloneTask;
import org.eclipse.egit.core.op.ConfigureGerritAfterCloneTask;
import org.eclipse.egit.core.op.ConfigurePushAfterCloneTask;
import org.eclipse.egit.core.op.SetRepositoryConfigPropertyTask;
import org.eclipse.egit.core.securestorage.UserPasswordCredentials;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.JobFamilies;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.clone.GitCloneSourceProviderExtension;
import org.eclipse.egit.ui.internal.clone.GitCloneSourceProviderExtension.CloneSourceProvider;
import org.eclipse.egit.ui.internal.clone.ProjectRecord;
import org.eclipse.egit.ui.internal.clone.ProjectUtils;
import org.eclipse.egit.ui.internal.clone.RememberHostTask;
import org.eclipse.egit.ui.internal.components.RepositorySelection;
import org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo.PushInfo;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo.RepositoryConfigProperty;
import org.eclipse.egit.ui.internal.provisional.wizards.IRepositorySearchResult;
import org.eclipse.egit.ui.internal.provisional.wizards.NoRepositoryInfoException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkingSet;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.git.components.wizards.CustomRepositorySelectionPage;
import com.kms.katalon.composer.integration.git.components.wizards.CustomSourceBranchPage;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.composer.keyword.constants.GitEventConstants;
import com.kms.katalon.controller.ProjectController;

@SuppressWarnings("restriction")
public class CustomGitCloneWizard extends Wizard {
    /**
     * a page for branch selection
     */
    protected CustomSourceBranchPage validSource;

    /**
     * a page for selection of the clone destination
     */
    protected CustomCloneDestinationPage cloneDestination;

    /**
     * the path where a clone has been created in
     */
    protected String alreadyClonedInto;

    /**
     * whether the clone operation is done later on by the caller of the wizard
     */
    protected boolean callerRunsCloneOperation;

    /**
     * the result which was found when the last search was done
     */
    protected IRepositorySearchResult currentSearchResult;

    private CloneOperation cloneOperation;

    /**
     * Construct the clone wizard with a repository location page that allows
     * the repository info to be provided by different search providers.
     */
    public CustomGitCloneWizard() {
        this(new CustomRepositorySelectionPage());
    }

    /**
     * Construct the clone wizard based on given repository search result. If
     * the search result is an instance of org.eclipse.jface.wizard.WizardPage,
     * then the page is shown in the wizard before the repository info is read.
     * The repository location page that allows the repository info to be
     * provided by different search providers is not shown.
     *
     * @param searchResult
     * the search result to initialize the clone wizard with.
     */
    public CustomGitCloneWizard(IRepositorySearchResult searchResult) {
        setNeedsProgressMonitor(true);
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
        cloneDestination = new CustomCloneDestinationPage() {
            @Override
            public void setVisible(boolean visible) {
                RepositorySelection selection = getRepositorySelection();
                if (selection != null && visible) {
                    setSelection(selection, validSource.getAvailableBranches(), validSource.getSelectedBranches(),
                            validSource.getHEAD());
                }
                super.setVisible(visible);
            }
        };
        initialize();
    }

    private void initialize() {
        setWindowTitle(UIText.GitCloneWizard_title);
        setDefaultPageImageDescriptor(UIIcons.WIZBAN_IMPORT_REPO);
        setNeedsProgressMonitor(true);
    }

    /**
     * Set whether to show project import options on the destination page
     *
     * @param show
     * @return this wizard
     */
    public CustomGitCloneWizard setShowProjectImport(boolean show) {
        cloneDestination.setShowProjectImport(show);
        return this;
    }

    @Override
    public boolean performCancel() {
        if (alreadyClonedInto == null) {
            return true;
        }

        File test = new File(alreadyClonedInto);
        if (test.exists()
                && MessageDialog.openQuestion(getShell(), UIText.GitCloneWizard_abortingCloneTitle,
                        UIText.GitCloneWizard_abortingCloneMsg)) {
            try {
                FileUtils.delete(test, FileUtils.RECURSIVE | FileUtils.RETRY);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
        return true;
    }

    @Override
    public boolean canFinish() {
        return cloneDestination.isPageComplete();
    }

    @Override
    public boolean performFinish() {
        try {
            return performClone(currentSearchResult.getGitRepositoryInfo());
        } catch (URISyntaxException e) {
            Activator.error(UIText.GitImportWizard_errorParsingURI, e);
        } catch (NoRepositoryInfoException e) {
            Activator.error(UIText.GitImportWizard_noRepositoryInfo, e);
        } catch (Exception e) {
            Activator.error(e.getMessage(), e);
        } finally {
            setWindowTitle(UIText.GitCloneWizard_title);
        }
        return false;
    }

    @Override
    final public void addPages() {
        addPage(new CustomRepositorySelectionPage());
        addPage(validSource);
        addPage(cloneDestination);
    }

    /**
     * @return if the search result is set
     */
    protected boolean hasSearchResult() {
        return currentSearchResult != null;
    }

    /**
     * @return a list of CloneSourceProviders, may be extended by a subclass
     */
    protected List<CloneSourceProvider> getCloneSourceProviders() {
        return GitCloneSourceProviderExtension.getCloneSourceProvider();
    }

    /**
     * Do the clone using data which were collected on the pages {@code validSource} and {@code cloneDestination}
     *
     * @param gitRepositoryInfo
     * @return if clone was successful
     * @throws Exception 
     */
    protected boolean performClone(GitRepositoryInfo gitRepositoryInfo) throws Exception {
        URIish uri = new URIish(gitRepositoryInfo.getCloneUri());
        UserPasswordCredentials credentials = gitRepositoryInfo.getCredentials();
        setWindowTitle(NLS.bind(UIText.GitCloneWizard_jobName, uri.toString()));
        final boolean allSelected;
        final Collection<Ref> selectedBranches;
        if (validSource.isSourceRepoEmpty()) {
            // fetch all branches of empty repo
            allSelected = true;
            selectedBranches = Collections.emptyList();
        } else {
            allSelected = validSource.isAllSelected();
            selectedBranches = validSource.getSelectedBranches();
        }
        
        String tempDir = ProjectController.getInstance().getTempDir();
        
        final File workdir = new File(tempDir, "git_tmp");
        
        final Ref ref = cloneDestination.getInitialBranch();
        final String remoteName = cloneDestination.getRemote();

        boolean created = workdir.exists();
        if (workdir.exists()) {
            org.apache.commons.io.FileUtils.deleteDirectory(workdir);
        }
        created = workdir.mkdirs();

        if (!created || !workdir.isDirectory()) {
            final String errorMessage = NLS.bind(UIText.GitCloneWizard_errorCannotCreate, workdir.getPath());
            ErrorDialog.openError(getShell(), getWindowTitle(), UIText.GitCloneWizard_failed, new Status(IStatus.ERROR,
                    Activator.getPluginId(), 0, errorMessage, null));
            // let's give user a chance to fix this minor problem
            return false;
        }

        int timeout = Activator.getDefault().getPreferenceStore().getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT);
        final CloneOperation op = new CloneOperation(uri, allSelected, selectedBranches, workdir, ref != null
                ? ref.getName() : null, remoteName, timeout);
        CredentialsProvider credentialsProvider = null;
        if (credentials != null) {
            credentialsProvider = new EGitCredentialsProvider(credentials.getUser(), credentials.getPassword());
        } else {
            credentialsProvider = new EGitCredentialsProvider();
        }
        op.setCredentialsProvider(credentialsProvider);
        op.setCloneSubmodules(cloneDestination.isCloneSubmodules());

        rememberHttpHost(op, uri);
        configureFetchSpec(op, gitRepositoryInfo, remoteName);
        configurePush(op, gitRepositoryInfo, remoteName);
        configureRepositoryConfig(op, gitRepositoryInfo);
        configureGerrit(op, gitRepositoryInfo, credentialsProvider, remoteName, timeout);

        if (cloneDestination.isImportProjects()) {
            final IWorkingSet[] sets = cloneDestination.getWorkingSets();
            op.addPostCloneTask(new PostCloneTask() {
                @Override
                public void execute(Repository repository, IProgressMonitor monitor) throws CoreException {
                    importProjects(repository, sets);
                }
            });
        }

        alreadyClonedInto = workdir.getPath();

        if (!callerRunsCloneOperation) {
            runAsJob(uri, op, gitRepositoryInfo, workdir);
        } else {
            cloneOperation = op;
        }
        return true;
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof IRepositorySearchResult) {
            currentSearchResult = (IRepositorySearchResult) page;
            return validSource;
        }
        return super.getNextPage(page);
    }

    /**
     * @return the repository selected by the user or {@code null} if an error
     * occurred
     */
    @Nullable
    protected RepositorySelection getRepositorySelection() {
        try {
            return (new RepositorySelection(new URIish(currentSearchResult.getGitRepositoryInfo().getCloneUri()), null));
        } catch (URISyntaxException e) {
            Activator.error(UIText.GitImportWizard_errorParsingURI, e);
            return null;
        } catch (NoRepositoryInfoException e) {
            Activator.error(UIText.GitImportWizard_noRepositoryInfo, e);
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
            return currentSearchResult.getGitRepositoryInfo().getCredentials();
        } catch (NoRepositoryInfoException e) {
            Activator.error(UIText.GitImportWizard_noRepositoryInfo, e);
            return null;
        } catch (Exception e) {
            Activator.error(e.getMessage(), e);
            return null;
        }
    }

    private void rememberHttpHost(CloneOperation op, URIish uri) {
        String scheme = uri.getScheme();
        if (scheme != null && scheme.toLowerCase().startsWith("http")) { //$NON-NLS-1$
            String host = uri.getHost();
            if (host != null) {
                op.addPostCloneTask(new RememberHostTask(host));
            }
        }
    }

    private void configureFetchSpec(CloneOperation op, GitRepositoryInfo gitRepositoryInfo, String remoteName) {
        for (String fetchRefSpec : gitRepositoryInfo.getFetchRefSpecs())
            op.addPostCloneTask(new ConfigureFetchAfterCloneTask(remoteName, fetchRefSpec));
    }

    private void configurePush(CloneOperation op, GitRepositoryInfo gitRepositoryInfo, String remoteName) {
        for (PushInfo pushInfo : gitRepositoryInfo.getPushInfos())
            try {
                URIish uri = pushInfo.getPushUri() != null ? new URIish(pushInfo.getPushUri()) : null;
                ConfigurePushAfterCloneTask task = new ConfigurePushAfterCloneTask(remoteName,
                        pushInfo.getPushRefSpec(), uri);
                op.addPostCloneTask(task);
            } catch (URISyntaxException e) {
                Activator.handleError(UIText.GitCloneWizard_failed, e, true);
            }
    }

    private void configureRepositoryConfig(CloneOperation op, GitRepositoryInfo gitRepositoryInfo) {
        for (RepositoryConfigProperty p : gitRepositoryInfo.getRepositoryConfigProperties()) {
            SetRepositoryConfigPropertyTask task = new SetRepositoryConfigPropertyTask(p.getSection(),
                    p.getSubsection(), p.getName(), p.getValue());
            op.addPostCloneTask(task);
        }
    }

    private void configureGerrit(CloneOperation op, GitRepositoryInfo gitRepositoryInfo,
            CredentialsProvider credentialsProvider, String remoteName, int timeout) {
        ConfigureGerritAfterCloneTask task = new ConfigureGerritAfterCloneTask(gitRepositoryInfo.getCloneUri(),
                remoteName, credentialsProvider, timeout);
        op.addPostCloneTask(task);
    }

    private void importProjects(final Repository repository, final IWorkingSet[] sets) {
        String repoName = Activator.getDefault().getRepositoryUtil().getRepositoryName(repository);
        Job importJob = new WorkspaceJob(MessageFormat.format(UIText.GitCloneWizard_jobImportProjects, repoName)) {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) {
                List<File> files = new ArrayList<>();
                ProjectUtil.findProjectFiles(files, repository.getWorkTree(), true, monitor);
                if (files.isEmpty())
                    return Status.OK_STATUS;

                Set<ProjectRecord> records = new LinkedHashSet<>();
                for (File file : files)
                    records.add(new ProjectRecord(file));
                try {
                    ProjectUtils.createProjects(records, sets, monitor);
                } catch (InvocationTargetException e) {
                    Activator.logError(e.getLocalizedMessage(), e);
                } catch (InterruptedException e) {
                    Activator.logError(e.getLocalizedMessage(), e);
                }
                return Status.OK_STATUS;
            }
        };
        importJob.schedule();
    }

    /**
     * @param container
     * @param repositoryInfo
     */
    public void runCloneOperation(IWizardContainer container, final GitRepositoryInfo repositoryInfo,
            final File destination) {
        try {
            container.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    executeCloneOperation(cloneOperation, repositoryInfo, monitor, destination);
                }
            });
        } catch (InvocationTargetException e) {
            Activator.handleError(UIText.GitCloneWizard_failed, e.getCause(), true);
        } catch (InterruptedException e) {
            // nothing to do
        }
    }

    private void runAsJob(final URIish uri, final CloneOperation op, final GitRepositoryInfo repositoryInfo,
            final File destination) throws InterruptedException {
        final Job job = new Job(NLS.bind(UIText.GitCloneWizard_jobName, uri.toString())) {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    return executeCloneOperation(op, repositoryInfo, monitor, destination);
                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                } catch (InvocationTargetException e) {
                    Throwable thr = e.getCause();
                    return new Status(IStatus.ERROR, Activator.getPluginId(), 0, thr.getMessage(), thr);
                }
            }

            @Override
            public boolean belongsTo(Object family) {
                if (JobFamilies.CLONE.equals(family))
                    return true;
                return super.belongsTo(family);
            }
        };
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (!event.getResult().isOK()) {
                    return;
                }
                EventBrokerSingleton.getInstance().getEventBroker().post(GitEventConstants.KEYWORD_CLONE_FINISHED, destination);
            }
        });
        job.setUser(true);
        job.schedule();
    }

    private IStatus executeCloneOperation(final CloneOperation op, final GitRepositoryInfo repositoryInfo,
            final IProgressMonitor monitor, final File destination) throws InvocationTargetException,
            InterruptedException {
        try {
            final RepositoryUtil util = Activator.getDefault().getRepositoryUtil();
            op.run(monitor);
            util.addConfiguredRepository(op.getGitDir());
            if (repositoryInfo.shouldSaveCredentialsInSecureStore()) {
                SecureStoreUtils.storeCredentials(repositoryInfo.getCredentials(),
                        new URIish(repositoryInfo.getCloneUri()));
            }
        } catch (InterruptedException e) {
            // User cancel, ignore this
            return Status.CANCEL_STATUS;
        } catch (Exception e) {
            Activator.error(e.getMessage(), e);
            LoggerSingleton.getInstance().getLogger().error(e);
            UISynchronizeService.syncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog.openError(getShell(), GitStringConstants.ERROR,
                            GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_CLONE);
                }
            });
        }
        return Status.OK_STATUS;
    }

    /**
     * @param newValue
     * if true the clone wizard just creates a clone operation. The
     * caller has to run this operation using runCloneOperation. If
     * false the clone operation is performed using a job.
     */
    public void setCallerRunsCloneOperation(boolean newValue) {
        callerRunsCloneOperation = newValue;
    }
}
