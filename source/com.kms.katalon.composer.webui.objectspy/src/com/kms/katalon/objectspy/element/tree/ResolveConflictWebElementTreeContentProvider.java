package com.kms.katalon.objectspy.element.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreePath;

import com.kms.katalon.objectspy.element.ConflictWebElementWrapper;

public class ResolveConflictWebElementTreeContentProvider extends WebElementTreeContentProvider {
    
    private boolean isCreatedNewFolderAsPageName;
    
    public ResolveConflictWebElementTreeContentProvider(boolean isCreatedNewFolderAsPageName) {
        this.isCreatedNewFolderAsPageName = isCreatedNewFolderAsPageName;
    }
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ConflictWebElementWrapper) {
            return ((ConflictWebElementWrapper) element).hasChild();
        }
        return false;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ConflictWebElementWrapper) {
            return ((ConflictWebElementWrapper) element).getParent();
        }
        return null;
    }
    
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ConflictWebElementWrapper) {
            return ((ConflictWebElementWrapper) parentElement).getChildren().toArray();
        }
        return new Object[0];
    }
    
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement == null) {
            return new Object[0];
        }
        if (inputElement.getClass().isArray()) {
            Object[] preInputs = (Object[]) inputElement;
            
            //Just hide level 1 - WebPage folder.
            if (!isCreatedNewFolderAsPageName) {
                List<ConflictWebElementWrapper> childList = new ArrayList<>();
                for (Object ob : preInputs) {
                    if (ob instanceof ConflictWebElementWrapper) {
                        childList.addAll(((ConflictWebElementWrapper) ob).getChildren());
                    }
                }
                return childList.toArray();
            }
            
            return (Object[]) inputElement;
        }
        
        if (inputElement instanceof List<?>) {
            List preInput = ((List<?>) inputElement);
            
          //Just hide level 1 - WebPage folder.
            if (!isCreatedNewFolderAsPageName) {
                List<ConflictWebElementWrapper> childList = new ArrayList<>();
                for (Object ob : preInput) {
                    if (ob instanceof ConflictWebElementWrapper) {
                        childList.addAll(((ConflictWebElementWrapper) ob).getChildren());
                    }
                }
                return childList.toArray();
            }
            return ((List<?>) inputElement).toArray();
        }
        return new Object[0];
    }
    
    @Override
    public TreePath getTreePath(Object element) {
        Object parentElement = getParent(element);
        if (parentElement != null) {
            return getTreePath(parentElement).createChildPath(element);
        } else {
            return new TreePath(new Object[] { element });
        }
    }
    
    public boolean isCreatedNewFolderAsPageName() {
        return isCreatedNewFolderAsPageName;
    }

    public void setCreatedNewFolderAsPageName(boolean isCreatedNewFolderAsPageName) {
        this.isCreatedNewFolderAsPageName = isCreatedNewFolderAsPageName;
    }

    
}
