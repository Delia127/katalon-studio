package com.kms.katalon.composer.webui.recorder.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class RecorderPreferencePage extends FieldEditorPreferencePage {
    public RecorderPreferencePage() {
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setFont(parent.getFont());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        composite.setLayoutData(gridData);
        addField(new IntegerFieldEditor(RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_PORT,
                StringConstants.PREF_LBL_INSTANT_BROWSER_PORT, composite));
        initialize();
        checkState();
        return composite;
    }
    
    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
