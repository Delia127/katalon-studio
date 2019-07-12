package com.kms.katalon.composer.components.impl.wizard;

import java.util.ArrayList;
import java.util.List;

public class WizardManager {
    private List<IWizardPage> wizardPages;
    private int currentIdx;

    public WizardManager() {
        currentIdx = 0;
    }

    public WizardManager addPage(IWizardPage wizardPage) {
        getWizardPages().add(wizardPage);
        return this;
    }

    public IWizardPage nextPage() {
        do {
            currentIdx++;
        } while (currentIdx <= getWizardPages().size() - 1 && getCurrentPage().autoFlip());

        return getCurrentPage();
    }
    
    public IWizardPage choosenPage(int selection) {
        do {
            currentIdx = selection; 
        } while (currentIdx <= getWizardPages().size() - 1 && getCurrentPage().autoFlip());

        return getCurrentPage();
    }

    public IWizardPage backPage() {
        do {
            currentIdx--;
        } while (currentIdx >= 0 && getCurrentPage().autoFlip());

        return getCurrentPage();
    }

    public IWizardPage getCurrentPage() {
        if (currentIdx < 0 || currentIdx > getWizardPages().size() - 1) {
            return null;
        }
        return getWizardPages().get(currentIdx);
    }

    public List<IWizardPage> getWizardPages() {
        if (wizardPages == null) {
            wizardPages = new ArrayList<>();
        }
        return wizardPages;
    }

    public void setWizardPages(List<IWizardPage> wizardPages) {
        this.wizardPages = wizardPages;
    }
}
