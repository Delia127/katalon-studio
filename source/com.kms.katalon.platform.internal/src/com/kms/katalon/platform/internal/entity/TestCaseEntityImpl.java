package com.kms.katalon.platform.internal.entity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;

import com.katalon.platform.api.model.Integration;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.controller.TestCaseController;
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

    @Override
    public Integration getIntegration(String integrationName) {
        if (StringUtils.isEmpty(integrationName)) {
            return null;
        }
        return source.getIntegratedEntities()
                .stream()
                .filter(i -> {
                    return i.getProductName() != null && i.getProductName().equals(integrationName);
                }).map(i -> new IntegrationImpl(i))
                .findFirst()
                .orElse(null);
    }

    @Override
    public File getScriptFile() {
        try {
            GroovyGuiUtil.getOrCreateGroovyScriptForTestCase(source);

            String testCaseFilePath = TestCaseController.getInstance().getGroovyScriptFilePath(source);
            if (StringUtils.isNotEmpty(testCaseFilePath)) {
                return new File(testCaseFilePath);
            }
            return null;
        } catch (CoreException | IOException e) {
            return null;
        }
    }

	@Override
	public String getTags() {
		return source.getTag();
	}
}
