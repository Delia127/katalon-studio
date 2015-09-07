package com.kms.katalon.composer.mobile.objectspy.element.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;

public class MobileElementTreeContentProvider implements ITreeContentProvider {

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
		if (parentElement instanceof MobileElement) {
			return ((MobileElement) parentElement).getChildrenElement().toArray();
		} 
		return Collections.emptyList().toArray();
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof MobileElement) {
			return ((MobileElement) element).getParentElement();
		} 
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof MobileElement) {
			return ((MobileElement) element).getChildrenElement().size() > 0;
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
		
	}
}
