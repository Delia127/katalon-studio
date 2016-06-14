package com.kms.katalon.composer.webui.recorder.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
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
        composite.setLayout(new GridLayout());
        composite.setFont(parent.getFont());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        composite.setLayoutData(gridData);
        addPortField(composite);
        addField(new BooleanFieldEditor(RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_DO_NOT_SHOW_AGAIN,
                StringConstants.PREF_LBL_INSTANT_BROWSER_DO_NOT_SHOW_WARNING_DIALOG, composite));
        initialize();
        checkState();
        return composite;
    }

    private void addPortField(Composite composite) {
        Composite portComposite = new Composite(composite, SWT.NONE);
        composite.setLayout(new GridLayout());
        GridData portCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        portCompositeGridData.horizontalSpan = 1;
        portComposite.setLayoutData(portCompositeGridData);
        addField(new IntegerFieldEditor(RecorderPreferenceConstants.WEBUI_RECORDER_INSTANT_BROWSER_PORT,
                StringConstants.PREF_LBL_INSTANT_BROWSER_PORT, portComposite));
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
