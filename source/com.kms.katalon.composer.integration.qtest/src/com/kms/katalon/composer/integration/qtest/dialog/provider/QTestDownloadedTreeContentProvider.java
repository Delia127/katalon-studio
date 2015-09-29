package com.kms.katalon.composer.integration.qtest.dialog.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.integration.qtest.dialog.model.ModuleDownloadedPreviewTreeNode;
import com.kms.katalon.composer.integration.qtest.dialog.model.DownloadedPreviewTreeNode;

public class QTestDownloadedTreeContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement != null && inputElement instanceof List<?>) {
            List<?> chilren = (List<?>) inputElement;

            return chilren.toArray(new Object[chilren.size()]);
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement != null && parentElement instanceof ModuleDownloadedPreviewTreeNode) {
            ModuleDownloadedPreviewTreeNode parentModule = (ModuleDownloadedPreviewTreeNode) parentElement;

            List<Object> chilren = new ArrayList<Object>();
            chilren.addAll(parentModule.getChildModuleTrees());
            chilren.addAll(parentModule.getChildTestCaseTrees());

            return chilren.toArray(new Object[chilren.size()]);
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element != null && element instanceof DownloadedPreviewTreeNode) {
            DownloadedPreviewTreeNode updated = (DownloadedPreviewTreeNode) element;

            return updated.getParent();
        }
        return false;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element != null) {
            if (element instanceof ModuleDownloadedPreviewTreeNode) {
                ModuleDownloadedPreviewTreeNode module = (ModuleDownloadedPreviewTreeNode) element;
                return module.getChildModuleTrees().size() + module.getChildTestCaseTrees().size() > 0;
            }
        }
        return false;
    }

}
