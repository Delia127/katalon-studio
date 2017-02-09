package com.kms.katalon.composer.global.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.global.editor.GlobalVariableSelectionCellEditor;
import com.kms.katalon.composer.global.provider.TableViewerProvider;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableEdittingSupport extends EditingSupport {

    private TableViewerProvider provider;

    public GlobalVariableEdittingSupport(TableViewerProvider provider) {
        super(provider.getTableViewer());
        this.provider = provider;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (!(element instanceof GlobalVariableEntity)) {
            return null;
        }
        return new GlobalVariableSelectionCellEditor((Composite) getViewer().getControl(),
                (GlobalVariableEntity) element, getAllGlobalVariableName());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof GlobalVariableEntity) {
            return ((GlobalVariableEntity) element);
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof GlobalVariableEntity)) {
            return;
        }
        copyTheNewValue((GlobalVariableEntity) element, (GlobalVariableEntity) value);
        getViewer().refresh(element);
        provider.markDirty();
    }

    private void copyTheNewValue(GlobalVariableEntity element, GlobalVariableEntity value) {
        element.setDescription(value.getDescription());
        element.setInitValue(value.getInitValue());
        element.setName(value.getName());
    }

    private List<String> getAllGlobalVariableName() {
        TableViewer tableViewer = provider.getTableViewer();
        if (tableViewer == null || tableViewer.getTable().isDisposed()) {
            return Collections.emptyList();
        }
        List<String> varNames = new ArrayList<String>();
        for (TableItem item : tableViewer.getTable().getItems()) {
            varNames.add(((GlobalVariableEntity) item.getData()).getName());
        }
        return varNames;
    }

}
