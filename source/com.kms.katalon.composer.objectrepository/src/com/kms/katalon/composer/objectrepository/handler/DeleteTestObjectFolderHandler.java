package com.kms.katalon.composer.objectrepository.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class DeleteTestObjectFolderHandler extends DeleteTestObjectHandler implements IDeleteFolderHandler {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private UISynchronize sync;

    @Override
    public FolderType getFolderType() {
        return FolderType.WEBELEMENT;
    }

    @Override
    public boolean execute(FolderTreeEntity folderTreeEntity, IProgressMonitor monitor) {
        try {
            FolderEntity folder = (FolderEntity) folderTreeEntity.getObject();
            if (folder == null) {
                return false;
            }

            final List<Object> descendant = FolderController.getInstance().getAllDescentdantEntities(folder);
            monitor.beginTask(
                    MessageFormat.format(StringConstants.HAND_DELETE_OBJECT_FOLDER_TASK_NAME, folder.getName()),
                    descendant.size() + 1);

            List<IEntity> undeletedTestObjects = new ArrayList<IEntity>();
            String folderId = FolderController.getInstance().getIdForDisplay(folder);
            final boolean hasRef = GroovyRefreshUtil.hasReferencesInTestCaseScripts(folderId, folder.getProject());
            sync.syncExec(new Runnable() {

                @Override
                public void run() {
                    if (hasRef) {
                        isRemovingRef = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                                StringConstants.HAND_TITLE_DELETE, StringConstants.HAND_MSG_REMOVE_FOLDER_REF);
                    }
                }
            });

            for (Object descendantObject : descendant) {

                if (monitor.isCanceled()) {
                    return false;
                }

                if (descendantObject instanceof WebElementEntity) {
                    if (!internallyDeleteTestObject((WebElementEntity) descendantObject, monitor, descendant,
                            isRemovingRef)) {
                        undeletedTestObjects.add((WebElementEntity) descendantObject);
                    }
                } else if (descendantObject instanceof FolderEntity) {
                    FolderEntity descendantFolder = (FolderEntity) descendantObject;
                    deleteFolder(descendantFolder, undeletedTestObjects, monitor);
                }
                monitor.worked(1);
            }

            deleteFolder(folder, undeletedTestObjects, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

    private void deleteFolder(FolderEntity folder, List<IEntity> undeletedTestCases, IProgressMonitor monitor) {
        try {
            String folderId = FolderController.getInstance().getIdForDisplay(folder);
            boolean canDelete = true;
            for (IEntity entity : undeletedTestCases) {
                if (folder.equals(entity.getParentFolder())) {
                    canDelete = false;
                    break;
                }
            }
            if (canDelete) {
                monitor.subTask(MessageFormat.format(StringConstants.HAND_DELETE_OBJECT_FOLDER_TASK_NAME, folderId));
                FolderController.getInstance().deleteFolder(folder);
            } else {
                undeletedTestCases.add(folder);
            }

        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        } finally {
            monitor.worked(1);
        }
    }

    private boolean internallyDeleteTestObject(final WebElementEntity testObject, IProgressMonitor monitor,
            final List<Object> descendant, final boolean isRemovingRefInTestCase) {
        try {
            String testObjectId = ObjectRepositoryController.getInstance().getIdForDisplay(testObject);
            monitor.subTask(MessageFormat.format(StringConstants.HAND_DELETE_OBJECT_SUB_TASK_NAME, testObjectId));
            return performDeleteTestObject(testObject, sync, eventBroker, descendant, isRemovingRefInTestCase);
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return false;
        } finally {
            monitor.worked(1);
        }
    }

}
