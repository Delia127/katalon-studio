package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.collector.DriverConnectorCollector;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.exception.ExecutionException;

public class CustomRunConfiguration extends AbstractRunConfiguration {
    private String name;
    private List<IDriverConnector> driverConnectors;
    private File configFolder;

    public CustomRunConfiguration(String name) throws IOException, ExecutionException {
        this.name = name;
        initConfigFolder(ProjectController.getInstance().getCurrentProject().getFolderLocation());
    }

    public CustomRunConfiguration(TestCaseEntity testCaseEntity, String name) throws IOException, ExecutionException {
        super(testCaseEntity);
        this.name = name;
        initConfigFolder(testCaseEntity.getProject().getFolderLocation());
    }

    public CustomRunConfiguration(TestSuiteEntity testSuiteEntity, String name) throws IOException, ExecutionException {
        super(testSuiteEntity);
        this.name = name;
        initConfigFolder(testSuiteEntity.getProject().getFolderLocation());
    }

    private void initConfigFolder(String projectFolderLocation) throws IOException, ExecutionException {
        setConfigFolder(getConfigFolder(projectFolderLocation));
        initDriverConnectors();
    }

    private void initDriverConnectors() throws IOException, ExecutionException {
        driverConnectors = new ArrayList<IDriverConnector>();
        if (configFolder == null || !configFolder.exists()) {
            return;
        }
        for (File file : configFolder.listFiles()) {
            IDriverConnector driverConnector = DriverConnectorCollector.getInstance().getDriverConnector(
                    file.getName(), configFolder.getAbsolutePath());
            if (driverConnector != null) {
                driverConnectors.add(driverConnector);
            }
        }
    }

    private File getConfigFolder(String projectFolderLocation) {
        return new File(projectFolderLocation + File.separator
                + RunConfigurationCollector.CUSTOM_EXECUTION_CONFIG_ROOT_FOLDLER_RELATIVE_PATH + File.separator + name);
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors.toArray(new IDriverConnector[driverConnectors.size()]);
    }

    public void clearAllDriverConnectors() {
        driverConnectors.clear();
    }

    public void addDriverConnector(IDriverConnector driverConnector) {
        if (driverConnector == null) {
            return;
        }
        driverConnectors.add(driverConnector);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) throws IOException, ExecutionException {
        this.name = name;
        configFolder = getConfigFolder(projectFolderLocation);
        for (IDriverConnector driverConnector : driverConnectors) {
            driverConnector.setParentFolderPath(configFolder.getAbsolutePath());
        }
    }

    public void save() throws IOException {
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        for (IDriverConnector driverConnector : driverConnectors) {
            driverConnector.saveDriverProperties();
        }
    }

    public void delete() throws IOException {
        if (configFolder.exists()) {
            FileUtils.deleteDirectory(configFolder);
        }
    }

    public File getConfigFolder() {
        return configFolder;
    }

    public void setConfigFolder(File configFolder) {
        this.configFolder = configFolder;
    }

    @Override
    public String toString() {
        StringBuilder valueString = new StringBuilder();
        boolean isFirst = true;
        for (IDriverConnector driverConnector : getDriverConnectors()) {
            if (!isFirst) {
                valueString.append(" + ");
            } else {
                isFirst = false;
            }
            valueString.append(driverConnector.getDriverType().toString() + ": " + driverConnector.toString());
        }
        return valueString.toString();
    }
}
