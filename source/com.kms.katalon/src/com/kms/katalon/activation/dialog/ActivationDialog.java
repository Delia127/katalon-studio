package com.kms.katalon.activation.dialog;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.jface.dialogs.Dialog;
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

import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.ActivationInfoCollector;

public class ActivationDialog extends Dialog {

    private static final Character PASSWORD_CHAR_MASK = (char) 0x25cf;

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
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.marginRight = 10;
        glContainer.marginTop = 10;
        glContainer.marginLeft = 10;
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        glContainer.horizontalSpacing = 0;
        glContainer.verticalSpacing = 0;
        container.setLayout(glContainer);

        Label lblUsername = new Label(container, SWT.NONE);
        GridData gdLblUsername = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdLblUsername.widthHint = 73;
        lblUsername.setLayoutData(gdLblUsername);
        lblUsername.setText(StringConstants.USERNAME_TITLE);

        txtUserName = new Text(container, SWT.BORDER);
        GridData gdTxtUserName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdTxtUserName.widthHint = 356;
        gdTxtUserName.heightHint = 22;
        txtUserName.setLayoutData(gdTxtUserName);

        Composite compSeparate = new Composite(container, SWT.NONE);
        GridData gdCompSeparate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdCompSeparate.heightHint = 5;
        compSeparate.setLayoutData(gdCompSeparate);

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setText(StringConstants.PASSSWORD_TITLE);

        txtPassword = new Text(container, SWT.BORDER);
        GridData gdTxtPassword = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
        gdTxtPassword.widthHint = 356;
        gdTxtPassword.heightHint = 22;
        txtPassword.setLayoutData(gdTxtPassword);
        txtPassword.setEchoChar(PASSWORD_CHAR_MASK);

        lblError = new Label(container, SWT.NONE);
        lblError.setAlignment(SWT.CENTER);
        GridData gdLblError = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdLblError.verticalIndent = 5;
        gdLblError.widthHint = 432;
        gdLblError.heightHint = 22;
        lblError.setLayoutData(gdLblError);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        Composite composite = new Composite(container, SWT.NONE);
        GridData gdComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdComposite.widthHint = 430;
        composite.setLayoutData(gdComposite);
        composite.setLayout(new GridLayout(9, false));

        btnClear = new Button(composite, SWT.NONE);
        GridData gdBtnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnClear.heightHint = 26;
        gdBtnClear.widthHint = 62;
        btnClear.setLayoutData(gdBtnClear);
        btnClear.setText(StringConstants.BTN_CLEAR_TILE);

        btnActivate = new Button(composite, SWT.NONE);
        GridData gdBtnActivate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnActivate.heightHint = 26;
        gdBtnActivate.widthHint = 72;
        btnActivate.setLayoutData(gdBtnActivate);
        btnActivate.setText(StringConstants.BTN_ACTIVATE_TILE);

        Link linkForgotPass = new Link(composite, SWT.NONE);
        linkForgotPass.setText(StringConstants.LINK_LABEL_FORGOT_PASS_TEXT);

        Label lblVertialSeparate = new Label(composite, SWT.NONE);
        lblVertialSeparate.setText(StringConstants.SEPARATE_LINK);

        Link linkRegister = new Link(composite, SWT.NONE);
        linkRegister.setText(StringConstants.LINK_LABEL_REGISTER_TEXT);

        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setText(StringConstants.SEPARATE_LINK);

        Link link = new Link(composite, SWT.NONE);
        link.setText(StringConstants.LINK_OPEN_ACTIVATE_FORM_OFFLINE);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    int result = new ActivationOfflineDialog(ActivationDialog.this.getShell()).open();
                    if (result == Dialog.OK) {
                        setReturnCode(Window.OK);
                        close();
                    }
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });

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
                    Desktop.getDesktop().browse(new URI(StringConstants.FORGOT_PASS_LINK));
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });
        linkRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(StringConstants.REGISTER_LINK));
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
        if (!isFullFillActivateInfo()) {
            return;
        }
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblError.setText(StringConstants.WAITTING_MESSAGE);

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
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

    private void enableActivateButton() {
        boolean enable = isFullFillActivateInfo();
        lblError.setText(isFullFillActivateInfo() ? "" : StringConstants.PROMT_ENTER_USERNAME_PASSWORD);
        btnActivate.setEnabled(enable);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIALOG_TITLE);
        newShell.setImage(ImageConstants.KATALON_IMAGE);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginHeight = 0;
        parent.getShell().setDefaultButton(btnActivate);
    }

    private class ActivateDialogKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (((e.character) == SWT.CR) || ((e.character) == SWT.KEYPAD_CR)) {
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
