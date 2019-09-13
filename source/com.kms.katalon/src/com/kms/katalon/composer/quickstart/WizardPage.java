package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.widgets.Composite;

public interface WizardPage {
    String getStepIndexAsString();

    boolean isChild();

    void createStepArea(Composite parent);
}
