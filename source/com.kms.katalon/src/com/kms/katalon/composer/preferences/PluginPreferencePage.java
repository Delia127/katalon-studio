package com.kms.katalon.composer.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PluginPreferencePage extends PreferencePage {

    @Override
    protected Control createContents(Composite parent) {
        return new Composite(parent, SWT.NONE);
    }

}
