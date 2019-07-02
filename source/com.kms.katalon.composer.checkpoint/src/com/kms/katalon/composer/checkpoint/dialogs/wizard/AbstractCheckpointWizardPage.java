package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.eclipse.swt.graphics.Point;

import com.kms.katalon.composer.components.wizard.WizardPage;

public abstract class AbstractCheckpointWizardPage extends WizardPage {

    protected AbstractCheckpointWizardPage(String pageName, String title, String description) {
        super(pageName, title );
        setDescription(description);
        setMessage(description, INFORMATION);
    }

    protected abstract boolean isComplete();

    public abstract Point getPageSize();

}
