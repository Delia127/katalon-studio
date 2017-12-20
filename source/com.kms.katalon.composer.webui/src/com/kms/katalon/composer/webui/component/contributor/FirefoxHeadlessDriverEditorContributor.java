package com.kms.katalon.composer.webui.component.contributor;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.composer.webui.component.FirefoxHeadlessDriverConnectorCellEditor;
import com.kms.katalon.execution.webui.driver.FirefoxHeadlessDriverConnector;

public class FirefoxHeadlessDriverEditorContributor implements IDriverConnectorEditorContributor {

    @Override
    public Class<?> getDriverConnectorClass() {
        return FirefoxHeadlessDriverConnector.class;
    }

    @Override
    public DriverConnectorCellEditor getCellEditor(Composite parent) {
        return new FirefoxHeadlessDriverConnectorCellEditor(parent);
    }

}