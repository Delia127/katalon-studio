package com.kms.katalon.composer.components.impl.wizard;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

public interface IWizardPage {
    String getTitle();

    boolean canFlipToNextPage();
    
    boolean canFinish();

    void createStepArea(Composite parent);

    void setInput(final Map<String, Object> sharedData);

    void registerControlModifyListeners();
    
    Map<String, Object> storeControlStates();
}
