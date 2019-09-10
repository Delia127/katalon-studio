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
    
    private Composite parentComposite;
    
    @Override
    protected Control createContents(Composite parent) {
        parentComposite = new Composite(parent, SWT.NONE);
        parentComposite.setLayout(new GridLayout(1, false));
        
        initialize();
        
        return parentComposite;
    }
    
    @Override
    public IPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
    }
    
    private void initialize() {
        IPreferenceStore prefStore = getPreferenceStore();
    }
    
    @Override
    protected void performDefaults() {
        if (!isControlCreated()) {
            return;
        }
        super.performDefaults();
    }
    
    @Override
    protected void performApply() {
        if (!isControlCreated()) {
            return;
        }
    }
    
    @Override
    public boolean performOk() {
        performApply();
        return super.performOk();
    }
}
