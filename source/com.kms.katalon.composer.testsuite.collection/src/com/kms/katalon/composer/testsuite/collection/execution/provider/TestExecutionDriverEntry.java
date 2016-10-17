package com.kms.katalon.composer.testsuite.collection.execution.provider;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class TestExecutionDriverEntry extends TestExecutionEntryItem {

    final protected DriverType driverType;

    final protected String groupName;

    final protected String imageUrl;

    private TestExecutionDriverEntry(final DriverType driverType, final String groupName, final String imageUrl) {
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
    public RunConfigurationDescription toConfigurationEntity() {
        return RunConfigurationDescription.from(groupName, getName());
    }

    public static TestExecutionDriverEntry from(String groupName, DriverType driverType, String imageUrl) {
        return new TestExecutionDriverEntry(driverType, groupName, imageUrl);
    }
}
