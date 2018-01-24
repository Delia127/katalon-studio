package com.kms.katalon.composer.testcase.dialogs;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.util.CryptoUtil;

public class TextEncryptionDialog extends Dialog {
    
    private Text txtRawText;
    
    private Text txtEncryptedText;
    
    private String encryptedText;
    
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
        txtEncryptedText.setEditable(false);
        
        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        buttonComposite.setLayout(new GridLayout(2, false));
        
        btnEncrypt = new Button(buttonComposite, SWT.FLAT);
        btnEncrypt.setText("Encrypt");
        btnEncrypt.setEnabled(false);
        
        btnEncryptAndClose = new Button(buttonComposite, SWT.FLAT);
        btnEncryptAndClose.setText("Encrypt and Close");
        btnEncryptAndClose.setEnabled(false);
        
        addControlListeners();
        return body;
    }
    
    private void addControlListeners() {
        txtRawText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                if (StringUtils.isBlank(text.getText())) {
                    btnEncrypt.setEnabled(false);
                    btnEncryptAndClose.setEnabled(false);
                } else {
                    btnEncrypt.setEnabled(true);
                    btnEncryptAndClose.setEnabled(true);
                }
            }
        });
        
        btnEncrypt.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleGenerateEncryptedText();
            }
        });
        
        btnEncryptAndClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleGenerateEncryptedText();
                encryptedText = txtEncryptedText.getText();
                closeDialog();
            }
        });
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
    
    private void handleGenerateEncryptedText() {
        txtEncryptedText.setText(StringUtils.EMPTY);
        
        String rawText = txtRawText.getText();
        if (!StringUtils.isEmpty(rawText)) {
            try {
                CryptoUtil.CrytoInfo cryptoInfo = CryptoUtil.getDefault(rawText);
                String encryptedText = CryptoUtil.encode(cryptoInfo);
                txtEncryptedText.setText(encryptedText);
            } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                LoggerSingleton.logError(e);
            }
        }
    }
    
    private void closeDialog() {
        this.close();
    }
    
    public String getEncryptedText() {
        return encryptedText;
    }
}
