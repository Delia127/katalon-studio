package com.kms.katalon.composer.testsuite.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface TestSuiteIntegrationViewBuilder {

    String getName();

    boolean isEnabled(ProjectEntity project);

    public AbstractTestSuiteIntegrationView getIntegrationView(TestSuiteEntity testSuite, MPart mpart, SavableCompositePart parentPart);
}
