package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.jface.viewers.ColumnViewer;

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
    public Widget getColumn(int columnIndex);

}
