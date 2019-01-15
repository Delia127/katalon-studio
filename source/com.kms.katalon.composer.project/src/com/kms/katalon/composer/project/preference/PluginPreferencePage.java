package com.kms.katalon.composer.project.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PluginPreferencePage extends PreferencePage {

    @Override
    protected Control createContents(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());

        return composite;
    }
}
