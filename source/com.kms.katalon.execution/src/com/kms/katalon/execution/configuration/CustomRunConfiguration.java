package com.kms.katalon.execution.configuration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.execution.collector.DriverConnectorCollector;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class CustomRunConfiguration extends AbstractRunConfiguration {
    private String name;
    private Map<String, IDriverConnector> driverConnectors;
    private Map<String, IRunConfiguration> runConfigurations;
    private File configFolder;
    protected String projectDir;
    
    public CustomRunConfiguration(String projectDir, String name) throws IOException, ExecutionException {        
        this.name = name;
        this.projectDir = projectDir;
        initConfigFolder(projectDir);
    }

    private void initConfigFolder(String projectFolderLocation) throws IOException, ExecutionException {
        setConfigFolder(getConfigFolder(projectFolderLocation));
        initDriverConnectors();
    }
    
    @Override
    public IRunConfiguration cloneConfig() throws IOException, ExecutionException {
        return new CustomRunConfiguration(projectDir, name);
    }

    private void initDriverConnectors() throws IOException, ExecutionException {
        driverConnectors = new LinkedHashMap<String, IDriverConnector>();
        runConfigurations = new HashMap<>();
        if (configFolder == null || !configFolder.exists()) {
            return;
        }
        driverConnectors = DriverConnectorCollector.getInstance().getDriverConnectors(getConfigFolder());
        for (IDriverConnector driverConnector : driverConnectors.values()) {
            String driverName = driverConnector.getDriverType().toString();
            IRunConfigurationContributor contributor = RunConfigurationCollector.getInstance().getRunContributor(driverName);
            
            try {
                IRunConfiguration runConfig = contributor.getRunConfiguration(projectDir, driverConnector);

                if (runConfig != null) {
                    runConfigurations.put(driverName, runConfig);
                }
            } catch (Exception ignored) {}
        }
    }

    private File getConfigFolder(String projectFolderLocation) {
        return new File(projectFolderLocation + File.separator
                + RunConfigurationCollector.CUSTOM_EXECUTION_CONFIG_ROOT_FOLDER_RELATIVE_PATH + File.separator + name);
    }

    @Override
    public Map<String, IDriverConnector> getDriverConnectors() {
        return driverConnectors;
    }

    public void clearAllDriverConnectors() {
        driverConnectors.clear();
    }

    public void addDriverConnector(String contributorName, IDriverConnector driverConnector) {
        if (driverConnector == null) {
            return;
        }
        driverConnectors.put(contributorName, driverConnector);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) throws IOException, ExecutionException {
        this.name = name;
        configFolder = getConfigFolder(getProjectFolderLocation());
        for (IDriverConnector driverConnector : driverConnectors.values()) {
            driverConnector.setParentFolderPath(configFolder.getAbsolutePath());
        }
    }

    public void save() throws IOException {
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        for (IDriverConnector driverConnector : driverConnectors.values()) {
            driverConnector.saveUserConfigProperties();
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
        for (IDriverConnector driverConnector : getDriverConnectors().values()) {
            if (!isFirst) {
                valueString.append(" + ");
            } else {
                isFirst = false;
            }
            valueString.append(driverConnector.getDriverType().toString() + ": " + driverConnector.toString());
        }
        return valueString.toString();
    }
    
    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException {
        Map<String, String> environmentVariables = new HashMap<>(super.getAdditionalEnvironmentVariables());
        for (IRunConfiguration runConfig : runConfigurations.values()) {
            environmentVariables.putAll(runConfig.getAdditionalEnvironmentVariables());
        }
        return environmentVariables;
    }
}
