package com.kms.katalon.composer.webservice.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;

public class WebServiceSettingPage extends PreferencePageWithHelp {

    private Composite container;

    public WebServiceSettingPage() {
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        return container;
    }

}
