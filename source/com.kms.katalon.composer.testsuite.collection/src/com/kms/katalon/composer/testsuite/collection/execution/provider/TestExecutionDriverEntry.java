package com.kms.katalon.composer.testsuite.collection.execution.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.composer.testsuite.collection.util.MapUtil;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class TestExecutionDriverEntry extends TestExecutionEntryItem {

    final protected DriverType driverType;

    final protected String groupName;

    final protected String imageUrl;
    
    protected Map<String, String> runConfigurationData = new HashMap<String, String>();

    protected TestExecutionDriverEntry(final DriverType driverType, final String groupName, final String imageUrl) {
        this.driverType = driverType;
        this.groupName = groupName;
        this.imageUrl = imageUrl;
    }

    @Override
    public IRunConfigurationContributor getRunConfigurationContributor() {
        return RunConfigurationCollector.getInstance().getRunContributor(getName());
    }

    @Override
    public String getName() {
        return driverType.toString();
    }

    @Override
    public String getImageUrlAsString() {
        return imageUrl;
    }
    
    public Map<String, String> getRunConfigurationData() {
        return runConfigurationData;
    }

    public void setRunConfigurationData(Map<String, String> runConfigurationData) {
        this.runConfigurationData = runConfigurationData;
    }

    @Override
    public RunConfigurationDescription toConfigurationEntity() {
        return RunConfigurationDescription.from(groupName, getName(), runConfigurationData);
    }

    public static TestExecutionDriverEntry from(String groupName, DriverType driverType, String imageUrl) {
        return new TestExecutionDriverEntry(driverType, groupName, imageUrl);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(ColumnViewer parent) {
        return null;
    }

    @Override
    public String displayRunConfigurationData(Map<String, String> runConfigurationData) {
        return  MapUtil.buildStringForMap(runConfigurationData);
    }
}
