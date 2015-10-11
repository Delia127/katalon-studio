package com.kms.katalon.composer.execution.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverConnectorCellEditor;
import com.kms.katalon.composer.execution.components.contributor.IDriverConnectorEditorContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class DriverConnectorEditorCollector {
    private static DriverConnectorEditorCollector _instance;
    private List<IDriverConnectorEditorContributor> driverConnectorEditorContributorList;

    private DriverConnectorEditorCollector() {
        driverConnectorEditorContributorList = new ArrayList<IDriverConnectorEditorContributor>();
    }

    public static DriverConnectorEditorCollector getInstance() {
        if (_instance == null) {
            _instance = new DriverConnectorEditorCollector();
        }
        return _instance;
    }

    public void addDriverConnectorEditorContributor(IDriverConnectorEditorContributor driverConnectorContributor) {
        driverConnectorEditorContributorList.add(driverConnectorContributor);
    }

    public DriverConnectorCellEditor getDriverConnector(Class<?> driverConnectorClass, Composite parent) throws IOException,
            ExecutionException {
        for (IDriverConnectorEditorContributor driverConnectorEditorContributor : driverConnectorEditorContributorList) {
            if (driverConnectorEditorContributor.getDriverConnectorClass().getName().equals(driverConnectorClass.getName())) {
                return driverConnectorEditorContributor.getCellEditor(parent);
            }
        }
        return null;
    }

    public IDriverConnectorEditorContributor[] getAllDriverConnectorEditorContributors() {
        return driverConnectorEditorContributorList.toArray(new IDriverConnectorEditorContributor[driverConnectorEditorContributorList
                .size()]);
    }
}
