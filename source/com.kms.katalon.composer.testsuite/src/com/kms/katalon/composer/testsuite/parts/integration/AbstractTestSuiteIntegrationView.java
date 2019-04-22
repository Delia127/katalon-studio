package com.kms.katalon.composer.testsuite.parts.integration;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.parts.TestSuiteIntegrationPart;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public abstract class AbstractTestSuiteIntegrationView {
    protected TestSuiteEntity testSuiteEntity;
    protected MPart mpart;

    public AbstractTestSuiteIntegrationView(TestSuiteEntity testCaseEntity, MPart mpart) {
        this.testSuiteEntity = testCaseEntity;
        this.mpart = mpart;
    }

    public abstract Composite createContainer(Composite parent);

    public void setDirty(boolean dirty) {
        TestSuiteIntegrationPart integrationPart = (TestSuiteIntegrationPart) mpart.getObject();
        integrationPart.setDirty(dirty);
    }

    public boolean needsSaving() {
        return false;
    }

    public IntegratedEntity getEditingIntegrated() {
        return null;
    }
    
    public void onSaveSuccess(TestSuiteEntity testSuite) {
        
    }
    
    public void onSaveFailure(Exception failure) {
        
    }
}
