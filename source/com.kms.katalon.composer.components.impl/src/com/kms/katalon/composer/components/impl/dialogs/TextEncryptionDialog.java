package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextEncryptionDialog extends Dialog {
    
    private Text txtRawText;
    
    private Text txtEncryptedText;
    
    private Button btnEncrypt;
    
    private Button btnEncryptAndClose;

    public TextEncryptionDialog(Shell parentShell) {
        super(parentShell);
        
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData bodyGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        bodyGridData.widthHint = 400;
        body.setLayoutData(bodyGridData);
        body.setLayout(new GridLayout(1, false));
        
        Composite inputComposite = new Composite(body, SWT.NONE);
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        inputComposite.setLayout(new GridLayout(2, false));
        
        
        Label lblRawText = new Label(inputComposite, SWT.NONE);
        lblRawText.setText("Raw Text");
        txtRawText = new Text(inputComposite, SWT.BORDER);
        txtRawText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        Label lblEncryptedText = new Label(inputComposite, SWT.NONE);
        lblEncryptedText.setText("Encrypted Text");
        txtEncryptedText = new Text(inputComposite, SWT.BORDER);
        txtEncryptedText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        buttonComposite.setLayout(new GridLayout(2, false));
        
        Button btnEncrypt = new Button(buttonComposite, SWT.FLAT);
        btnEncrypt.setText("Encrypt");
        Button btnEncryptAndClose = new Button(buttonComposite, SWT.FLAT);
        btnEncryptAndClose.setText("Encrypt and Close");
        
        addControlListeners();
        
        return body;
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Encrypt Text");
    }

    private void addControlListeners() {
        
    }
}
