package com.kms.katalon.composer.components.impl.editors;

import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;

public class CustomTableEditor extends TableEditor {

    private static final int DF_EXTENDED_SPACE = 0;
    private CTableViewer tableViewer;
    private int columnIndex;

    public CustomTableEditor(CTableViewer tableViewer) {
        super(tableViewer.getTable());
        this.tableViewer = tableViewer;
        this.columnIndex = 0;
    }

    @Override
    public void setEditor(Control editor, TableItem item, int column) {
        super.setEditor(editor, item, column);
        columnIndex = column;
    }
    
    @Override
    public void layout() {
        super.layout();
        Control editor = getEditor();
        if (editor == null) {
            return;
        }

        Rectangle rect = editor.getBounds();
        int extendedSpace = getExtendedSpace();
        editor.setBounds(rect.x + extendedSpace, rect.y, rect.width - extendedSpace, rect.height);
    }

    protected int getExtendedSpace() {
        TypeCheckedStyleCellLabelProvider<?> cellLabelProvider = tableViewer.getCellLabelProvider(columnIndex);
        if (cellLabelProvider == null) {
            return DF_EXTENDED_SPACE;
        }
        CellLayoutInfo cellLayoutInfo = cellLabelProvider.getCellLayoutInfo();
        if (cellLayoutInfo == null) {
            return DF_EXTENDED_SPACE;
        }
        return cellLayoutInfo.getLeftMargin() + cellLayoutInfo.getSpace();
    }
}
