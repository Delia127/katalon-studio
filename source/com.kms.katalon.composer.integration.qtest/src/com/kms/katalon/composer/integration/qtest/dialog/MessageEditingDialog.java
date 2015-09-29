package com.kms.katalon.composer.integration.qtest.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MessageEditingDialog extends Dialog {

    private Text txtMessage;
    private String returnedMessage;

    public MessageEditingDialog(Shell parentShell, String initMessage) {
        super(parentShell);
        this.returnedMessage = initMessage;
    }
    
    /**
     * @wbp.parser.entryPoint
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, true));
        
        txtMessage = new Text(container, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        txtMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        return container;
    }
    
    @Override
    public void create() {
        super.create();
        initialize();
    }
    
    private void initialize() {
        txtMessage.setText((returnedMessage != null) ? returnedMessage : "");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 250);
    }
    
    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Message Editor");
    }
    
    @Override
    protected void okPressed() {
        returnedMessage = txtMessage.getText();
        super.okPressed();
    }
    
    
    public String getMessage() {
        return returnedMessage;
    }
}

