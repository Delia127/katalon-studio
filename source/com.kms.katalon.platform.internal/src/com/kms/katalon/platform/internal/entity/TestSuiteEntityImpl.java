package com.kms.katalon.platform.internal.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.model.Integration;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteEntityImpl implements com.katalon.platform.api.model.TestSuiteEntity {

    private final TestSuiteEntity source;

    public TestSuiteEntityImpl(TestSuiteEntity source) {
        this.source = source;
    }

    @Override
    public List<Integration> getIntegrations() {
        return source.getIntegratedEntities()
                .stream()
                .map(i -> new IntegrationImpl(i))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

    @Override
    public String getParentFolderId() {
        return source.getParentFolder().getIdForDisplay();
    }

    @Override
    public Integration getIntegration(String integrationName) {
        return source.getIntegratedEntities()
                .stream()
                .filter(i -> i.getProductName().equals(integrationName))
                .map(i -> new IntegrationImpl(i))
                .findFirst()
                .orElseGet(null);
    }

    @Override
    public String getFileLocation() {
        return source.getId();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getLocation();
    }

    @Override
    public String getId() {
        return source.getIdForDisplay();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public boolean getIsRerun() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getMailRecepient() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isPageLoadTimeoutDefault() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int numberOfRerun() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean rerunFailedTestCasesOnly() {
        // TODO Auto-generated method stub
        return false;
    }
}
