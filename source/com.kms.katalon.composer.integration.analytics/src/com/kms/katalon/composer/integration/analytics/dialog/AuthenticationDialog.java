package com.kms.katalon.composer.integration.analytics.dialog;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.util.CryptoUtil;

public class AuthenticationDialog extends Dialog {
    
    private Text username;
    
    private Text password;
    
    private String encryptedText;
    
    private Button btnInsert;
    
    private Button btnCopyAndClose;
    
    private Button btnCancel;
    
    private boolean isManualMode;

    private AuthenticationDialog(Shell parentShell, boolean isManualMode) {
        super(parentShell);
        this.isManualMode = isManualMode;
    }
    
    public static AuthenticationDialog createDialogForManualModeCellEditor(Shell parentShell) {
        return new AuthenticationDialog(parentShell, true);
    }
    
    public static AuthenticationDialog createDefault(Shell parentShell) {
        return new AuthenticationDialog(parentShell, false);
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
        
        Label lblusername = new Label(inputComposite, SWT.NONE);
        lblusername.setText(StringConstants.LBL_USER_NAME);
        username = new Text(inputComposite, SWT.BORDER);
        username.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        Label lblpassword = new Label(inputComposite, SWT.NONE);
        lblpassword.setText(StringConstants.LBL_PASSWORD);
        password = new Text(inputComposite, SWT.BORDER);
        password.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        Composite buttonComposite = new Composite(body, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
        buttonComposite.setLayout(new GridLayout(2, false));
        
        if (isManualMode) {
            btnInsert = new Button(buttonComposite, SWT.FLAT);
            btnInsert.setText(StringConstants.BTN_INSERT);
            btnInsert.setEnabled(false);
        } else {
            btnCopyAndClose = new Button(buttonComposite, SWT.FLAT);
            btnCopyAndClose.setText(StringConstants.BTN_COPY_AND_CLOSE);
            btnCopyAndClose.setEnabled(false);
        }
        
        btnCancel = new Button(buttonComposite, SWT.FLAT);
        btnCancel.setText(StringConstants.BTN_CANCEL);
        
        addControlListeners();
        return body;
    }
    
    private void addControlListeners() {
        username.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                handleGenerateEncryptedText();
                if (!StringUtils.isBlank(encryptedText)) {
                    if (isManualMode) {
                        btnInsert.setEnabled(true);
                    } else {
                        btnCopyAndClose.setEnabled(true);
                    }
                } else {
                    if (isManualMode) {
                        btnInsert.setEnabled(false);
                    } else {
                        btnCopyAndClose.setEnabled(false);
                    }
                }
            }
        });
        
        if (isManualMode) {
            btnInsert.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleGenerateEncryptedText();
                    closeDialog();
                }
            });
        } else {
            btnCopyAndClose.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleGenerateEncryptedText();
                    handleCopyEncryptedText();
                    closeDialog();
                }
            });
        }
        
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
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
        shell.setText(StringConstants.AUTHENTICATION_DIALOG_TITLE);
    }
    
    private void handleGenerateEncryptedText() {
        String rawText = username.getText();
        if (!StringUtils.isEmpty(rawText)) {
            try {
                CryptoUtil.CrytoInfo cryptoInfo = CryptoUtil.getDefault(rawText);
                encryptedText = CryptoUtil.encode(cryptoInfo);
                password.setText(encryptedText);
            } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                LoggerSingleton.logError(e);
            }
        } else {
            password.setText(StringUtils.EMPTY);
            encryptedText = password.getText();
        }
    }
    
    private void handleCopyEncryptedText() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new Object[] { encryptedText }, new Transfer[] { textTransfer });
        clipboard.dispose();
    }
    
    private void closeDialog() {
        this.close();
    }
    
    public String getEncryptedText() {
        return encryptedText;
    }
}
