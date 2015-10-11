package com.kms.katalon.composer.execution.components.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;

public interface IDriverConnectorEditorContributor {
    public Class<?> getDriverConnectorClass();
    public DriverConnectorCellEditor getCellEditor(Composite parent);
}
