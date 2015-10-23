package com.kms.katalon.composer.integration.qtest.wizard;

import java.util.EventObject;

public class WizardPageChangedEvent extends EventObject {
    
    private static final long serialVersionUID = 4111558212666210927L;
    private IWizardPage fWizardPage;
    
    public WizardPageChangedEvent(Object source, IWizardPage page) {
        super(source);
        setWizardPage(page);
    }

    private void setWizardPage(IWizardPage page) {
        fWizardPage = page;
    }

    public IWizardPage getWizardPage() {
        return fWizardPage;
    }
}
