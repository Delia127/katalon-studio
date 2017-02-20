package com.kms.katalon.composer.objectrepository.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.impl.handler.AddTestObjectHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class AddToObjectSpyHandler extends AddTestObjectHandler {

    @Inject
    private static IEventBroker eventBroker;

    @Override
    public void execute() {
        if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
            try {
                Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
                List<ITreeEntity> treeEntitiesList = new ArrayList<ITreeEntity>();
                for (Object selectedObject : selectedObjects) {
                    if (selectedObject instanceof ITreeEntity) {
                        ITreeEntity newTreeEntity = (ITreeEntity) selectedObject;
                        boolean isExist = false;
                        for (int i = 0; i < treeEntitiesList.size(); i++) {
                            if (isParent(treeEntitiesList.get(i), newTreeEntity)) {
                                isExist = true;
                                break;
                            } else if (isParent(newTreeEntity, treeEntitiesList.get(i))) {
                                treeEntitiesList.set(i, newTreeEntity);
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            treeEntitiesList.add(newTreeEntity);
                        }
                    }
                }
                if (treeEntitiesList.size() > 0) {
                    Object[] selectedTestObjects = new Object[treeEntitiesList.size()];
                    for (int i = 0; i < treeEntitiesList.size(); i++) {
                        selectedTestObjects[i] = treeEntitiesList.get(i).getObject();
                    }
                    eventBroker.post(EventConstants.OBJECT_SPY_TEST_OBJECT_ADDED, selectedTestObjects);
                }
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }
        }
    }

    private static boolean isParent(ITreeEntity newEntity, ITreeEntity existingEntity) throws Exception {
        boolean isParent = false;
        if (existingEntity != null && newEntity != null) {
            ITreeEntity parentTreeEntity = existingEntity.getParent();
            while (parentTreeEntity != null) {
                if (parentTreeEntity.equals(newEntity)) {
                    isParent = true;
                    break;
                } else {
                    parentTreeEntity = parentTreeEntity.getParent();
                }
            }
        }
        return isParent;
    }
}
