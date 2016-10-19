package com.kms.katalon.composer.testsuite.collection.execution.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class CustomTestExecutionEntry extends TestExecutionEntryItem {

    final protected String name;

    final protected String groupName;

    final protected String imageUrl;

    protected Map<String, String> runConfigurationData = new HashMap<String, String>();

    protected CustomTestExecutionEntry(final String name, final String groupName, final String imageUrl) {
        this.name = name;
        this.groupName = groupName;
        this.imageUrl = imageUrl;
    }

    @Override
    public IRunConfigurationContributor getRunConfigurationContributor() {
        return RunConfigurationCollector.getInstance().getRunContributor(getName());
    }

    @Override
    public String getName() {
        return name;
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

    public static CustomTestExecutionEntry from(String groupName, String name, String imageUrl) {
        return new CustomTestExecutionEntry(name, groupName, imageUrl);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(ColumnViewer parent) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CustomTestExecutionEntry)) {
            return false;
        }
        CustomTestExecutionEntry otherCustomTestEntity = (CustomTestExecutionEntry) obj;
        return getName().equals(otherCustomTestEntity.getName());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).toHashCode();
    }
}
