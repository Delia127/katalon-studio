package com.kms.katalon.composer.integration.qtest.dialog.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.integration.qtest.editor.TestCaseResultMessageEditor;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;

public class TestCaseResultMessageEditingSupport extends EditingSupport {

	public TestCaseResultMessageEditingSupport(ColumnViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element == null && !(element instanceof QTestLogUploadedPreview)) return null;
		QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
		return new TestCaseResultMessageEditor((Composite) getViewer().getControl(), uploadedPreview.getQTestLog().getMessage());
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element == null && !(element instanceof QTestLogUploadedPreview)) return false;
		QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
		return uploadedPreview.getQTestLog() != null;
	}

	@Override
	protected Object getValue(Object element) {
		QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
		return uploadedPreview.getQTestLog().getMessage();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element == null && !(element instanceof QTestLogUploadedPreview)) return;
		if (!(value instanceof String)) return;
		QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
		String valueChange = (String) value;
		uploadedPreview.getQTestLog().setMessage(valueChange);
		getViewer().refresh(element);
	}

}
