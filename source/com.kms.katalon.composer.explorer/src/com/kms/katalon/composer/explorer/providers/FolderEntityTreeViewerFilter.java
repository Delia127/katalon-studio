package com.kms.katalon.composer.explorer.providers;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;

public class FolderEntityTreeViewerFilter extends EntityViewerFilter {

    public FolderEntityTreeViewerFilter(EntityProvider entityProvider) {
        super(entityProvider);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof FolderTreeEntity) {
            return super.select(viewer, parentElement, element);
        }
        
        return false;
    }
}
