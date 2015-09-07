package com.kms.katalon.composer.objectrepository.providers;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PropertyContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List)
			return ((List<?>) parentElement).toArray();
		if (parentElement instanceof PropertyGroup)
			return ((PropertyGroup) parentElement).getProperties().toArray();
		return new Object[0];
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof List)
			return ((List<?>) element).size() > 0;
		if (element instanceof PropertyGroup)
			return ((PropertyGroup) element).getProperties().size() > 0;
		return false;
	}

	public Object[] getElements(Object group) {
		return getChildren(group);
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}
