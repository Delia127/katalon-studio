package com.kms.katalon.composer.windows.spy;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedTreeContentProvider;
import com.kms.katalon.composer.windows.element.TreeWindowsElement;

public class WindowsElementTreeContentProvider extends TypeCheckedTreeContentProvider<TreeWindowsElement> {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement == null) {
            return null;
        }

        Class<?> clazz = inputElement.getClass();
        if (clazz.isArray()) {
            return (Object[]) inputElement;
        }

        if (inputElement instanceof List) {
            return ((List<?>) inputElement).toArray();
        }

        return null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    protected Class<TreeWindowsElement> getElementType() {
        return TreeWindowsElement.class;
    }

    @Override
    protected Object[] getChildElements(TreeWindowsElement parentElement) {
        return parentElement.getChildren().toArray(new TreeWindowsElement[0]);
    }

    @Override
    protected Object getParentElement(TreeWindowsElement element) {
        return element.getParent();
    }

    @Override
    protected boolean hasChildElements(TreeWindowsElement element) {
        return !element.getChildren().isEmpty();
    }

}
