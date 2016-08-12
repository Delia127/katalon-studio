package com.kms.katalon.composer.components.impl.editors;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.TableEditor;

import com.kms.katalon.composer.components.impl.control.CTableViewer;

public class DefaultTableColumnViewerEditor extends CustomColumViewerEditor {

    protected DefaultTableColumnViewerEditor(CTableViewer viewer) {
        super(viewer, new ColumnViewerEditorActivationStrategy(viewer), DEFAULT);
    }

    @Override
    protected boolean isLastEditableCell(ViewerCell focusCell) {
        return false;
    }

    @Override
    protected TableEditor getTableEditor(CTableViewer viewer) {
        return new CustomTableEditor(viewer);
    }

    public static DefaultTableColumnViewerEditor create(CTableViewer viewer) {
        return new DefaultTableColumnViewerEditor(viewer);
    }
}
