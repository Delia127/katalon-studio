package com.kms.katalon.composer.webui.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.webui.component.RemoteWebDriverConnectorCellEditor;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return RemoteWebDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new RemoteWebDriverConnectorCellEditor(parent);
    }

}
