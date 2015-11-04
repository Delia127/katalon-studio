package com.kms.katalon.composer.objectrepository.provider;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.entity.repository.WebElementEntity;

public class ParentObjectViewerFilter extends EntityViewerFilter {

    private WebElementEntity fChildElement;
    
    public ParentObjectViewerFilter(EntityProvider entityProvider, WebElementEntity childElement) {
        super(entityProvider);
        fChildElement = childElement;
    }

    
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean isQualified = super.select(viewer, parentElement, element);
        if (!isQualified) {
            return false;
        }
        
        ITreeEntity treeEntity = (ITreeEntity) element;
        if (treeEntity instanceof FolderTreeEntity) {
            return true;
        } else if (treeEntity instanceof WebElementTreeEntity) {
            try {
                Object treeObject = treeEntity.getObject();
                if (treeObject == null) {
                    return false;
                }
                
                //Remove web service entity
                if (!WebElementEntity.class.equals(treeObject.getClass())) {
                    return false;
                }
                
                //fChildElement cannot be its parent.
                if (treeObject.equals(fChildElement)) {
                    return false;
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return false;
            }
            return true;
        }
        return false;
    }
}
