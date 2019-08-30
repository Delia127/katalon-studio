package com.kms.katalon.activation.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class ActivationOfflineDialog extends Dialog {

    private Button btnActivate;

    private Text txtActivationRequest;

    private Button btnCopyToClipboard;

    private Text txtActivationCode;

    private Label lblError;

    public ActivationOfflineDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout glContainer = new GridLayout();
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        container.setLayout(glContainer);

        Composite body = new Composite(container, SWT.NONE);
        GridLayout glBody = new GridLayout(2, false);
        glBody.marginWidth = 10;
        glBody.marginHeight = 10;
        body.setLayout(glBody);

        Link lblMessage = new Link(body, SWT.WRAP);
        lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        lblMessage.setText(StringConstants.LBL_ACTIVATE_OFFLINE_HELP);
        lblMessage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(StringConstants.LINK_ACTIVATION_LINK);
                } catch (Exception ex) {
                    LogUtil.logError(ex);
                }
            }
        });

        Label lblRequestCode = new Label(body, SWT.NONE);
        lblRequestCode.setText(StringConstants.ACTIVATION_REQUEST_CODE);
        lblRequestCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtActivationRequest = new Text(body, SWT.BORDER | SWT.READ_ONLY);
        GridData gdActivationRequest = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdActivationRequest.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtActivationRequest.setLayoutData(gdActivationRequest);
        txtActivationRequest.setText(ComposerActivationInfoCollector.genRequestActivationInfo());
        txtActivationRequest.setBackground(ColorUtil.getDisabledItemBackgroundColor());

        new Label(body, SWT.NONE);
        btnCopyToClipboard = new Button(body, SWT.PUSH);
        btnCopyToClipboard.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        btnCopyToClipboard.setText(StringConstants.BTN_COPY_TITLE);
        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Clipboard cb = new Clipboard(Display.getCurrent());
                cb.setContents(
                        new Object[] {txtActivationRequest.getText() },
                        new Transfer[] { TextTransfer.getInstance() });
            }
        });

        Label lblNewLabelOne = new Label(body, SWT.NONE);
        lblNewLabelOne.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblNewLabelOne.setText(StringConstants.LBL_ACTIVATE_CODE);

        txtActivationCode = new Text(body, SWT.BORDER);
        GridData gdActivationCode = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdActivationCode.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtActivationCode.setLayoutData(gdActivationCode);
        txtActivationCode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                enableActivateButton();
            }
        });

        new Label(body, SWT.NONE);
        lblError = new Label(body, SWT.NONE);
        lblError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        Label spacer = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        spacer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        return container;
    }

    private boolean isFullFillActivateInfo() {
        return txtActivationCode.getText().trim().length() > 0;
    }

    protected void processActivate() {
        if (isFullFillActivateInfo()) {
            lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            lblError.setText(StringConstants.WAITTING_MESSAGE);

            Display.getCurrent().asyncExec(new Runnable() {
                @Override
                public void run() {
                    StringBuilder errorMessage = new StringBuilder();
                    boolean result = ActivationInfoCollector.activateOffline(txtActivationCode.getText().trim(), errorMessage);
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
        lblError.setText(isFullFillActivateInfo() ? "" : StringConstants.PROMT_ENTER_ACTIVATE_CODE);
        btnActivate.setEnabled(enable);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIALOG_OFFLINE_TITLE);
        newShell.setImage(ImageConstants.KATALON_IMAGE);
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
        btnActivate = createButton(parent, IDialogConstants.OK_ID, StringConstants.BTN_ACTIVATE_TITLE, true);
        enableActivateButton();
    }

    @Override
    protected void okPressed() {
        processActivate();
    }
}
