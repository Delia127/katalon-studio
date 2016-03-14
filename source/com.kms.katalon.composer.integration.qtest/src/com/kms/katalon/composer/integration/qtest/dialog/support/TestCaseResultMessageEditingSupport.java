package com.kms.katalon.composer.integration.qtest.dialog.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.integration.qtest.editor.TestCaseResultMessageEditor;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;

/**
 * Supporting editor for {@link ListReportUploadingPreviewDialog}. 
 * <p>
 * This editor a.k.a a dialog will be showed when users
 * want to edit message of a test log.
 * 
 * @see {@link TestCaseResultMessageEditor}
 * @author duyluong
 *
 */
public class TestCaseResultMessageEditingSupport extends EditingSupport {

    public TestCaseResultMessageEditingSupport(ColumnViewer viewer) {
        super(viewer);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (!(element instanceof QTestLogUploadedPreview)) {
            return null;
        }

        QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
        return new TestCaseResultMessageEditor((Composite) getViewer().getControl(), uploadedPreview.getQTestLog()
                .getMessage());
    }

    @Override
    protected boolean canEdit(Object element) {
        if (!(element instanceof QTestLogUploadedPreview)) {
            return false;
        }

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
        if (!(element instanceof QTestLogUploadedPreview) || !(value instanceof String)) {
            return;
        }

        QTestLogUploadedPreview uploadedPreview = (QTestLogUploadedPreview) element;
        String valueChange = (String) value;
        uploadedPreview.getQTestLog().setMessage(valueChange);
        getViewer().refresh(element);
    }

}
