package com.kms.katalon.activation.dialog;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;

public class ExpiredLicenseDialog extends AbstractDialog {

    private String message;

    public ExpiredLicenseDialog(Shell parentShell, String message) {
        super(parentShell, false);
        this.message = message;
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
    }

    @Override
    public String getDialogTitle() {
        return ApplicationMessageConstants.TITLE_KS_NOTIFICATION;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());

        Label lblText = new Label(composite, SWT.NONE);

        if (StringUtils.equals(message, ApplicationMessageConstants.ACTIVATION_ONLINE_INVALID)) {
            //Cannot connect to server, no response
            lblText.setText(ApplicationMessageConstants.LICENSE_EXPIRED_NO_MESSAGE + "\n"
                    + ApplicationMessageConstants.AUTO_CLOSE);
        } else {
            lblText.setText(MessageFormat.format(ApplicationMessageConstants.LICENSE_EXPIRED_MESSAGE, message) + "\n"
                    + ApplicationMessageConstants.AUTO_CLOSE);
        }
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ApplicationMessageConstants.BTN_ACKNOWLEDGE, true);
    }
}
