package com.kms.katalon.composer.testcase.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public interface TestCaseIntegrationViewBuilder {
    String getName();

    boolean isEnabled(ProjectEntity project);

    AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart, SavableCompositePart parentPart);
}
