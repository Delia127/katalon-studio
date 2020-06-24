package com.kms.katalon.execution.collection.provider;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.execution.util.MapUtil;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class TestExecutionDriverEntry extends TestExecutionEntryItem {

    final protected DriverType driverType;

    final protected String groupName;

    final protected String imageUrl;

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

    @Override
    public RunConfigurationDescription toConfigurationEntity(RunConfigurationDescription previousDescription) {
        Map<String, String> runConfigurationData = previousDescription != null
                ? previousDescription.getRunConfigurationData() : Collections.emptyMap();
        String previousProfileName = previousDescription != null ? previousDescription.getProfileName()
                : StringUtils.EMPTY;
        return RunConfigurationDescription.from(groupName, getName(), runConfigurationData, previousProfileName);
    }

    public static TestExecutionDriverEntry from(String groupName, DriverType driverType, String imageUrl) {
        return new TestExecutionDriverEntry(driverType, groupName, imageUrl);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(Composite parent) {
        return null;
    }

    @Override
    public String displayRunConfigurationData(Map<String, String> runConfigurationData) {
        return MapUtil.buildStringForMap(runConfigurationData);
    }

    @Override
    public Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData) {
        return runConfigurationData;
    }

    @Override
    public boolean requiresExtraConfiguration() {
        return false;
    }
}
