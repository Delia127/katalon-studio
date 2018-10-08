package com.kms.katalon.composer.execution.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.constants.DocumentationMessageConstants;

public class ExecutionSettingPage extends PreferencePageWithHelp {
    public ExecutionSettingPage() {
        noDefaultAndApplyButton();
    }
    
	@Override
	protected Control createContents(Composite parent) {
	    Composite fieldEditorParent = new Composite(parent, SWT.NONE);
		return fieldEditorParent;
	}

	@Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EXECUTION;
    }
}
