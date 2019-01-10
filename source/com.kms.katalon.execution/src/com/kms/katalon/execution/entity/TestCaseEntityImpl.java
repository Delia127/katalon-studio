package com.kms.katalon.execution.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    	return new ArrayList<>();
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

    @Override
    public Integration getIntegration(String integrationName) {
    	return null;
    }

    @Override
    public File getScriptFile() {
    	return null;
    }

	@Override
	public String getTags() {
		return source.getTag();
	}
}
