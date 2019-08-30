package com.kms.katalon.composer.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PluginOptions;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class PluginPreferencePage extends PreferencePage {

    private Text txtPluginDirectory;

    private ScopedPreferenceStore prefStore;

    private Map<PluginOptions, Button> reloadOptionButtons;

    public PluginPreferencePage() {
        prefStore = getPreferenceStore();
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

        Label lblPluginDirectory = new Label(composite, SWT.NONE);
        lblPluginDirectory.setText(StringConstants.PAGE_LBL_PLUGIN_DIRECTORY);

        txtPluginDirectory = new Text(composite, SWT.BORDER);
        txtPluginDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtPluginDirectory.setText(prefStore.getString(PreferenceConstants.PLUGIN_DIRECTORY));

        Button btnBrowseFolder = new Button(composite, SWT.PUSH);
        btnBrowseFolder.setText(StringConstants.PAGE_BTN_BROWSE_FOLDER);
        btnBrowseFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
                String directoryLocation = directoryDialog.open();
                txtPluginDirectory.setText(directoryLocation);
            }
        });
        
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

        PluginOptions reloadOption = PluginOptions.valueOf(prefStore.getString(PreferenceConstants.PLUGIN_RELOAD_OPTION));
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
            prefStore.setValue(PreferenceConstants.PLUGIN_DIRECTORY, txtPluginDirectory.getText());
            PluginOptions selectedReloadOption = reloadOptionButtons.entrySet().stream()
                    .filter(entry -> entry.getValue().getSelection()).findFirst().get().getKey();
            prefStore.setValue(PreferenceConstants.PLUGIN_RELOAD_OPTION, selectedReloadOption.name());
        }

        return performOk;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(PreferenceConstants.PLUGIN_DIRECTORY);
        getPreferenceStore().setToDefault(PreferenceConstants.PLUGIN_RELOAD_OPTION);
        txtPluginDirectory.setText(prefStore.getString(PreferenceConstants.PLUGIN_DIRECTORY));
        PluginOptions reloadOption = PluginOptions.valueOf(prefStore.getString(PreferenceConstants.PLUGIN_RELOAD_OPTION));
        if (reloadOption != null) {
            reloadOptionButtons.get(reloadOption).setSelection(true);
        }
    }

    public ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }

}
