package com.kms.katalon.composer.testcase.parts;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;

public class TestSuiteViewerFilter extends EntityViewerFilter {

    public TestSuiteViewerFilter(EntityProvider entityProvider) {
        super(entityProvider);
    }
    
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof TestSuiteCollectionTreeEntity) {
            return false;
        }
        
        return super.select(viewer, parentElement, element);
    }

}