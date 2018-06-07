package com.kms.katalon.composer.keyword.handlers;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.GitEventConstants;
import com.kms.katalon.composer.keyword.git.CustomGitCloneWizard;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;

public class ImportGitHandler {
    @Inject
    IEventBroker eventBroker;
    
    private Shell parentShell;
    
    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) throws Exception {
        WizardDialog dlg = new WizardDialog(parentShell, new CustomGitCloneWizard());
        dlg.open();
 
    }
    
    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        eventBroker.subscribe(GitEventConstants.KEYWORD_CLONE_FINISHED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                try {
                    ImportFolderHandler importHandler = new ImportFolderHandler();

                    String tempDir = ProjectController.getInstance().getTempDir();
                    File git_tmp = new File(tempDir, "git_tmp");
                    
                    importHandler.copyFilesToKeywordsDirectory(parentShell, git_tmp, true);
                    if (git_tmp.exists()) {
                        FileUtils.deleteDirectory(git_tmp);
                    }
                    
                    ITreeEntity keywordRootFolder = new FolderTreeEntity(FolderController.getInstance()
                            .getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null);
                    
                    eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
                    eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
                    
              } catch (Exception e) {
                  LoggerSingleton.logError(e);
              }
            }
        });
    }



}
