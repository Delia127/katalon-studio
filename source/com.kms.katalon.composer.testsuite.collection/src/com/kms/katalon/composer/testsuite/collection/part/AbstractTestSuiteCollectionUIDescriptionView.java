package com.kms.katalon.composer.testsuite.collection.part;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.parts.AbstractTestSuiteUIDescriptionView;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public abstract class AbstractTestSuiteCollectionUIDescriptionView {
	
	protected TestSuiteCollectionEntity testSuiteCollectionEntity;
	
	protected MPart mpart;
	
	public AbstractTestSuiteCollectionUIDescriptionView(TestSuiteCollectionEntity testSuiteCollectionEntity, MPart mpart) {
		this.testSuiteCollectionEntity = testSuiteCollectionEntity;
		this.mpart = mpart;
	}
	
	public abstract Composite createContainer(Composite parent);
	
	/**
	 * This method should be called after {@link AbstractTestSuiteUIDescriptionView#createContainer} has returned
	 * in order to initialize data for your controls
	 */
	public abstract void postContainerCreated();
	
	public void setDirty(boolean dirty) {
		TestSuiteCollectionPart testSuiteCollectionPart = (TestSuiteCollectionPart) mpart.getObject();
		if(testSuiteCollectionPart != null){
			testSuiteCollectionPart.setDirty(dirty);
		}
	}
	
	public boolean isDirty() {
		TestSuiteCollectionPart testSuiteCollectionPart = (TestSuiteCollectionPart) mpart.getObject();
		if(testSuiteCollectionPart != null){
			return testSuiteCollectionPart.getMPart().isDirty();
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
	
	public void onSaveSuccess(TestSuiteCollectionEntity testSuite) {
		
	}
	
	public void onSaveFailure(Exception failure) {
		
	}
}
