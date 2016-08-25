package com.kms.katalon.composer.integration.git.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class ShareProjectHandler extends AbstractGitProjectHandler {
    private static final String GITIGNORE_FILE_NAME = ".gitignore";

    private static final String[] DEFAULT_IGNORE_RESOURCES = { "/bin/", "/Libs/", "/.settings/", "/.classpath", "/.svn/" };

    private static final String SHARE_PROJECT_COMMAND_ID = "org.eclipse.egit.ui.command.shareProject";

    @CanExecute
    public boolean canExecute() {
        return getCurrentProject() != null && getRepository() == null;
    }

    @Execute
    public void execute(final Shell shell) {
        try {
            final ProjectEntity currentProject = getCurrentProject();
            final IProject groovyProject = getCurrentIProject();
            new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) throws InvocationTargetException {
                    try {
                        File repo = createRepo(currentProject.getFolderLocation(), groovyProject);
                        final ConnectProviderOperation op = new ConnectProviderOperation(groovyProject, repo);
                        op.execute(monitor);
                        addDefaultIgnores(currentProject.getFolderLocation());
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialog.openInformation(shell, GitStringConstants.INFO,
                                        GitStringConstants.HAND_SUCCESS_MSG_SHARE_PROJECT);
                            }
                        });
                    } catch (CoreException | IOException ce) {
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
    
    public static void addDefaultIgnores(String projectFolder) throws IOException {
        File gitIgnoreFile = new File(projectFolder, GITIGNORE_FILE_NAME);
        if (!gitIgnoreFile.exists()) {
            gitIgnoreFile.createNewFile();
        }
        String[] ignoreResources = FileUtils.readFileToString(gitIgnoreFile).split("\n");
        List<String> ignoreResourcesList = new ArrayList<>(Arrays.asList(ignoreResources));
        for (String ignoreResource : DEFAULT_IGNORE_RESOURCES) {
            if (ignoreResourcesList.contains(ignoreResource)) {
                continue;
            }
            ignoreResourcesList.add(ignoreResource);
        }
        FileUtils.writeStringToFile(gitIgnoreFile, StringUtils.join(ignoreResourcesList, "\n"));
    }
}
