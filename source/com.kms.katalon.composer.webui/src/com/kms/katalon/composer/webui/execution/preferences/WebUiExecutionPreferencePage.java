package com.kms.katalon.composer.webui.execution.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.constants.PreferenceConstants;

public class WebUiExecutionPreferencePage extends PreferencePage {
    private Text txtWaitForIEHanging;

    private Composite fieldEditorParent;

    public WebUiExecutionPreferencePage() {
    }

    @Override
    protected Control createContents(Composite parent) {
        fieldEditorParent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fieldEditorParent.setLayout(layout);

        Composite composite = new Composite(fieldEditorParent, SWT.NONE);
        GridLayout glComposite = new GridLayout(2, false);
        composite.setLayout(glComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblWaitForIEHanging = new Label(composite, SWT.NONE);
        lblWaitForIEHanging.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblWaitForIEHanging.setText(StringConstants.PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT);

        txtWaitForIEHanging = new Text(composite, SWT.BORDER);
        txtWaitForIEHanging.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        initialize();

        return fieldEditorParent;
    }

    private void initialize() {
        txtWaitForIEHanging.setText(Integer.toString(getPreferenceStore().getInt(
                PreferenceConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING)));
    }

    @Override
    protected void performDefaults() {
        if (fieldEditorParent == null) {
            return;
        }
        super.performDefaults();
    }

    @Override
    protected void performApply() {
        if (fieldEditorParent == null) return;

        if (txtWaitForIEHanging != null) {
            getPreferenceStore().setValue(PreferenceConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING,
                    Integer.parseInt(txtWaitForIEHanging.getText()));
        }
    }

    public boolean performOk() {
        boolean result = super.performOk();
        if (result) {
            if (isValid()) {
                performApply();
            }
        }
        return true;
    }
}
