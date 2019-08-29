package com.kms.katalon.composer.project.preference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webservice.setting.SSLCertificateOption;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;
import com.kms.katalon.execution.constants.PluginOptions;
import com.kms.katalon.execution.setting.PluginSettingStore;

public class PluginPreferencePage extends PreferencePage {

    private Composite container;
    
    private Map<PluginOptions, Button> reloadOptionButtons;

    private PluginSettingStore pluginSettingStore;
    
    public PluginPreferencePage() {
    	pluginSettingStore = new PluginSettingStore(ProjectController.getInstance().getCurrentProject());
    	reloadOptionButtons = new HashMap<>();
    }
    
    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group grpReloadPluginOptions = new Group(container, SWT.NONE);
        grpReloadPluginOptions.setText(StringConstants.PLUGINS_REPOSITORY);
        grpReloadPluginOptions.setLayout(new GridLayout(1, false));
        grpReloadPluginOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button rbtnOnlineAndOfflineOpt = new Button(grpReloadPluginOptions, SWT.RADIO);
        rbtnOnlineAndOfflineOpt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        rbtnOnlineAndOfflineOpt.setText(StringConstants.PLUGIN_ONLINE_AND_OFFLINE);
        reloadOptionButtons.put(PluginOptions.ONLINE_AND_OFFLINE, rbtnOnlineAndOfflineOpt);

        Button rbtnOnlineOpt = new Button(grpReloadPluginOptions, SWT.RADIO);
        rbtnOnlineOpt.setText(StringConstants.PLUGIN_ONLINE);
        reloadOptionButtons.put(PluginOptions.ONLINE, rbtnOnlineOpt);

        Button rbtnOfflineOpt = new Button(grpReloadPluginOptions, SWT.RADIO);
        rbtnOfflineOpt.setText(StringConstants.PLUGIN_OFFLINE);
        reloadOptionButtons.put(PluginOptions.OFFLINE, rbtnOfflineOpt);

		setInput();
        return container;
    }
    
    private void setInput() {
		PluginOptions reloadOption = null;
		try {
			reloadOption = pluginSettingStore.getdReloadPluginOption();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (reloadOption != null) {
	    	reloadOptionButtons.get(reloadOption).setSelection(true);
		}
    	
    }

    private boolean isNotOpenedYet() {
        return container == null || container.isDisposed();
    }

    @Override
    protected void performDefaults() {
        setInput();
    }

    @Override
    public boolean performOk() {
        if (isNotOpenedYet()) {
            return true;
        }
        boolean valid = true;
        if (valid) {
            savePluginSettings();
        }
        return valid;
    }
    
    private void savePluginSettings() {
    	PluginOptions selectedReloadOption = reloadOptionButtons
    			.entrySet()
    			.stream()
    			.filter(entry -> entry.getValue().getSelection())
    			.findFirst()
    			.get()
    			.getKey();
		try {
			pluginSettingStore.setReloadPluginOption(selectedReloadOption);
		} catch (IOException e) {
			return;
		}
    }
}
