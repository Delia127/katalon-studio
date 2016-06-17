package com.kms.katalon.composer.components.impl.handler;

import com.kms.katalon.composer.components.tree.ITreeEntity;

public abstract class CommonEditPropertiesHandler<T extends ITreeEntity> extends CommonExplorerHandler {

    protected abstract Class<T> getTreeEntityClass();

    /**
     * Get single selection from Test Explorer
     * 
     * @return Corresponding tree entity
     */
    @SuppressWarnings("unchecked")
    protected T getSingleSelection() {
        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length != 1) {
            return null;
        }

        Object selectedObject = selectedObjects[0];
        if (!isInstanceOfAssignedTreeEntity(selectedObject)) {
            return null;
        }

        return (T) selectedObject;
    }

    private boolean isInstanceOfAssignedTreeEntity(Object o) {
        Class<T> treeEntityClass = getTreeEntityClass();
        return treeEntityClass != null && treeEntityClass.isInstance(o);
    }

}
