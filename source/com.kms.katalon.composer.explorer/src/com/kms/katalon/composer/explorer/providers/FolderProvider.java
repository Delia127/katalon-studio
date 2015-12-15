package com.kms.katalon.composer.explorer.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;

public class FolderProvider extends EntityProvider {

    @Override
    public Object[] getChildren(Object parentElement) {
        try {
            if (parentElement instanceof FolderTreeEntity) {
                Object[] objects = ((FolderTreeEntity) parentElement).getChildren();
                List<FolderTreeEntity> children = new ArrayList<FolderTreeEntity>();
                for (Object o : objects) {
                    if (o instanceof FolderTreeEntity) {
                        children.add((FolderTreeEntity) o);
                    }
                }
                return children.toArray();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return Collections.emptyList().toArray();
    }

    @Override
    public Object getParent(Object element) {
        try {
            if (element instanceof FolderTreeEntity) {
                return ((FolderTreeEntity) element).getParent();
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

}
