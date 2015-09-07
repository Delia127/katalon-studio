package com.kms.katalon.composer.report.provider;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.core.logging.model.ILogRecord;

public class ReportTreeTableContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		} else if (inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ILogRecord) {
			return ((ILogRecord) parentElement).getChildRecords();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
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
		if (element instanceof ILogRecord) {
			return ((ILogRecord) element).hasChildRecords();
		}
		return false;
	}

}
