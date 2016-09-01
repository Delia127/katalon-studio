package com.kms.katalon.composer.components.impl.control;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;

public class CTableViewer extends TableViewer implements CustomColumnViewer {

    public CTableViewer(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    public Widget getColumn(int columnIndex) {
        return getColumnViewerOwner(columnIndex);
    }

    @Override
    public ViewerRow getViewerRowFromWidgetItem(Widget item) {
        return super.getViewerRowFromItem(item);
    }
    
    @Override
    public ViewerRow getViewerRowFromItem(Widget item) {
        return super.getViewerRowFromItem(item);
    }
    
    public void showLastItem() {
        Table table = getTable();
        if (table == null || table.isDisposed()) {
            return;
        }
        int lastItemIndex = table.getItemCount() - 1;
        if (lastItemIndex >= 0) {
            table.showItem(table.getItem(lastItemIndex));
        }
    }

    @Override
    public TypeCheckedStyleCellLabelProvider<?> getCellLabelProvider(int columnIndex) {
        return new CellLayoutColumnViewerHelper(this).getCellLabelProvider(columnIndex);
    }

    @Override
    public void enableTooltipSupport() {
        getTable().setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(this);
    }
}
