package com.kms.katalon.composer.objectrepository.handler;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.groovy.util.GroovyRefreshUtil.findReferencesInAffectedTestCaseScripts;
import static com.kms.katalon.groovy.util.GroovyRefreshUtil.findReferencesInTestCaseScripts;
import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;

public class DeleteTestObjectFolderHandler extends DeleteTestObjectHandler implements IDeleteFolderHandler {

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

            List<Object> descendantEntities = FolderController.getInstance().getAllDescentdantEntities(folder);
            monitor.beginTask(format(StringConstants.HAND_DELETE_OBJECT_FOLDER_TASK_NAME, folder.getName()),
                    descendantEntities.size() + 1);

            List<IFile> affectedTestCaseScripts = findReferencesInTestCaseScripts(folder.getIdForDisplay()
                    + StringConstants.ENTITY_ID_SEPERATOR, folder.getProject());

            List<IEntity> undeletedTestObjects = new ArrayList<IEntity>();

            for (Object entity : descendantEntities) {
                if (monitor.isCanceled()) {
                    return false;
                }

                if (entity instanceof WebElementEntity
                        && !deleteTestObject((WebElementEntity) entity, monitor, descendantEntities,
                                affectedTestCaseScripts)) {
                    undeletedTestObjects.add((WebElementEntity) entity);
                    continue;
                }

                if (entity instanceof FolderEntity) {
                    deleteFolder((FolderEntity) entity, undeletedTestObjects, monitor);
                }
            }

            deleteFolder(folder, undeletedTestObjects, monitor);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
            return true;
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

    private boolean deleteTestObject(WebElementEntity testObject, IProgressMonitor monitor, List<Object> descendant,
            List<IFile> affectedTestCaseScripts) {
        try {
            String testObjectId = testObject.getIdForDisplay();
            monitor.subTask(format(StringConstants.HAND_DELETE_OBJECT_SUB_TASK_NAME, testObjectId));
            return performDeleteTestObject(testObject, descendant,
                    findReferencesInAffectedTestCaseScripts(testObjectId, affectedTestCaseScripts));
        } catch (Exception e) {
            logError(e);
            return false;
        } finally {
            monitor.worked(1);
        }
    }
}
