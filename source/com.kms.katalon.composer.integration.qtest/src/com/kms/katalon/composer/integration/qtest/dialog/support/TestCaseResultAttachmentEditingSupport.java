package com.kms.katalon.composer.integration.qtest.dialog.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;

public class TestCaseResultAttachmentEditingSupport extends EditingSupport {
	private CheckboxCellEditor editor;

	public TestCaseResultAttachmentEditingSupport(ColumnViewer viewer) {
		super(viewer);
		editor = new CheckboxCellEditor(((TableViewer) viewer).getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
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
		return uploadedPreview.getQTestLog().isAttachmentIncluded();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element == null && !(element instanceof QTestLogUploadedPreview)) return;
		if (!(value instanceof Boolean)) return;
		QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
		boolean valueChange = (boolean) value;
		uploadedPreview.getQTestLog().setAttachmentIncluded(valueChange);
		getViewer().refresh(element);
	}

}
