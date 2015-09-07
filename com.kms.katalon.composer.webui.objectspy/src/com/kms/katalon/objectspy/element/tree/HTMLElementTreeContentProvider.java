package com.kms.katalon.objectspy.element.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;

public class HTMLElementTreeContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			return ((List<Object>)inputElement).toArray();
		} else if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof HTMLFrameElement) {
			return ((HTMLFrameElement) parentElement).getChildElements().toArray();
		} 
		return Collections.emptyList().toArray();
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof HTMLElement) {
			return ((HTMLElement) element).getParentElement();
		} 
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof HTMLFrameElement) {
			return !((HTMLFrameElement) element).getChildElements().isEmpty();
		} 
		return false;
	}
	
	public TreePath getTreePath(Object element) {
	    Object parentElement = getParent(element);
	    if (parentElement != null) {
	        return getTreePath(parentElement).createChildPath(element);
	    } else {
	        return new TreePath(new Object[] {element});
	    }
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
