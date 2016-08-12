package com.kms.katalon.composer.components.impl.control;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;

public class CTreeViewer extends TreeViewer implements CustomColumnViewer {

    public CTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    public CTreeViewer(Tree tree) {
        super(tree);
    }

    @Override
    public Widget getColumn(int columnIndex) {
        return getColumnViewerOwner(columnIndex);
    }

    @Override
    public ViewerRow getViewerRowFromWidgetItem(Widget item) {
        return getViewerRowFromItem(item);
    }

    @Override
    public TypeCheckedStyleCellLabelProvider<?> getCellLabelProvider(int columnIndex) {
        return new CellLayoutColumnViewerHelper(this).getCellLabelProvider(columnIndex);
    }
}
