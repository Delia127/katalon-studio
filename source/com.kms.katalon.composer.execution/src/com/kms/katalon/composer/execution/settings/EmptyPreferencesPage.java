package com.kms.katalon.composer.execution.settings;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class EmptyPreferencesPage extends PreferencePage {

    public EmptyPreferencesPage() {
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        return new Label(parent, SWT.NONE);
    }
}