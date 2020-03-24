package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.tracking.service.Trackings;

public class WarningKSEFeatureAccessDialog extends Dialog {

    private String message;

    private Link lnkMessage;

    public WarningKSEFeatureAccessDialog(Shell shell, String message) {
        super(shell);
        this.message = message;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridLayout glBody = new GridLayout(2, false);
        glBody.marginWidth = 15;
        glBody.marginHeight = 15;
        body.setLayout(glBody);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 500;
        body.setLayoutData(gdBody);

        Label lblImage = new Label(body, SWT.NONE);
        lblImage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
        lblImage.setImage(getWarningImage());

        lnkMessage = new Link(body, SWT.WRAP);
        lnkMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        lnkMessage.setText(message);

        registerControlListeners();

        return body;
    }

    private Image getWarningImage() {
        Shell shell = getShell();
        Display display = shell.getDisplay();
        Image imgWarning = display.getSystemImage(SWT.ICON_WARNING);
        return imgWarning;
    }

    private void registerControlListeners() {
        lnkMessage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
                Trackings.trackOpenKSEBrochurePage();
            }
        });
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(GlobalStringConstants.WARN);
    }
}
