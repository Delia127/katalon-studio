package com.kms.katalon.objectspy.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ObjectSpyPreferencePage extends FieldEditorPreferencePage {
    public ObjectSpyPreferencePage() {
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setFont(parent.getFont());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        composite.setLayoutData(gridData);
        addField(new IntegerFieldEditor(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_INSTANT_BROWSER_PORT,
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
