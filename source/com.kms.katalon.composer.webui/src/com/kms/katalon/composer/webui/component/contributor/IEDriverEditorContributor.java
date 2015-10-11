package com.kms.katalon.composer.webui.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.webui.component.IEDriverConnectorCellEditor;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;

public class IEDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return IEDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new IEDriverConnectorCellEditor(parent);
    }

}
