package com.kms.katalon.composer.webui.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.webui.component.EdgeDriverConnectorCellEditor;
import com.kms.katalon.execution.webui.driver.EdgeDriverConnector;

public class EdgeDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return EdgeDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new EdgeDriverConnectorCellEditor(parent);
    }

}
