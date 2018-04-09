package com.kms.katalon.composer.execution.collection.provider;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class CustomTestExecutionEntry extends TestExecutionEntryItem {

    final protected String name;

    final protected String groupName;

    final protected String imageUrl;

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

    public static CustomTestExecutionEntry from(String groupName, String name, String imageUrl) {
        return new CustomTestExecutionEntry(name, groupName, imageUrl);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(Composite parent) {
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

    @Override
    public String displayRunConfigurationData(Map<String, String> runConfigurationData) {
        return StringUtils.EMPTY;
    }

    @Override
    public Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData) {
        return runConfigurationData;
    }

    @Override
    public RunConfigurationDescription toConfigurationEntity(RunConfigurationDescription previousDescription) {
        return RunConfigurationDescription.from(groupName, getName(), Collections.emptyMap(),
                previousDescription.getProfileName());
    }

    @Override
    public boolean requiresExtraConfiguration() {
        return false;
    }
}
