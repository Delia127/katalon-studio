package com.kms.katalon.composer.testcase.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.parts.TestCaseIntegrationPart;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public abstract class AbstractTestCaseIntegrationView {
	
	protected TestCaseEntity testCaseEntity;

	protected MPart mpart;
	
	public AbstractTestCaseIntegrationView(TestCaseEntity testCaseEntity, MPart mpart) {
		this.testCaseEntity = testCaseEntity;
		this.mpart = mpart;
	}
	
	public abstract Composite createContainer(Composite parent);
	
	public void setDirty(boolean dirty) {
		TestCaseIntegrationPart integrationPart = (TestCaseIntegrationPart) mpart.getObject();
		integrationPart.setDirty(dirty);
	}

	public boolean isDirty() {
		TestCaseIntegrationPart integrationPart = (TestCaseIntegrationPart) mpart.getObject();
		
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
}
