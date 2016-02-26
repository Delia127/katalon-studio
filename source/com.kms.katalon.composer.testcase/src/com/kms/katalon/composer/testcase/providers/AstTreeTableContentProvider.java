package com.kms.katalon.composer.testcase.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.testcase.ast.treetable.AstScriptTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;

public class AstTreeTableContentProvider implements ITreeContentProvider {
    @Override
    public Object[] getElements(Object inputElement) {
        if (!(inputElement instanceof List<?>)) {
            return Collections.emptyList().toArray();
        }
        List<AstTreeTableNode> treeTableNodes = new ArrayList<AstTreeTableNode>();
        for (Object object : ((List<?>) inputElement)) {
            if (object instanceof AstScriptTreeTableNode) {
                AstScriptTreeTableNode mainBlockNode = (AstScriptTreeTableNode) object;
                mainBlockNode.reloadChildren();
                treeTableNodes.addAll(mainBlockNode.getChildren());
            } else if (object instanceof AstTreeTableNode) {
                treeTableNodes.add((AstTreeTableNode) object);
            }
        }
        return treeTableNodes.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (!(parentElement instanceof AstTreeTableNode)) {
            return null;
        }
        AstTreeTableNode parentNode = (AstTreeTableNode) parentElement;
        parentNode.reloadChildren();
        return parentNode.getChildren().toArray();
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof AstTreeTableNode) {
            return ((AstTreeTableNode) element).getParent();
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
    public boolean hasChildren(Object element) {
        return (element instanceof AstTreeTableNode && ((AstTreeTableNode) element).hasChildren());
    }

}
