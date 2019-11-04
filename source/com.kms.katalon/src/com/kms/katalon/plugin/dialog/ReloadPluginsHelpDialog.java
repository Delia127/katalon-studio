package com.kms.katalon.plugin.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.constants.StringConstants;

public class ReloadPluginsHelpDialog extends Dialog {

    private Label lblHelp;

    public ReloadPluginsHelpDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.BORDER);
        body.setLayout(new GridLayout(1, false));
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        body.setLayoutData(gdBody);

        lblHelp = new Label(body, SWT.WRAP);
        GridData gdHelp = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdHelp.verticalIndent = 5;
        gdHelp.widthHint = 430;
        lblHelp.setLayoutData(gdHelp);
        lblHelp.setText(StringConstants.KStorePluginsDialog_LBL_HELP);
        lblHelp.setAlignment(SWT.CENTER);

        Composite bottomComposite = new Composite(body, SWT.NONE);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        bottomComposite.setLayout(new GridLayout(2, false));

        Button btnClose = new Button(bottomComposite, SWT.NONE);
        btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true));
        btnClose.setText(IDialogConstants.CLOSE_LABEL);
        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ReloadPluginsHelpDialog.this.setReturnCode(Dialog.CANCEL);
                ReloadPluginsHelpDialog.this.close();
            }
        });

        return body;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.KStorePluginsDialog_DIA_TITLE);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }
}
