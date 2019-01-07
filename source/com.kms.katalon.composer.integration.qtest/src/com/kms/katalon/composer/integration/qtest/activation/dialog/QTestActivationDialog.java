package com.kms.katalon.composer.integration.qtest.activation.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.activation.dialog.ProxyConfigurationDialog;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.integration.qtest.helper.QTestActivationHelper;

public class QTestActivationDialog extends Dialog {

    private Button btnActivate;

    private Button btnClear;

    private Text txtQTestUserName;

    private Text txtActivationCode;

    private boolean expiredActivation = false;

    private boolean allowOfflineActivation = true;

    private Composite linkComposite;

    private Label lblMessage;

    public boolean isAllowOfflineActivation() {
        return allowOfflineActivation;
    }

    public QTestActivationDialog(Shell parentShell, boolean expiredActivation) {
        super(parentShell);
        this.expiredActivation = expiredActivation;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);

        setInput();

        return control;
    }

    private void setInput() {
        if (!expiredActivation) {
            setMessage(ComposerIntegrationQtestMessageConstants.DIA_MSG_ENTER_USERNAME_CODE,
                    IMessageProvider.INFORMATION);
        } else {
            setMessage(ComposerIntegrationQtestMessageConstants.DIA_WARN_EXPIRED_LICENSE,
                    IMessageProvider.WARNING);
        }

        checkActivationStatus();
    }

    private void setMessage(String message, int information) {
        if (information == IMessageProvider.NONE) {
            lblMessage.setForeground(ColorUtil.getDefaultTextColor());
        } else {
            lblMessage.setForeground(lblMessage.getDisplay().getSystemColor(SWT.COLOR_RED));
        }
        lblMessage.setText(message);
        getShell().layout();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(body, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.marginWidth = 10;
        glContainer.horizontalSpacing = 15;
        glContainer.verticalSpacing = 10;
        container.setLayout(glContainer);

        lblMessage = new Label(container, SWT.WRAP);
        lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblUsername = new Label(container, SWT.NONE);
        lblUsername.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblUsername.setText(ComposerIntegrationQtestMessageConstants.DIA_LBL_QTEST_USERNAME);

        txtQTestUserName = new Text(container, SWT.BORDER);
        txtQTestUserName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(container, SWT.NONE);
        lblPassword.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblPassword.setText(ComposerIntegrationQtestMessageConstants.DIA_LBL_QTEST_CODE);

        txtActivationCode = new Text(container, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gdActivationCode = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdActivationCode.heightHint = txtActivationCode.getLineHeight() * 5;
        txtActivationCode.setLayoutData(gdActivationCode);
        

        Link linkUserGuide = new Link(container, SWT.WRAP);
        linkUserGuide.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 2, 1));
        linkUserGuide.setText(ComposerIntegrationQtestMessageConstants.DIA_MSG_USER_GUIDE);
        linkUserGuide.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
        
        return container;
    }

    private void addOfflineActivationLink(Composite composite) {
        Link linkOfflineActivation = new Link(linkComposite, SWT.NONE);
        linkOfflineActivation.setText(StringConstants.LINK_OPEN_ACTIVATE_FORM_OFFLINE);
        linkOfflineActivation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                int result = new QTestActivationOfflineDialog(QTestActivationDialog.this.getShell()).open();
                if (result == Dialog.OK) {
                    setReturnCode(Window.OK);
                    close();
                }
            }
        });
    }

    private boolean isFullFillActivateInfo() {
        return StringUtils.isNotEmpty(txtQTestUserName.getText().trim())
                && StringUtils.isNotEmpty(txtActivationCode.getText().trim());
    }

    protected void processActivate() {
        if (!isFullFillActivateInfo()) {
            return;
        }
        setMessage(StringConstants.WAITTING_MESSAGE, IMessageProvider.NONE);

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                StringBuilder errorMessage = new StringBuilder();
                boolean result = QTestActivationHelper.qTestOnlineActivate(txtQTestUserName.getText(),
                        txtActivationCode.getText(), errorMessage);
                if (result == true) {
                    setReturnCode(Window.OK);
                    close();
                } else {
                    setMessage(errorMessage.toString(), IMessageProvider.ERROR);
                }
            }
        });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConstants.DIA_TITLE_KS_ACTIVATION);
        newShell.setImage(ImageConstants.KATALON_IMAGE);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite btnComposite) {
        btnComposite.setLayout(new GridLayout(9, false));

        linkComposite = new Composite(btnComposite, SWT.NONE);
        GridData linkCompositeGd = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        linkComposite.setLayoutData(linkCompositeGd);
        linkComposite.setLayout(new GridLayout(4, false));

        if (isAllowOfflineActivation()) {
            addOfflineActivationLink(btnComposite);
        }

        Label lblNewLabel = new Label(linkComposite, SWT.NONE);
        lblNewLabel.setText(StringConstants.SEPARATE_LINK);

        Link linkConfigProxy = new Link(linkComposite, SWT.NONE);
        linkConfigProxy.setText(MessageConstants.CONFIG_PROXY);
        linkConfigProxy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                new ProxyConfigurationDialog(QTestActivationDialog.this.getShell()).open();
            }
        });

        ActivateDialogKeyAdapter keyAdapter = new ActivateDialogKeyAdapter();
        txtQTestUserName.addKeyListener(keyAdapter);

        ActivateDialogTextChanged textChangedListener = new ActivateDialogTextChanged();
        txtQTestUserName.addModifyListener(textChangedListener);
        txtActivationCode.addModifyListener(textChangedListener);

        btnClear = new Button(btnComposite, SWT.NONE);
        GridData gdBtnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        btnClear.setLayoutData(gdBtnClear);
        btnClear.setText(StringConstants.BTN_CLEAR_TILE);

        btnActivate = new Button(btnComposite, SWT.NONE);
        GridData gdBtnActivate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        btnActivate.setLayoutData(gdBtnActivate);
        btnActivate.setText(StringConstants.BTN_ACTIVATE_TITLE);

        btnClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtActivationCode.setText("");
                txtQTestUserName.setText("");
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processActivate();
            }
        });

        getShell().setDefaultButton(btnActivate);
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
            checkActivationStatus();
        }
    }

    private void checkActivationStatus() {
        btnActivate.setEnabled(isFullFillActivateInfo());
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, super.getInitialSize().y);
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }

}
