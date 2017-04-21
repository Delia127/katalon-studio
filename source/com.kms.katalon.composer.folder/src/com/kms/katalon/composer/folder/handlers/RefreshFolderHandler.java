package com.kms.katalon.composer.folder.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class RefreshFolderHandler {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new EventHandler() {
            @SuppressWarnings("restriction")
            @Override
            public void handleEvent(Event event) {
                if (ProjectController.getInstance().getCurrentProject() == null) {
                    return;
                }
                try {
                    Object selectedObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (selectedObject != null && selectedObject instanceof FolderTreeEntity) {
                        execute((FolderTreeEntity) selectedObject, false);
                    }
                } catch (Exception e) {
                    LoggerSingleton.getInstance().getLogger().error(e);
                }
            }
        });
    }

    /**
     * Refresh children tree entities of a folder tree entity on explorer If folder tree entity has its folder entity
     * exists, refresh all its own children If it doesn't refresh its parent tree entity If its folder entity is a test
     * case folder, refresh all its children script class paths If its folder entity is a keyword folder, that means the
     * folder also is keyword root folder then call CustomKeywordParse to refresh all keyword files
     * 
     * @param folderTreeEntity
     * @throws Exception
     */
    private void execute(FolderTreeEntity folderTreeEntity, boolean isRecursive) throws Exception {
        if (!isRecursive) {
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity);
        }

        if ((folderTreeEntity.getObject() != null) && (folderTreeEntity.getObject() instanceof FolderEntity)) {
            FolderEntity folderEntity = folderTreeEntity.getObject();

            if (!isRecursive) {
                RefreshingFolderJob refreshingJob = new RefreshingFolderJob(folderEntity);
                refreshingJob.schedule();
            }

            if (FolderController.getInstance().getFolder(folderEntity.getId()) != null) {
                for (Object treeEntityObject : folderTreeEntity.getChildren()) {
                    ITreeEntity childTreeEntity = (ITreeEntity) treeEntityObject;
                    if (childTreeEntity.getObject() != null
                            && !(childTreeEntity instanceof KeywordTreeEntity && childTreeEntity instanceof PackageTreeEntity)) {
                        if (childTreeEntity instanceof FolderTreeEntity) {
                            execute((FolderTreeEntity) childTreeEntity, true);
                        } else {
                            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, childTreeEntity);
                        }
                    }
                }
            } else {
                eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, folderTreeEntity.getParent());
            }
        } else {
            if (folderTreeEntity.getParent() != null) {
                eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, folderTreeEntity.getParent());
            }
        }

    }

    private class RefreshingFolderJob extends Job {

        private FolderEntity folderEntity;

        public RefreshingFolderJob(FolderEntity folderEntity) {
            super("Refresh folder");
            this.folderEntity = folderEntity;
            this.setUser(true);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                monitor.beginTask("Refreshing folder: " + folderEntity.getIdForDisplay() + "...", 2);
                ProjectEntity projectEntity = folderEntity.getProject();
                GroovyRefreshUtil.refreshFolder(folderEntity.getRelativePath(), folderEntity.getProject(),
                        new SubProgressMonitor(monitor, 1));
                if (folderEntity.getFolderType() == FolderType.TESTCASE) {

                    // refresh children entities
                    folderEntity.setChildrenEntities(FolderController.getInstance().getChildren(folderEntity));
                    GroovyUtil.refreshInfiniteScriptTestCaseClasspath(projectEntity, folderEntity,
                            new SubProgressMonitor(monitor, 1));
                } else if (folderEntity.getFolderType() == FolderType.KEYWORD) {
                    KeywordController.getInstance().parseAllCustomKeywords(projectEntity,
                            new SubProgressMonitor(monitor, 1));
                }
                return Status.OK_STATUS;
            } catch (Exception ex) {
                return Status.CANCEL_STATUS;
            }
        }

    }
}
