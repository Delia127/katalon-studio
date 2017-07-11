package com.kms.katalon.activation.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;

public class LinuxNotSupportedDialog extends Dialog {

    private Link lblNotification;

    public LinuxNotSupportedDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        lblNotification = new Link(container, SWT.WRAP);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        layoutData.widthHint = 400;
        lblNotification.setLayoutData(layoutData);
        lblNotification.setText(MessageConstants.MSG_LINUX_GUI_NOT_SUPPORTED);
        registerControlModifyListeners();

        return container;
    }

    private void registerControlModifyListeners() {
        lblNotification.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(arg | SWT.PRIMARY_MODAL | SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.NOTIFICATION);
    }
}
