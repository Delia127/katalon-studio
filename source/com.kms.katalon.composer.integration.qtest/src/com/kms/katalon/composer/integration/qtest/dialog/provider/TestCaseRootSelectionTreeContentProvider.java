package com.kms.katalon.composer.integration.qtest.dialog.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.integration.qtest.entity.QTestModule;

public class TestCaseRootSelectionTreeContentProvider  implements ITreeContentProvider  {

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
		if (inputElement != null && inputElement instanceof List<?>) {
			List<?> elements = (List<?>) inputElement;
			return elements.toArray(new Object[elements.size()]);
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement != null && parentElement instanceof QTestModule) {
			QTestModule parentModule = (QTestModule) parentElement;
			List<QTestModule> childModules = parentModule.getChildModules();
			return childModules.toArray(new QTestModule[childModules.size()]);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element != null && element instanceof QTestModule) {
			QTestModule qTestModule = (QTestModule) element;
			return qTestModule.getChildModules().size() > 0;
		}
		return false;
	}

}
