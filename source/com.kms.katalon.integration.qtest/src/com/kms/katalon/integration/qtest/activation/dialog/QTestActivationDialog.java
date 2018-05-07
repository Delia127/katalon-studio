package com.kms.katalon.integration.qtest.activation.dialog;

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

import com.kms.katalon.activation.dialog.ProxyConfigurationDialog;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.logging.LogUtil;

public class QTestActivationDialog extends Dialog {

    private Button btnActivate;

    private Button btnClear;

    private Text txtQTestUserName;

    private Text txtQtestCode;

    private Label lblError;
    
    private boolean allowOfflineActivation = true;

    public QTestActivationDialog(Shell parentShell) {
        super(parentShell);
    }

    public boolean isAllowOfflineActivation() {
        return allowOfflineActivation;
    }

    public void setAllowOfflineActivation(boolean allowOfflineActivation) {
        this.allowOfflineActivation = allowOfflineActivation;
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
        gdLblUsername.widthHint = 100;
        lblUsername.setLayoutData(gdLblUsername);
        lblUsername.setText(QTestMessageConstants.QTEST_USERNAME_LABEL);

        txtQTestUserName = new Text(container, SWT.BORDER);
        GridData gdTxtUserName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtUserName.heightHint = 22;
        txtQTestUserName.setLayoutData(gdTxtUserName);

        Composite compSeparate = new Composite(container, SWT.NONE);
        GridData gdCompSeparate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdCompSeparate.heightHint = 5;
        compSeparate.setLayoutData(gdCompSeparate);

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setText(QTestMessageConstants.QTEST_CODE_LABEL);

        txtQtestCode = new Text(container, SWT.BORDER | SWT.PASSWORD);
        GridData gdTxtPassword = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
        gdTxtPassword.heightHint = 22;
        txtQtestCode.setLayoutData(gdTxtPassword);

        lblError = new Label(container, SWT.NONE);
        lblError.setAlignment(SWT.CENTER);
        GridData gdLblError = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        gdLblError.verticalIndent = 5;
        gdLblError.heightHint = 22;
        lblError.setLayoutData(gdLblError);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        Composite composite = new Composite(container, SWT.NONE);
        GridData gdComposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
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

        if (isAllowOfflineActivation()) {
            Label lblNewLabel = new Label(composite, SWT.NONE);
            lblNewLabel.setText(StringConstants.SEPARATE_LINK);
            addOfflineActivationLink(composite);
        }
        
        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setText(StringConstants.SEPARATE_LINK);

        Link linkConfigProxy = new Link(composite, SWT.NONE);
        linkConfigProxy.setText(MessageConstants.CONFIG_PROXY);
        linkConfigProxy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                new ProxyConfigurationDialog(QTestActivationDialog.this.getShell()).open();
            }
        });

        ActivateDialogKeyAdapter keyAdapter = new ActivateDialogKeyAdapter();
        txtQTestUserName.addKeyListener(keyAdapter);
        txtQtestCode.addKeyListener(keyAdapter);

        ActivateDialogTextChanged textChangedListener = new ActivateDialogTextChanged();
        txtQTestUserName.addModifyListener(textChangedListener);
        txtQtestCode.addModifyListener(textChangedListener);

        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtQtestCode.setText("");
                txtQTestUserName.setText("");
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processActivate();
            }
        });

        enableActivateButton();

        return container;
    }

    private void addOfflineActivationLink(Composite composite) {
        Link linkOfflineActivation = new Link(composite, SWT.NONE);
        linkOfflineActivation.setText(StringConstants.LINK_OPEN_ACTIVATE_FORM_OFFLINE);
        linkOfflineActivation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                try {
                    int result = new QTestActivationOfflineDialog(QTestActivationDialog.this.getShell()).open();
                    if (result == Dialog.OK) {
                        setReturnCode(Window.OK);
                        close();
                    }
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });
    }

    private boolean isFullFillActivateInfo() {
        return txtQTestUserName.getText().trim().length() > 0 && txtQtestCode.getText().trim().length() > 0;
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
                boolean result = ActivationInfoCollector.qTestActivate(txtQTestUserName.getText(), txtQtestCode.getText(),
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
        lblError.setText(isFullFillActivateInfo() ? "" : QTestMessageConstants.QTEST_ACTIVATION_PROMPT_ENTER_USERNAME_CODE);
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
