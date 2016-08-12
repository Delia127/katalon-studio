package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerRow;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;

/**
 * Exposes more features that {@link ColumnViewer} doesn't do it
 *
 */
public interface CustomColumnViewer {

    /**
     * Exposes {@link ColumnViewer#getColumnViewerOwner(int columnIndex)}
     * 
     * @param columnIndex
     */
    Widget getColumn(int columnIndex);


    ViewerRow getViewerRowFromWidgetItem(Widget item);

    TypeCheckedStyleCellLabelProvider<?> getCellLabelProvider(int columnIndex);
}
