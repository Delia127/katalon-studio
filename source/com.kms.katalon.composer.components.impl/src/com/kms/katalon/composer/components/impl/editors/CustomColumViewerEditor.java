package com.kms.katalon.composer.components.impl.editors;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.control.CTableViewer;

/**
 * Enhancement of {@link TableViewerEditor} </p>
 * Improve user behavior when pressing <code>TAB</code> on cell editor with cycle in table movement.
 *
 */
public abstract class CustomColumViewerEditor extends ColumnViewerEditor {

    private TableEditor tableEditor;

    private int feature;

    public static final int TABBING_CYCLE_IN_TABLE = 1 << 7;

    protected CustomColumViewerEditor(CTableViewer viewer,
            ColumnViewerEditorActivationStrategy editorActivationStrategy, int feature) {
        super(viewer, editorActivationStrategy, feature);
        viewer.setColumnViewerEditor(this);
        tableEditor = getTableEditor(viewer);
        this.feature = feature;
    }

    protected TableEditor getTableEditor(CTableViewer viewer) {
        return new CustomTableEditor(viewer);
    }

    /**
     * @see {@link TableViewerEditor#setEditor(Control w, Item item, int columnNumber)}
     */
    @Override
    protected void setEditor(Control w, Item item, int columnNumber) {
        tableEditor.setEditor(w, (TableItem) item, columnNumber);
    }

    /**
     * @see {@link TableViewerEditor#setLayoutData(LayoutData layoutData)}
     */
    @Override
    protected void setLayoutData(LayoutData layoutData) {
        tableEditor.grabHorizontal = layoutData.grabHorizontal;
        tableEditor.horizontalAlignment = layoutData.horizontalAlignment;
        tableEditor.minimumWidth = layoutData.minimumWidth;
        tableEditor.verticalAlignment = layoutData.verticalAlignment;

        if (layoutData.minimumHeight != SWT.DEFAULT) {
            tableEditor.minimumHeight = layoutData.minimumHeight;
        }
    }

    @Override
    protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {
        // Nothing to do here
    }

    /**
     * @return <code>true</code> if current editing cell is the last editable cell of the table. Otherwise,
     * <code>false</code>
     */
    protected abstract boolean isLastEditableCell(ViewerCell focusCell);

    /**
     * Children may override this
     * 
     * @return the first element that users can edit on it. By default, it will be the item with 0 index.
     */
    protected Object firstEditableElement() {
        return getViewer().getElementAt(0);
    }

    /**
     * Children may override this
     * 
     * @return the first column index of the element that users can edit on it. By default, it will be 0.
     */
    protected int firstEditableColumnIndex() {
        return 0;
    }

    @Override
    protected void processTraverseEvent(int columnIndex, ViewerRow row, TraverseEvent event) {
        if (isTabbingCycleInTableActive(event) && isLastEditableCell(row.getCell(columnIndex))) {
            Object firstEditableElement = firstEditableElement();
            if (firstEditableElement != null) {
                getViewer().editElement(firstEditableElement, firstEditableColumnIndex());
            }
            return;
        }

        super.processTraverseEvent(columnIndex, row, event);
    }

    private boolean isTabbingCycleInTableActive(TraverseEvent event) {
        return event.detail == SWT.TRAVERSE_TAB_NEXT && (feature & TABBING_CYCLE_IN_TABLE) == TABBING_CYCLE_IN_TABLE;
    }

    @Override
    protected TableViewer getViewer() {
        return (TableViewer) super.getViewer();
    }
}
