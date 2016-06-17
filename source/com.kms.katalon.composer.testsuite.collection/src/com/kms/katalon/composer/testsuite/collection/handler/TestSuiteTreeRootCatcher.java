package com.kms.katalon.composer.testsuite.collection.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class TestSuiteTreeRootCatcher {
    private FolderTreeEntity testRunTreeRoot;

    private Object getFirstSelection(ESelectionService selectionService) {
        Object selectedObject =  selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        
        if (selectedObject == null || !selectedObject.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjectAsArray = (Object[]) selectedObject;
        return  selectedObjectAsArray.length == 1 ? selectedObjectAsArray[0] : null;
    }
    
    protected FolderTreeEntity getParentTestRunTreeFolder(ESelectionService selectionService, boolean returnRootIfNull) {
        Object selectedObj = getFirstSelection(selectionService);

        if (selectedObj instanceof FolderTreeEntity) {
            FolderTreeEntity folderTreeEntity = (FolderTreeEntity) selectedObj;
            try {
                FolderEntity folder = (FolderEntity) folderTreeEntity.getObject();
                if (folder != null && folder.getFolderType() == FolderType.TESTSUITE) {
                    return folderTreeEntity;
                }
            } catch (Exception e) {
                //Ignore this, will never happen.
            }
        }
        return returnRootIfNull ? testRunTreeRoot : null;
    }

    @Inject
    @Optional
    private void catchTestCaseTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
        try {
            if (treeEntities == null) {
                return;
            }

            for (Object o : treeEntities) {
                Object entityObject = ((ITreeEntity) o).getObject();
                if (!(entityObject instanceof FolderEntity)) {
                    continue;
                }

                if (((FolderEntity) entityObject).getFolderType() == FolderType.TESTSUITE) {
                    testRunTreeRoot = (FolderTreeEntity) o;
                    return;
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
