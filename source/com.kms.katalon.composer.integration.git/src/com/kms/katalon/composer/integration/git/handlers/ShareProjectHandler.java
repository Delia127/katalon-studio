package com.kms.katalon.composer.integration.git.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.internal.commands.ShareSingleProjectCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class ShareProjectHandler extends AbstractGitProjectHandler {

    private static final String SHARE_PROJECT_COMMAND_ID = "org.eclipse.egit.ui.command.shareProject";

    @CanExecute
    public boolean canExecute() {
        return getCurrentProject() != null && getRepository() == null;
    }

    @Execute
    public void execute(final Shell shell) {
        try {
            ProjectEntity currentProject = getCurrentProject();
            IProject groovyProject = getCurrentIProject();
            File repo = createRepo(currentProject.getFolderLocation(), groovyProject);
            final ConnectProviderOperation op = new ConnectProviderOperation(groovyProject, repo);
            new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) throws InvocationTargetException {
                    try {
                        op.execute(monitor);
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog.openInformation(shell, GitStringConstants.INFO,
                                        GitStringConstants.HAND_SUCCESS_MSG_SHARE_PROJECT);
                            }
                        });
                    } catch (CoreException ce) {
                        throw new InvocationTargetException(ce);
                    }
                }
            });
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MessageDialog.openError(shell, GitStringConstants.ERROR,
                    GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_SHARE_PROJ);
        }
    }

    private File createRepo(String projectFolder, IProject project) throws IOException, CoreException {
        File gitDir = new File(projectFolder, Constants.DOT_GIT);
        FileRepositoryBuilder.create(gitDir).create();
        // If we don't refresh the project directories right
        // now we won't later know that a .git directory
        // exists within it and we won't mark the .git
        // directory as a team-private member. Failure
        // to do so might allow someone to delete
        // the .git directory without us stopping them.
        // (Half lie, we should optimize so we do not
        // refresh when the .git is not within the project)
        //
        if (!gitDir.toString().contains("..")) {//$NON-NLS-1$
            project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
        }
        Activator.getDefault().getRepositoryUtil().addConfiguredRepository(gitDir);
        return gitDir;
    }

    @Override
    public String getEgitCommandId() {
        return SHARE_PROJECT_COMMAND_ID;
    }

    @Override
    public AbstractHandler getHandler() {
        return new ShareSingleProjectCommand();
    }
}
