package com.kms.katalon.composer.webui.setting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;

public class TestDesignPerferencePage extends PreferencePageWithHelp {
    public TestDesignPerferencePage() {
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
}
