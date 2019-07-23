package com.kms.katalon.composer.integration.git.internal.fetch;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.egit.core.op.FetchOperation;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.JobFamilies;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider;
import org.eclipse.egit.ui.internal.fetch.FetchResultDialog;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;

/**
 * UI Wrapper for {@link FetchOperation}
 */
@SuppressWarnings("restriction")
public class CustomFetchOperationUI {
    private final Repository repository;

    private final FetchOperation op;

    private final String sourceString;

    /**
     * @param repository
     * @param config
     * @param timeout
     * @param dryRun
     *
     */
    public CustomFetchOperationUI(Repository repository, RemoteConfig config, int timeout, boolean dryRun) {
        this.repository = repository;
        op = new FetchOperation(repository, config, timeout, dryRun);
        sourceString = NLS.bind("{0} - {1}", repository.getDirectory() //$NON-NLS-1$
                .getParentFile()
                .getName(), config.getName());

    }

    /**
     * @param repository
     * @param uri
     * @param specs
     * @param timeout
     * @param dryRun
     */
    public CustomFetchOperationUI(Repository repository, URIish uri, List<RefSpec> specs, int timeout, boolean dryRun) {
        this.repository = repository;
        op = new FetchOperation(repository, uri, specs, timeout, dryRun);
        sourceString = uri.toPrivateString();
    }

    /**
     * @param credentialsProvider
     */
    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        op.setCredentialsProvider(credentialsProvider);
    }

    /**
     * @param tagOpt
     */
    public void setTagOpt(TagOpt tagOpt) {
        op.setTagOpt(tagOpt);
    }

    /**
     * Executes this directly, without showing a confirmation dialog
     *
     * @param monitor
     * @return the result of the operation
     * @throws CoreException
     */
    public FetchResult execute(IProgressMonitor monitor) throws CoreException {
        try {
            if (op.getCredentialsProvider() == null) {
                op.setCredentialsProvider(new EGitCredentialsProvider());
            }
            op.run(monitor);
            return op.getOperationResult();
        } catch (InvocationTargetException e) {
            throw new CoreException(Activator.createErrorStatus(e.getCause().getMessage(), e.getCause()));
        }
    }

    /**
     * Starts the operation asynchronously showing a confirmation dialog after
     * completion
     */
    public void start() {
        Job job = new WorkspaceJob(NLS.bind(UIText.FetchOperationUI_FetchJobName, sourceString)) {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) {
                try {
                    execute(monitor);
                } catch (CoreException e) {
                    Throwable rootCause = ExceptionUtils.getRootCause(e);
                    if (rootCause instanceof UnknownHostException) {
                        return Activator.createErrorStatus(MessageFormat.format(
                                GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_ACCESS_COULD_NOT_RESOLVE_Y,
                                rootCause.getMessage()), e);
                    }
                    return Activator.createErrorStatus(e.getStatus().getMessage(), e);
                }
                return Status.OK_STATUS;
            }

            @Override
            public boolean belongsTo(Object family) {
                if (JobFamilies.FETCH.equals(family)) {
                    return true;
                }
                return super.belongsTo(family);
            }
        };
        job.setUser(true);
        job.schedule();
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (event.getResult().isOK()) {
                    UISynchronizeService.syncExec(() -> {
                        FetchResultDialog dialog = new FetchResultDialog(Display.getCurrent().getActiveShell(),
                                repository, op.getOperationResult(), getSourceString());
                        dialog.open();
                    });
                } else {
                    Activator.handleError(event.getResult().getMessage(), event.getResult().getException(), true);
                }
            }
        });
    }

    /**
     * @return the string denoting the remote source
     */
    public String getSourceString() {
        return sourceString;
    }
}
