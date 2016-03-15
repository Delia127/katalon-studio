package com.kms.katalon.execution.collector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class DriverConnectorCollector {
    private static DriverConnectorCollector _instance;
    private List<IDriverConnectorContributor> driverConnectorContributorList;

    private DriverConnectorCollector() {
        driverConnectorContributorList = new ArrayList<IDriverConnectorContributor>();
    }

    public static DriverConnectorCollector getInstance() {
        if (_instance == null) {
            _instance = new DriverConnectorCollector();
        }
        return _instance;
    }

    public void addBuiltinDriverConnectorContributor(IDriverConnectorContributor driverConnectorContributor) {
        driverConnectorContributorList.add(driverConnectorContributor);
    }

    public Map<String, IDriverConnector> getDriverConnectors(File configFolder) throws IOException {
        Map<String, IDriverConnector> connectorCollectors = new LinkedHashMap<String, IDriverConnector>();
        for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributorList) {
            for (IDriverConnector driverConnector : driverConnectorContributor.getDriverConnector(configFolder
                    .getAbsolutePath())) {
                if (new File(configFolder, driverConnector.getSettingFileName()
                        + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION).exists()) {
                    connectorCollectors.put(driverConnectorContributor.getName(), driverConnector);
                    break;
                }
            }
        }

        return connectorCollectors;
    }

    public IDriverConnector getDriverConnector(String configFileName, String configFolderPath) throws IOException,
            ExecutionException {
        for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributorList) {
            for (IDriverConnector driverConnector : driverConnectorContributor.getDriverConnector(configFolderPath)) {
                if ((driverConnector.getSettingFileName() + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION)
                        .equals(configFileName)) {
                    return driverConnector;
                }
            }
        }
        return null;
    }

    public String getContributorName(IDriverConnector connector, String configFolderPath) throws IOException {
        for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributorList) {
            for (IDriverConnector existedConnector : driverConnectorContributor.getDriverConnector(configFolderPath)) {
                if (existedConnector.getSettingFileName().equals(connector.getSettingFileName())) {
                    return driverConnectorContributor.getName();
                }
            }
        }
        return null;
    }

    public IDriverConnectorContributor[] getAllBuiltinDriverConnectorContributors() {
        return driverConnectorContributorList.toArray(new IDriverConnectorContributor[driverConnectorContributorList
                .size()]);
    }
}
