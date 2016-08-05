package com.kms.katalon.composer.mobile.objectspy.element.tree;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedTreeContentProvider;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;

public class MobileElementTreeContentProvider extends TypeCheckedTreeContentProvider<TreeMobileElement> {

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
    protected Class<TreeMobileElement> getElementType() {
        return TreeMobileElement.class;
    }

    @Override
    protected Object[] getChildElements(TreeMobileElement parentElement) {
        return parentElement.getChildrenElement().toArray(new TreeMobileElement[0]);
    }

    @Override
    protected Object getParentElement(TreeMobileElement element) {
        return element.getParentElement();
    }

    @Override
    protected boolean hasChildElements(TreeMobileElement element) {
        return !element.getChildrenElement().isEmpty();
    }

}
