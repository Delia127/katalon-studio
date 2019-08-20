package com.kms.katalon.composer.components.impl.wizard;

import org.eclipse.swt.widgets.Composite;

public interface IWizardPageChangedListerner {
    public void handlePageChanged(WizardPageChangedEvent event);
    String getStepIndexAsString();

    boolean isChild();
}
