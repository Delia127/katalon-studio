package com.kms.katalon.composer.objectrepository.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.objectrepository.part.TestObjectIntegrationPart;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public abstract class AbstractTestObjectIntegrationView {

	protected WebElementEntity testObjectEntity;

	protected MPart mpart;

	public AbstractTestObjectIntegrationView(WebElementEntity testObjectEntity, MPart mpart) {
		this.testObjectEntity = testObjectEntity;
		this.mpart = mpart;
	}

	public abstract Composite createContainer(Composite parent);

	public void setDirty(boolean dirty) {
		TestObjectIntegrationPart integrationPart = (TestObjectIntegrationPart) mpart.getObject();
		integrationPart.setDirty(dirty);
	}

	public boolean isDirty() {
		TestObjectIntegrationPart integrationPart = (TestObjectIntegrationPart) mpart.getObject();
		return integrationPart.isParentDirty();
	}

	public boolean hasDocumentation() {
		return false;
	}

	public String getDocumentationUrl() {
		return "";
	}

	public boolean needsSaving() {
		return false;
	}

	public IntegratedEntity getEditingIntegrated() {
		return null;
	}

	public void onSaveSuccess(TestCaseEntity testCase) {

	}

	public void onSaveFailure(Exception failure) {

	}
}
