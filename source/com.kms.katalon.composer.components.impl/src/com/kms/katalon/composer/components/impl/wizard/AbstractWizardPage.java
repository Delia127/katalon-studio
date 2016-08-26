package com.kms.katalon.composer.components.impl.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public abstract class AbstractWizardPage implements IWizardPage {
    private Set<IWizardPageChangedListerner> fListeners;
    
    public Set<IWizardPageChangedListerner> getListeners() {
        if (fListeners == null) {
            fListeners = new LinkedHashSet<>();
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
    public void setInput(Map<String, Object> sharedData) {
    }

    @Override
    public void registerControlModifyListeners() {
    }

    @Override
    public Map<String, Object> storeControlStates() {
        return null;
    }
    
    @Override
    public String getTitle() {
        return StringUtils.EMPTY;
    }
    
    @Override
    public boolean canFinish() {
        return false;
    }
    
    protected void closeQuietlyWithLog(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
}
