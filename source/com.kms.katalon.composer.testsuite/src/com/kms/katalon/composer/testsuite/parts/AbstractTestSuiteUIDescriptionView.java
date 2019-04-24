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
	
	/**
	 * This method should be called after {@link AbstractTestSuiteUIDescriptionView#createContainer} has returned
	 * in order to initialize data for your controls
	 */
	public abstract void postContainerCreated();
	
	public void setDirty(boolean dirty) {
		TestSuitePart testSuitePart = (TestSuitePart) mpart.getObject();
		if(testSuitePart != null){
			testSuitePart.setDirty(dirty);
		}
	}
	
	public boolean isDirty() {
		TestSuitePart testSuitePart = (TestSuitePart) mpart.getObject();
		if(testSuitePart != null){
			return testSuitePart.getMPart().isDirty();
		}
		return false;
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
