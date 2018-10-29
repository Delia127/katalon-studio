package com.kms.katalon.composer.integration.qtest.activation.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.qtest.constant.ComposerIntegrationQtestMessageConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.integration.qtest.helper.QTestActivationHelper;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class QTestActivationOfflineDialog extends AbstractDialog {

    private Button btnActivate;

    private Text txtRequestCode;

    private Button btnCopyToClipboard;

    private Text txtActivationCode;

    private Label lblMessage;

    public QTestActivationOfflineDialog(Shell parentShell) {
        super(parentShell);
    }

    private boolean isFullFillActivateInfo() {
        return txtActivationCode.getText().trim().length() > 0;
    }

    protected void processActivate() {
        if (isFullFillActivateInfo()) {
            lblMessage.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
            lblMessage.setText(StringConstants.WAITTING_MESSAGE);

            StringBuilder errorMessage = new StringBuilder();
            boolean result = QTestActivationHelper.activate(txtActivationCode.getText().trim(),
                    txtRequestCode.getText().trim(), errorMessage);
            lblMessage.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            if (result == true) {
                setReturnCode(OK);
                close();
                return;
            } else {
                lblMessage.setText(errorMessage.toString());
            }
            lblMessage.getParent().layout(true, true);
        }
    }

    private void enableActivateButton() {
        boolean enable = isFullFillActivateInfo();
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

    @Override
    protected Point getInitialSize() {
        return new Point(600, super.getInitialSize().y + 10);
    }

    @Override
    protected void registerControlModifyListeners() {

    }

    @Override
    protected void setInput() {
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridLayout glBody = new GridLayout(2, false);
        glBody.marginWidth = 10;
        glBody.marginHeight = 10;
        body.setLayout(glBody);

        Composite messageCompsite = new Composite(body, SWT.NONE);
        messageCompsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        GridLayout glMessage = new GridLayout();
        glMessage.marginWidth = 0;
        glMessage.marginHeight = 5;
        messageCompsite.setLayout(glMessage);

        lblMessage = new Label(messageCompsite, SWT.WRAP);
        lblMessage.setLayoutData(new GridData(GridData.FILL_BOTH));
        lblMessage.setText(ComposerIntegrationQtestMessageConstants.DIA_MSG_OFFLINE_ACTIVATION_HINT);
        lblMessage.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));

        Label lblRequestCode = new Label(body, SWT.NONE);
        lblRequestCode.setText(StringConstants.ACTIVATION_REQUEST_CODE);
        lblRequestCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtRequestCode = new Text(body, SWT.BORDER | SWT.READ_ONLY);
        GridData gdActivationRequest = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdActivationRequest.heightHint = ControlUtils.DF_CONTROL_HEIGHT;
        txtRequestCode.setLayoutData(gdActivationRequest);
        txtRequestCode.setText(ComposerActivationInfoCollector.genRequestActivationInfo());
        txtRequestCode.setBackground(ColorUtil.getDisabledItemBackgroundColor());

        new Label(body, SWT.NONE);
        btnCopyToClipboard = new Button(body, SWT.PUSH);
        btnCopyToClipboard.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        btnCopyToClipboard.setText(StringConstants.BTN_COPY_TITLE);
        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Clipboard cb = new Clipboard(btnCopyToClipboard.getDisplay());
                cb.setContents(new Object[] { txtRequestCode.getText() },
                        new Transfer[] { TextTransfer.getInstance() });
            }
        });

        Label lblNewLabelOne = new Label(body, SWT.NONE);
        lblNewLabelOne.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblNewLabelOne.setText(ComposerIntegrationQtestMessageConstants.DIA_LBL_OFFLINE_ACTIVATION_QTEST_CODE);

        txtActivationCode = new Text(body, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL);
        GridData gdActivationCode = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdActivationCode.heightHint = 5 * txtActivationCode.getLineHeight();
        txtActivationCode.setLayoutData(gdActivationCode);
        txtActivationCode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                enableActivateButton();
            }
        });

        body.pack();

        return body;
    }
}
