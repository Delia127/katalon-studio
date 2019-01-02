package com.kms.katalon.platform.internal.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.model.Integration;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseEntityImpl implements com.katalon.platform.api.model.TestCaseEntity {

    private final TestCaseEntity source;

    public TestCaseEntityImpl(TestCaseEntity source) {
        this.source = source;
    }

    @Override
    public String getFileLocation() {
        return source.getId() + source.getFileExtension();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getId();
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
    public List<Integration> getIntegrations() {
        return source.getIntegratedEntities().stream().map(i -> new IntegrationImpl(i)).collect(Collectors.toList());
    }

    @Override
    public String getComment() {
        return source.getComment();
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

    @Override
    public String getParentFolderId() {
        return source.getParentFolder().getIdForDisplay();
    }
}
