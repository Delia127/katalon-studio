package com.kms.katalon.composer.testsuite.view.builder;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testsuite.parts.AbstractTestSuiteUIDescriptionView;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface TestSuiteUIViewBuilder {
	
	String getName();
	
	boolean isEnabled(ProjectEntity project);
	
	AbstractTestSuiteUIDescriptionView getView(TestSuiteEntity testSuite, MPart mpart, SavableCompositePart parentPart);
	
}
