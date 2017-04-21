package com.kms.katalon.composer.components.impl.handler;

import java.util.List;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.ProjectController;

public abstract class AddTestObjectHandler extends CommonExplorerHandler {

    @Override
    public boolean canExecute() {
        if (ProjectController.getInstance().getCurrentProject() == null) {
            return false;
        }
        return canAddToObjectSpy(getElementSelection(ITreeEntity.class));
    }

    private boolean canAddToObjectSpy(List<ITreeEntity> elementSelection) {
        for (ITreeEntity selectedObject : elementSelection) {
            try {
                if (!canAddToObjectSpy(selectedObject)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private boolean canAddToObjectSpy(ITreeEntity treeEntity) throws Exception {
        if (treeEntity instanceof WebElementTreeEntity) {
            return ((WebElementTreeEntity) treeEntity).canAddToObjectSpy();
        } else if (treeEntity instanceof FolderTreeEntity) {
            for (Object child : ((FolderTreeEntity) treeEntity).getChildren()) {
                if (!canAddToObjectSpy((ITreeEntity) child)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
