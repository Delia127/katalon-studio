package com.kms.katalon.composer.integration.qtest.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public abstract class AbstractWizardPage implements IWizardPage {
    private Set<IWizardPageChangedListerner> fListeners;
    
    public Set<IWizardPageChangedListerner> getListeners() {
        if (fListeners == null) {
            fListeners = new LinkedHashSet<IWizardPageChangedListerner>();
        }
        return fListeners;
    }

    public void addChangedListeners(IWizardPageChangedListerner listener) {
        getListeners().add(listener);
    }
    
    public void removeListener(IWizardPageChangedListerner listener) {
        getListeners().remove(listener);
    }
    
    public void removeAllListeners() {
        getListeners().clear();
    }
    
    protected void firePageChanged() {
        for (IWizardPageChangedListerner listener : fListeners) {
            listener.handlePageChanged(new WizardPageChangedEvent(this, this));
        }
    }
    
    @Override
    public String getTitle() {
        return "";
    }
    
    @Override
    public final boolean canFinish() {
        return false;
    }
    
    protected void closeQuietly(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
}
