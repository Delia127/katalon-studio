package com.kms.katalon.platform.internal.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.model.TestSuiteCollectionEntity;
import com.katalon.platform.api.model.TestSuiteEntity;

public class TestSuiteCollectionEntityImpl implements TestSuiteCollectionEntity {

    private com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity source;

    public TestSuiteCollectionEntityImpl(com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity source) {
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
    public String getComment() {
        return StringUtils.EMPTY;
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
    public String getTags() {
        return source.getTag();
    }

    @Override
    public List<TestSuiteEntity> getTestSuites() {
        return source.getTestSuiteRunConfigurations()
                .stream()
                .map(runConfig -> new TestSuiteEntityImpl(runConfig.getTestSuiteEntity()))
                .collect(Collectors.toList());
    }

}
