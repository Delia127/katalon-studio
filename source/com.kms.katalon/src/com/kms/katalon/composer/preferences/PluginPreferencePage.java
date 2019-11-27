package com.kms.katalon.composer.preferences;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class PluginPreferencePage extends PreferencePage {

    private Text txtPluginDirectory;

    private ScopedPreferenceStore prefStore;

    public PluginPreferencePage() {
        prefStore = getPreferenceStore();
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
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 450;
        txtPluginDirectory.setLayoutData(gridData);
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
        }

        return performOk;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        getPreferenceStore().setToDefault(PreferenceConstants.PLUGIN_DIRECTORY);
        txtPluginDirectory.setText(prefStore.getString(PreferenceConstants.PLUGIN_DIRECTORY));
    }

    public ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }

}
