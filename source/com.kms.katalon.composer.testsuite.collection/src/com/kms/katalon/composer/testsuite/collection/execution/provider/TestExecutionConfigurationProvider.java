package com.kms.katalon.composer.testsuite.collection.execution.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public interface TestExecutionConfigurationProvider extends TestExecutionItem {
    IRunConfigurationContributor getRunConfigurationContributor();
    
    RunConfigurationDescription toConfigurationEntity();
    
    /**
     * Provide cell editor for editing run configuration data
     * @param parent ColumnViewer
     * @return an instance of CellEditor
     */
    CellEditor getRunConfigurationDataCellEditor(ColumnViewer parent);
}
