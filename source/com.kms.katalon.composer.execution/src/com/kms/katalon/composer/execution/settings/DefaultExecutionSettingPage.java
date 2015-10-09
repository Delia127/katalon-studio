package com.kms.katalon.composer.execution.settings;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DefaultExecutionSettingPage extends PreferencePage {
    public DefaultExecutionSettingPage() {
        noDefaultAndApplyButton();
    }
	@Override
	protected Control createContents(Composite parent) {
	    Composite fieldEditorParent = new Composite(parent, SWT.NONE);
		return fieldEditorParent;
	}
}
