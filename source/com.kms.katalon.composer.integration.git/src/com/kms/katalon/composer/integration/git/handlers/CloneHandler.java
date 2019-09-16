package com.kms.katalon.composer.integration.git.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.internal.workbench.PartServiceImpl;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.git.components.wizards.CustomGitCloneWizard;
import com.kms.katalon.composer.integration.git.constants.GitEventConstants;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.composer.integration.git.preference.GitPreferenceUtil;
import com.kms.katalon.composer.project.handlers.NewProjectHandler;
import com.kms.katalon.composer.project.handlers.OpenProjectHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

@SuppressWarnings("restriction")
public class CloneHandler {
    @Inject
    private IEclipseContext context;

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(Shell parentShell) {
        WizardDialog dlg = new WizardDialog(parentShell, new CustomGitCloneWizard());
        dlg.open();
    }

    @Inject
    @Optional
    private void gitCloneSuccessEventHandler(@UIEventTopic(GitEventConstants.CLONE_FINISHED) final File destination)
            throws InvocationTargetException, InterruptedException {
        if (!GitPreferenceUtil.isGitEnabled()) {
            try {
                GitPreferenceUtil.setEnable(true);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
        openOrCreateNewProjectAtDestination(destination);
    }

    private static void openOrCreateNewProjectAtDestination(final File destinationFolder) {
        File projectFile = OpenProjectHandler.getProjectFile(destinationFolder);
        if (projectFile == null) {
            try {
                ProjectEntity newProject = NewProjectHandler.createNewProject(destinationFolder.getName(),
                        destinationFolder.getParentFile().getAbsolutePath(), "");
                ShareProjectHandler.addDefaultIgnores(newProject.getFolderLocation());
                projectFile = new File(newProject.getLocation());
                EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.PROJECT_CREATED, newProject);
            } catch (Exception e) {
                LoggerSingleton.getInstance().getLogger().error(e);
                MessageDialog.openError(Display.getCurrent().getActiveShell(), GitStringConstants.ERROR,
                        GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_CLONE);
                return;
            }
        }

        try {
            OpenProjectHandler.doOpenProject(null, projectFile.getAbsolutePath(), UISynchronizeService.getInstance()
                    .getSync(), EventBrokerSingleton.getInstance().getEventBroker(), PartServiceSingleton.getInstance()
                    .getPartService(), ModelServiceSingleton.getInstance().getModelService(),
                    ApplicationSingleton.getInstance().getApplication());
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), GitStringConstants.ERROR,
                    GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_CLONE);
        }
        return;
    }

}
