package com.kms.katalon.composer.testsuite.collection.view.builder;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testsuite.collection.part.AbstractTestSuiteCollectionUIDescriptionView;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public interface TestSuiteCollectionUIViewBuilder {
	String getName();
	
	boolean isEnabled(ProjectEntity project);
	
	AbstractTestSuiteCollectionUIDescriptionView getView(TestSuiteCollectionEntity testSuiteCollection, MPart mpart, MPart parentPart);
	
}
