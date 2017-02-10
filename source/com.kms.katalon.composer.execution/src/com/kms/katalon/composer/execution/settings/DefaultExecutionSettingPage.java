package com.kms.katalon.composer.execution.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.constants.DocumentationMessageConstants;

public class DefaultExecutionSettingPage extends PreferencePageWithHelp {
    public DefaultExecutionSettingPage() {
        noDefaultAndApplyButton();
    }
	@Override
	protected Control createContents(Composite parent) {
	    Composite fieldEditorParent = new Composite(parent, SWT.NONE);
		return fieldEditorParent;
	}
	
    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EXECUTION;
    }
}
