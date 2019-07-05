package com.kms.katalon.composer.KatalonQuickStart;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;

public class NullDialog extends AbstractWizardPage implements WizardPage {

    public NullDialog() {

    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void createStepArea(Composite parent) {
    }

    @Override
    public void setInput(Map<String, Object> sharedData) {

    }

    @Override
    public void registerControlModifyListeners() {

    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public boolean autoFlip() {
        return false;
    }

    @Override
    public String getStepIndexAsString() {
        return "9";
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public boolean canFinish() {
        return true;
    }
}
