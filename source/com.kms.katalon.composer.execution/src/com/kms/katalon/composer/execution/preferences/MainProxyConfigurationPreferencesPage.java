package com.kms.katalon.composer.execution.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.constants.MessageConstants;

public class MainProxyConfigurationPreferencesPage extends PreferencePageWithHelp {

    @Override
    protected Control createContents(Composite parent) {
        Label lblGuideMessage = new Label(parent, SWT.WRAP);
        GridData gdGuideMessage = new GridData(SWT.LEFT, SWT.TOP, true, false);
        gdGuideMessage.widthHint = 500;
        lblGuideMessage.setLayoutData(gdGuideMessage);
        lblGuideMessage.setText(MessageConstants.LBL_CHANGE_PROXY_CONFIGURATIONS);
        return parent;
    }

    @Override
    protected Composite createDefaultButtonBarComposite(Composite content) {
        Composite defaultButtonBarComposite = super.createDefaultButtonBarComposite(content);
        defaultButtonBarComposite.setVisible(false);
        return defaultButtonBarComposite;
    }
}
