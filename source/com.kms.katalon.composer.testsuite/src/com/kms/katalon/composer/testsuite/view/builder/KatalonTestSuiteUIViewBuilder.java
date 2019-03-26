package com.kms.katalon.composer.testsuite.view.builder;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testsuite.parts.AbstractTestSuiteUIDescriptionView;
import com.kms.katalon.composer.view.KatalonTestSuiteUIView;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class KatalonTestSuiteUIViewBuilder implements TestSuiteUIViewBuilder {

	@Override
	public String getName() {
		return "Execution Information";
	}

	@Override
	public boolean isEnabled(ProjectEntity project) {
		return true;
	}

	@Override
	public AbstractTestSuiteUIDescriptionView getView(TestSuiteEntity testSuite, MPart mpart,
			SavableCompositePart parentPart) {
		return new KatalonTestSuiteUIView(testSuite, mpart);
	}	

}
