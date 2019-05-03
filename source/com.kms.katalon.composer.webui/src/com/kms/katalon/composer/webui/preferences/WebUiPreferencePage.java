package com.kms.katalon.composer.webui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.webui.constants.PreferenceConstants;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class WebUiPreferencePage extends PreferencePage {

    private Button chkAutoUpdateWebDrivers;
    
    private Composite parentComposite;
    
    @Override
    protected Control createContents(Composite parent) {
        parentComposite = new Composite(parent, SWT.NONE);
        parentComposite.setLayout(new GridLayout(1, false));
        
        chkAutoUpdateWebDrivers = new Button(parentComposite, SWT.CHECK);
        chkAutoUpdateWebDrivers.setText(StringConstants.PAGE_PREF_AUTO_UPDATE_WEBDRIVERS);
        
        initialize();
        
        return parentComposite;
    }
    
    @Override
    public IPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
    }
    
    private void initialize() {
        IPreferenceStore prefStore = getPreferenceStore();
        if (!prefStore.contains(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS)) {
            prefStore.setDefault(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS, false);
        }
        chkAutoUpdateWebDrivers.setSelection(prefStore.getBoolean(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS));
        
    }
    
    @Override
    protected void performDefaults() {
        if (!isControlCreated()) {
            return;
        }
        IPreferenceStore prefStore = getPreferenceStore();
        prefStore.setToDefault(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS);
        chkAutoUpdateWebDrivers.setSelection(prefStore.getBoolean(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS));
        super.performDefaults();
    }
    
    @Override
    protected void performApply() {
        if (!isControlCreated()) {
            super.performApply();
            return;
        }
        getPreferenceStore().setValue(PreferenceConstants.AUTO_UPDATE_WEBDRIVERS,
                chkAutoUpdateWebDrivers.getSelection());
    }
    
    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}
