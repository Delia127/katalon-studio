package com.kms.katalon.composer.keyword.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.integration.git.components.wizards.CustomGitCloneWizard;
import com.kms.katalon.composer.integration.git.constants.GitEventConstants;
import com.kms.katalon.composer.integration.git.constants.GitStringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

public class ImportGitHandler {
    @Inject
    IEventBroker eventBroker;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        WizardDialog dlg = new WizardDialog(parentShell, new CustomGitCloneWizard());
        dlg.open();
    }

    @Inject
    @Optional
    private void gitCloneSuccessEventHandler(@UIEventTopic(GitEventConstants.CLONE_FINISHED) final File destination)
            throws InvocationTargetException, InterruptedException {
        cloneKeywordRepository(destination);
        try {
            ITreeEntity keywordRootFolder = new FolderTreeEntity(
                    FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()),
                    null);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }


    private void cloneKeywordRepository(File keywordRepository) {
        try {
            // target folder
            FolderEntity parentFolder = FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject());
            FolderEntity copiedKWGitRepositoryFolder = FolderController.getInstance().addNewFolder(parentFolder,
                    keywordRepository.getName());

            for (File file : keywordRepository.listFiles()) {
                Files.copy(Paths.get(file.getPath()),
                        Paths.get(copiedKWGitRepositoryFolder.getLocation() + File.separator + file.getName()));
            }

        } catch (Exception e) {
            LoggerSingleton.getInstance().logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), GitStringConstants.ERROR,
                    GitStringConstants.HAND_ERROR_MSG_UNABLE_TO_CLONE);
        }
        return;
    }
}
