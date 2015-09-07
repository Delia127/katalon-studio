package com.kms.katalon.composer.testsuite.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TestCaseTableContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return (Object[]) inputElement;
    }

}
