package com.kms.katalon.composer.webui.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.webui.component.HeadlessDriverConnectorCellEditor;
import com.kms.katalon.execution.webui.driver.HeadlessDriverConnector;

public class HeadlessDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return HeadlessDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new HeadlessDriverConnectorCellEditor(parent);
    }

}