package com.kms.katalon.composer.execution.collection.provider;

import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public interface TestExecutionConfigurationProvider extends TestExecutionItem {
    IRunConfigurationContributor getRunConfigurationContributor();
    
    RunConfigurationDescription toConfigurationEntity(RunConfigurationDescription previousDescription);
    
    /**
     * Provide cell editor for editing run configuration data
     * @param parent parent control
     * @return an instance of CellEditor
     */
    CellEditor getRunConfigurationDataCellEditor(Composite parent);
    
    Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData);

    /**
     * Displayed data in Run With column of Test Suite Collection
     */
    String displayRunConfigurationData(Map<String, String> runConfigurationData);
    
    /**
     * Used in GenerateCommandDialog.
     * @return true if the item needs non-empty runConfigurationData.
     */
    boolean requiresExtraConfiguration();
}
