package com.kms.katalon.composer.objectrepository.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public interface TestObjectIntegrationViewBuilder {
	String getName();

    boolean isEnabled(ProjectEntity project);

    AbstractTestCaseIntegrationView getIntegrationView(WebElementEntity testObject, MPart mpart, SavableCompositePart parentPart);

    int preferredOrder();
}
