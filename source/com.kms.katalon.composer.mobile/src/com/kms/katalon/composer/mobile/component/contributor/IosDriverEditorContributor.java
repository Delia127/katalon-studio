package com.kms.katalon.composer.mobile.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.mobile.component.IosDriverConnectorCellEditor;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return IosDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new IosDriverConnectorCellEditor(parent);
    }

}
