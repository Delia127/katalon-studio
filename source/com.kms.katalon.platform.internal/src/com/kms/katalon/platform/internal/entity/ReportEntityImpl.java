package com.kms.katalon.platform.internal.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.model.Integration;
import com.kms.katalon.entity.report.ReportEntity;

public class ReportEntityImpl implements com.katalon.platform.api.model.ReportEntity {

    private ReportEntity source;

    public ReportEntityImpl(ReportEntity source) {
        this.source = source;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return source.getIdForDisplay();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getLocation();
    }

    @Override
    public String getFileLocation() {
        return source.getLocation();
    }

    @Override
    public List<Integration> getIntegrations() {
        return source.getIntegratedEntities().stream().map(i -> new IntegrationImpl(i)).collect(Collectors.toList());
    }

    @Override
    public Integration getIntegration(String integrationName) {
        if (StringUtils.isEmpty(integrationName)) {
            return null;
        }
        return source.getIntegratedEntities().stream().filter(i -> {
            return i.getProductName() != null && i.getProductName().equals(integrationName);
        }).map(i -> new IntegrationImpl(i)).findFirst().orElse(null);
    }

}
