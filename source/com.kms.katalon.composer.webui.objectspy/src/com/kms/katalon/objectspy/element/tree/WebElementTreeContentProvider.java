package com.kms.katalon.objectspy.element.tree;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;

import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebFrame;

public class WebElementTreeContentProvider implements ITreeContentProvider {

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof WebElement) {
            return ((WebElement) element).hasChild();
        }
        return false;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof WebElement) {
            return ((WebElement) element).getParent();
        }
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement == null) {
            return new Object[0];
        }
        if (inputElement.getClass().isArray()) {
            return (Object[]) inputElement;
        }
        if (inputElement instanceof List<?>) {
            return ((List<?>) inputElement).toArray();
        }
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof WebFrame) {
            return ((WebFrame) parentElement).getChildren().toArray();
        }
        return new Object[0];
    }

    public TreePath getTreePath(Object element) {
        Object parentElement = getParent(element);
        if (parentElement != null) {
            return getTreePath(parentElement).createChildPath(element);
        } else {
            return new TreePath(new Object[] { element });
        }
    }

}
