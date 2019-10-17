package com.kms.katalon.activation.dialog;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.constants.ApplicationMessageConstants;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;

public class WarningLicenseDialog extends AbstractDialog {
    private String message;

    public WarningLicenseDialog(Shell parentShell, String message) {
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
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());

        Label lblText = new Label(composite, SWT.NONE);
        lblText.setText(message);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, ApplicationMessageConstants.BTN_ACKNOWLEDGE, true);
    }
}
