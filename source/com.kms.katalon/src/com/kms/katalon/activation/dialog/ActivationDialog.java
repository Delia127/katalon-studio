package com.kms.katalon.activation.dialog;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.ActivationInfoCollector;

public class ActivationDialog extends Dialog {
    private static final String FORGOT_PASS_LINK = "https://wp-staging.katalon.com/#katalon-reset-password";

    private static final String LINK_LABEL_FORGOT_PASS_TEXT = "<a>Forgot Password?</a>";

    private static final String REGISTER_LINK = "https://wp-staging.katalon.com/#katalon-register";

    private static final String LINK_LABEL_REGISTER_TEXT = "<a>Register</a>";

    private static final String PROMT_ENTER_USERNAME_PASSWORD = "Enter email and password.";

    private static final String USERNAME_TITLE = "Email";

    private static final String PASSSWORD_TITLE = "Password";

    private static final String SEPARATE_LINK = "|";

    private static final String DIALOG_TITLE = "Product Activation";

    private static final String KATALON_IMAGE = "icons/branding_16.png";

    private static final String BTN_CLEAR_TILE = "Clear";

    private static final String BTN_ACTIVATE_TILE = "Activate";
    
    private static final String WAITTING_MESSAGE = "Activating product...";
    
    private static final Character PASSWORD_CHAR_MASK = (char)0x25cf;

    private Button btnActivate;

    private Button btnClear;

    private Text txtUserName;

    private Text txtPassword;

    private Label lblError;

    public ActivationDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gl_container = new GridLayout(2, false);
        gl_container.marginRight = 10;
        gl_container.marginTop = 10;
        gl_container.marginLeft = 10;
        gl_container.marginWidth = 0;
        gl_container.marginHeight = 0;
        gl_container.horizontalSpacing = 0;
        gl_container.verticalSpacing = 0;
        container.setLayout(gl_container);

        Label lblUsername = new Label(container, SWT.NONE);
        GridData gd_lblUsername = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_lblUsername.widthHint = 73;
        lblUsername.setLayoutData(gd_lblUsername);
        lblUsername.setText(USERNAME_TITLE);

        txtUserName = new Text(container, SWT.BORDER);
        GridData gd_txtUserName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_txtUserName.widthHint = 200;
        gd_txtUserName.heightHint = 22;
        txtUserName.setLayoutData(gd_txtUserName);

        Composite compSeparate = new Composite(container, SWT.NONE);
        GridData gd_compSeparate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gd_compSeparate.heightHint = 5;
        compSeparate.setLayoutData(gd_compSeparate);

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setText(PASSSWORD_TITLE);

        txtPassword = new Text(container, SWT.BORDER);
        GridData gd_txtPassword = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
        gd_txtPassword.widthHint = 200;
        gd_txtPassword.heightHint = 22;
        txtPassword.setLayoutData(gd_txtPassword);
        txtPassword.setEchoChar(PASSWORD_CHAR_MASK);
        
        lblError = new Label(container, SWT.NONE);
        lblError.setAlignment(SWT.CENTER);
        GridData gd_lblError = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gd_lblError.verticalIndent = 5;
        gd_lblError.widthHint = 365;
        gd_lblError.heightHint = 22;
        lblError.setLayoutData(gd_lblError);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        Composite composite = new Composite(container, SWT.NONE);
        GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gd_composite.widthHint = 321;
        composite.setLayoutData(gd_composite);
        composite.setLayout(new GridLayout(5, false));

        btnClear = new Button(composite, SWT.NONE);
        GridData gd_btnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnClear.heightHint = 26;
        gd_btnClear.widthHint = 62;
        btnClear.setLayoutData(gd_btnClear);
        btnClear.setText(BTN_CLEAR_TILE);

        btnActivate = new Button(composite, SWT.NONE);
        GridData gd_btnActivate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnActivate.heightHint = 26;
        gd_btnActivate.widthHint = 72;
        btnActivate.setLayoutData(gd_btnActivate);
        btnActivate.setText(BTN_ACTIVATE_TILE);

        Link linkForgotPass = new Link(composite, SWT.NONE);
        linkForgotPass.setText(LINK_LABEL_FORGOT_PASS_TEXT);

        Label lblVertialSeparate = new Label(composite, SWT.NONE);
        lblVertialSeparate.setText(SEPARATE_LINK);

        Link linkRegister = new Link(composite, SWT.NONE);
        linkRegister.setText(LINK_LABEL_REGISTER_TEXT);

        ActivateDialogKeyAdapter keyAdapter = new ActivateDialogKeyAdapter();
        txtUserName.addKeyListener(keyAdapter);
        txtPassword.addKeyListener(keyAdapter);

        ActivateDialogTextChanged textChangedListener = new ActivateDialogTextChanged();
        txtUserName.addModifyListener(textChangedListener);
        txtPassword.addModifyListener(textChangedListener);

        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtPassword.setText("");
                txtUserName.setText("");
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processActivate();
            }
        });
        linkForgotPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(FORGOT_PASS_LINK));
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });
        linkRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(REGISTER_LINK));
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });

        enableActivateButton();

        return container;
    }

    private boolean isFullFillActivateInfo() {
        return txtUserName.getText().trim().length() > 0 && txtPassword.getText().trim().length() > 0;
    }

    protected void processActivate() {
        if (isFullFillActivateInfo()) {
            lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            lblError.setText(WAITTING_MESSAGE);
            
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    StringBuilder errorMessage = new StringBuilder();
                    boolean result = ActivationInfoCollector.activate(txtUserName.getText(), txtPassword.getText(),
                            errorMessage);
                    lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                    if (result == true) {
                        setReturnCode(Window.OK);
                        close();
                    } else {
                        lblError.setText(errorMessage.toString());
                    }
                }
            });
            
        }
    }

    private void enableActivateButton() {
        boolean enable = isFullFillActivateInfo();
        lblError.setText(isFullFillActivateInfo() ? "" : PROMT_ENTER_USERNAME_PASSWORD);
        btnActivate.setEnabled(enable);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(DIALOG_TITLE);
        ImageDescriptor imgDesc = ImageDescriptor.createFromURL(FileLocator.find(
                Platform.getBundle(IdConstants.APPLICATION_ID), new Path(KATALON_IMAGE), null));
        newShell.setImage(imgDesc.createImage());
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginHeight = 0;
    }

    private class ActivateDialogKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if ((((int) e.character) == SWT.CR) || (((int) e.character) == SWT.KEYPAD_CR)) {
                processActivate();
            }
        }
    }

    private class ActivateDialogTextChanged implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            enableActivateButton();
        }
    }
}