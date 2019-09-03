package com.kms.katalon.composer.project.preference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.constants.PluginOptions;
import com.kms.katalon.execution.setting.PluginSettingStore;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.composer.project.constants.StringConstants;

public class PluginPreferencePage extends PreferencePage {
    
    private PluginSettingStore pluginSettingStore;

    private Map<PluginOptions, Button> reloadOptionButtons;
    
    public PluginPreferencePage() {
        pluginSettingStore = new PluginSettingStore(ProjectController.getInstance().getCurrentProject());
        reloadOptionButtons = new HashMap<>();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        
        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        Group grpReloadPluginOptions = new Group(container, SWT.NONE);
        grpReloadPluginOptions.setText(StringConstants.PAGE_GRP_PLUGIN_REPOSITORY);
        grpReloadPluginOptions.setLayout(new GridLayout(1, false));
        grpReloadPluginOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Button rbtnOnlineAndOfflineOpt = new Button(grpReloadPluginOptions, SWT.RADIO);
        rbtnOnlineAndOfflineOpt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        rbtnOnlineAndOfflineOpt.setText(StringConstants.PAGE_OPTION_RELOAD_ONLINE_AND_OFFLINE);
        reloadOptionButtons.put(PluginOptions.ONLINE_AND_OFFLINE, rbtnOnlineAndOfflineOpt);

        Button rbtnOnlineOpt = new Button(grpReloadPluginOptions, SWT.RADIO);
        rbtnOnlineOpt.setText(StringConstants.PAGE_OPTION_RELOAD_ONLINE);
        reloadOptionButtons.put(PluginOptions.ONLINE, rbtnOnlineOpt);

        Button rbtnOfflineOpt = new Button(grpReloadPluginOptions, SWT.RADIO);
        rbtnOfflineOpt.setText(StringConstants.PAGE_OPTION_RELOAD_OFFLINE);
        reloadOptionButtons.put(PluginOptions.OFFLINE, rbtnOfflineOpt);

        PluginOptions reloadOption = getReloadOption();
        if (reloadOption != null) {
            reloadOptionButtons.get(reloadOption).setSelection(true);
        }
        
        return composite;
    }
    
    @Override
    public boolean performOk() {
        if (!isControlCreated()) {
            return super.performOk();
        }
        boolean performOk = super.performOk();
        if (performOk) {
            PluginOptions selectedReloadOption = reloadOptionButtons.entrySet().stream()
                    .filter(entry -> entry.getValue().getSelection()).findFirst().get().getKey();
            setReloadOption(selectedReloadOption);
        }

        return performOk;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(PreferenceConstants.PLUGIN_RELOAD_OPTION);
        PluginOptions reloadOption = getReloadOption();
        if (reloadOption != null) {
            reloadOptionButtons.get(reloadOption).setSelection(true);
        }
    }
    
    public ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }
    
    private PluginOptions getReloadOption() {
    	try {
			return pluginSettingStore.getReloadOption();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    private void setReloadOption(PluginOptions option) {
        try {
			pluginSettingStore.setReloadOption(option);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
