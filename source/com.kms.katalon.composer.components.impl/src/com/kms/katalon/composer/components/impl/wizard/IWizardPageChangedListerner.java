package com.kms.katalon.composer.components.impl.wizard;

public interface IWizardPageChangedListerner {
    public void handlePageChanged(WizardPageChangedEvent event);
    String getStepIndexAsString();

    boolean isChild();
    void finishPressed();
}
