package com.kms.katalon.execution.configuration;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IDriverConnector;

public class CustomRunConfiguration extends AbstractRunConfiguration {
    private String name;
    private List<IDriverConnector> driverConnectors;

    public CustomRunConfiguration(TestCaseEntity testCaseEntity, String name) {
        super(testCaseEntity);
        this.name = name;
        driverConnectors = new ArrayList<IDriverConnector>();
    }

    public CustomRunConfiguration(TestSuiteEntity testSuiteEntity, String name) {
        super(testSuiteEntity);
        this.name = name;
        driverConnectors = new ArrayList<IDriverConnector>();
    }

    @Override
    public IDriverConnector[] getDriverConnectors() {
        return driverConnectors.toArray(new IDriverConnector[driverConnectors.size()]);
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

}
