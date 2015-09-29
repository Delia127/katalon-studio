package com.kms.katalon.composer.integration.qtest.dialog.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;

public class QTestSuiteParentTreeContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement != null && inputElement instanceof QTestSuiteParent[]) {
            QTestSuiteParent[] releaseRoots = (QTestSuiteParent[]) inputElement;
            return releaseRoots;
        } else {
            return null;
        }
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement != null && parentElement instanceof QTestSuiteParent) {
            QTestSuiteParent suiteParent = (QTestSuiteParent) parentElement;
            return suiteParent.getChildren().toArray(new QTestSuiteParent[suiteParent.getChildren().size()]);
        } else {
            return null;
        }
    }

    @Override
    public Object getParent(Object element) {
        if (element != null && element instanceof QTestSuiteParent) {
            QTestSuiteParent suiteParent = (QTestSuiteParent) element;
            return suiteParent.getParent();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element != null && element instanceof QTestSuiteParent) {
            QTestSuiteParent suiteParent = (QTestSuiteParent) element;

            return suiteParent.getChildren() != null && suiteParent.getChildren().size() > 0;
        } else {
            return false;
        }
    }

}
