package com.kms.katalon.composer.webui.setting;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class WebUiPerferencePage extends PreferencePage {
    public WebUiPerferencePage() {
        noDefaultAndApplyButton();
    }
    
    
	@Override
	protected Control createContents(Composite parent) {
	    Composite fieldEditorParent = new Composite(parent, SWT.NONE);
		return fieldEditorParent;
	}

}
