package com.kms.katalon.composer.keyword.handlers;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.GitEventConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;

public class GitHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(GitEventConstants.KEYWORD_CLONE_FINISHED, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Thread a = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000L);
                            UISynchronizeService.syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        String tempDir = ProjectController.getInstance().getTempDir();
                                        File git_tmp = new File(tempDir, "git_tmp");

                                        ImportFolderHandler importHandler = new ImportFolderHandler();
                                        importHandler.copyFilesToKeywordsDirectory(
                                                Display.getCurrent().getActiveShell(), git_tmp,
                                                event.getProperty(GitEventConstants.COMMIT_ID).toString(),
                                                event.getProperty(GitEventConstants.REPO_URL).toString());

                                        if (git_tmp.exists()) {
                                            FileUtils.deleteDirectory(git_tmp);
                                        }

                                        ITreeEntity keywordRootFolder = new FolderTreeEntity(
                                                FolderController.getInstance().getKeywordRoot(
                                                        ProjectController.getInstance().getCurrentProject()),
                                                null);

                                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,keywordRootFolder);
                                        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordRootFolder);
                                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,keywordRootFolder);
                                    } catch (Exception e) {
                                        LoggerSingleton.logError(e);
                                        MultiStatusErrorDialog.showErrorDialog(e, "Unable to refresh this keyword",
                                                e.getMessage());
                                    }
                                }
                            });

                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                            MultiStatusErrorDialog.showErrorDialog(e, "Unable to refresh this keyword", e.getMessage());
                        }

                    }
                });
                a.start();

            }
        });
    }
}
