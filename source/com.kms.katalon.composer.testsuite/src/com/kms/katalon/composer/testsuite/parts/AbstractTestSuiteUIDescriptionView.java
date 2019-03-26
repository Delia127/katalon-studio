package com.kms.katalon.composer.testsuite.parts;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public abstract class AbstractTestSuiteUIDescriptionView {
	
	protected TestSuiteEntity testSuiteEntity;
	
	protected MPart mpart;
	
	public AbstractTestSuiteUIDescriptionView(TestSuiteEntity testSuiteEntity, MPart mpart) {
		this.testSuiteEntity = testSuiteEntity;
		this.mpart = mpart;
	}
	
	public abstract Composite createContainer(Composite parent);
	
	public void setDirty(boolean dirty) {
		TestSuitePart testSuitePart = (TestSuitePart) mpart.getObject();
		testSuitePart.setDirty(dirty);
	}
	
	public boolean isDirty() {
		TestSuitePart testSuitePart = (TestSuitePart) mpart.getObject();
		return testSuitePart.getMPart().isDirty();
	}
	
	public boolean hasDocumentation() {
		return false;
	}
	
	public String getDocumentationUrl() {
		return "";
	}
	
	public boolean needSaving() { 
		return false;
	}
	
	public void onSaveSuccess(TestSuiteEntity testSuite) {
		
	}
	
	public void onSaveFailure(Exception failure) {
		
	}
}
