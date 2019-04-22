package com.kms.katalon.composer.integration.qtest.view.testsuite;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.testsuite.parts.integration.AbstractTestSuiteIntegrationView;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class QTestIntegrationTestSuiteViewBuilder implements TestSuiteIntegrationViewBuilder {

    @Override
    public AbstractTestSuiteIntegrationView getIntegrationView(TestSuiteEntity testSuite, MPart mpart, SavableCompositePart parentPart) {
        return new QTestIntegrationTestSuiteView(testSuite, mpart);
    }

    @Override
    public String getName() {
        return "qTest";
    }

    @Override
    public boolean isEnabled(ProjectEntity project) {
        return QTestIntegrationUtil.isIntegrationEnable(project);
    }
}
