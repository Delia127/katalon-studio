package com.kms.katalon.composer.execution.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;

public class LogRecordTreeViewerContentProvider implements ITreeContentProvider {

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
		if (inputElement == null || !(inputElement instanceof List)) return null;
		@SuppressWarnings("unchecked")
		List<ILogParentTreeNode> input = (List<ILogParentTreeNode>) inputElement;
		return input.toArray(new ILogParentTreeNode[0]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement != null && parentElement instanceof ILogParentTreeNode) {
			ILogParentTreeNode parentElementNode = (ILogParentTreeNode) parentElement;
			return parentElementNode.getChildren().toArray(new ILogTreeNode[0]);
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element != null && element instanceof ILogTreeNode) {
			ILogTreeNode elementNode = (ILogTreeNode) element;
			return elementNode.getParent();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element != null && element instanceof ILogParentTreeNode) {
			ILogParentTreeNode parentTreeNode = (ILogParentTreeNode) element;
			return parentTreeNode.getChildren().size() > 0;
		} else {
			return false;
		}
	}

}
