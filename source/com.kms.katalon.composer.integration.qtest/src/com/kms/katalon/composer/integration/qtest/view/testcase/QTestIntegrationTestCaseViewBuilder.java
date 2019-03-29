package com.kms.katalon.composer.integration.qtest.view.testcase;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class QTestIntegrationTestCaseViewBuilder implements TestCaseIntegrationViewBuilder {

    @Override
	public AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart,
			SavableCompositePart parentPart) {
        return new QTestIntegrationTestCaseView(testCase, mpart);
    }

    @Override
    public String getName() {
        return "qTest";
    }

    @Override
    public boolean isEnabled(ProjectEntity projectEntity) {
        return QTestIntegrationUtil.isIntegrationEnable(projectEntity);
    }

}
