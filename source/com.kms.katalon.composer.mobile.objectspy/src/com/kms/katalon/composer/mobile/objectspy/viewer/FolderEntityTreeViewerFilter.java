package com.kms.katalon.composer.mobile.objectspy.viewer;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;

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
