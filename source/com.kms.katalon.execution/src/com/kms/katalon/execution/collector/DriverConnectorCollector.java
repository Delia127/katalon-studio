package com.kms.katalon.execution.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public IDriverConnector getDriverConnector(String configFileName, String configFolderPath) throws IOException,
            ExecutionException {
        for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributorList) {
            if ((driverConnectorContributor.getConfigFileName() + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION).equals(configFileName)) {
                return driverConnectorContributor.getDriverConnector(configFolderPath);
            }
        }
        return null;
    }

    public IDriverConnectorContributor[] getAllBuiltinDriverConnectorContributors() {
        return driverConnectorContributorList.toArray(new IDriverConnectorContributor[driverConnectorContributorList
                .size()]);
    }
}
