package com.kms.katalon.composer.mobile.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.mobile.component.AndroidDriverConnectorCellEditor;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return AndroidDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new AndroidDriverConnectorCellEditor(parent);
    }

}
