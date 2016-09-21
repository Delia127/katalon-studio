package com.kms.katalon.activation.dialog;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.util.ActivationInfoCollector;

public class ActivationOfflineDialog extends Dialog {

    private Button btnActivate;

    private Button btnClose;

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
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.marginRight = 10;
        glContainer.marginTop = 10;
        glContainer.marginLeft = 10;
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        glContainer.horizontalSpacing = 0;
        glContainer.verticalSpacing = 0;
        container.setLayout(glContainer);

        Composite compositeActivationCode = new Composite(container, SWT.NONE);
        GridData gdCompositeActivationCode = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        gdCompositeActivationCode.widthHint = 518;
        gdCompositeActivationCode.heightHint = 169;
        compositeActivationCode.setLayoutData(gdCompositeActivationCode);
        GridLayout glCompositeActivationCode = new GridLayout(2, false);
        glCompositeActivationCode.verticalSpacing = 0;
        glCompositeActivationCode.marginWidth = 0;
        glCompositeActivationCode.marginHeight = 0;
        glCompositeActivationCode.horizontalSpacing = 0;
        compositeActivationCode.setLayout(glCompositeActivationCode);

        Label lblNewLabel = new Label(compositeActivationCode, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblNewLabel.setText(StringConstants.LBL_ACTIVATE_OFFLINE_HELP);

        new Label(compositeActivationCode, SWT.NONE);

        txtActivationRequest = new Text(compositeActivationCode, SWT.BORDER | SWT.MULTI);
        GridData gdTxtActivationRequest = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdTxtActivationRequest.widthHint = 517;
        gdTxtActivationRequest.verticalIndent = 5;
        gdTxtActivationRequest.heightHint = 35;
        txtActivationRequest.setLayoutData(gdTxtActivationRequest);
        txtActivationRequest.setText(StringConstants.ACTIVATION_REQUEST_CODE + ": "
                + ActivationInfoCollector.genRequestActivationInfo());
        txtActivationRequest.setEditable(false);
        txtActivationRequest.setBackground(ColorUtil.getDisabledItemBackgroundColor());
        new Label(compositeActivationCode, SWT.NONE);

        btnCopyToClipboard = new Button(compositeActivationCode, SWT.NONE);
        GridData gdBtnCopyToClipboard = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnCopyToClipboard.verticalIndent = 5;
        gdBtnCopyToClipboard.heightHint = 25;
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            gdBtnCopyToClipboard.horizontalIndent = -5;
        }
        btnCopyToClipboard.setLayoutData(gdBtnCopyToClipboard);
        btnCopyToClipboard.setText(StringConstants.BTN_COPY_TITLE);
        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Clipboard cb = new Clipboard(Display.getCurrent());
                cb.setContents(new Object[] { txtActivationRequest.getText() },
                        new Transfer[] { TextTransfer.getInstance() });
                txtActivationRequest.copy();
            }
        });
        new Label(compositeActivationCode, SWT.NONE);

        Composite compositeOne = new Composite(compositeActivationCode, SWT.NONE);
        GridLayout glCompositeOne = new GridLayout(2, false);
        glCompositeOne.marginWidth = 0;
        compositeOne.setLayout(glCompositeOne);
        GridData gdCompositeOne = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCompositeOne.verticalIndent = 5;
        gdCompositeOne.widthHint = 516;
        gdCompositeOne.heightHint = 33;
        compositeOne.setLayoutData(gdCompositeOne);

        Label lblNewLabelOne = new Label(compositeOne, SWT.NONE);
        GridData gdLblNewLabelOne = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdLblNewLabelOne.heightHint = 20;
        lblNewLabelOne.setLayoutData(gdLblNewLabelOne);
        lblNewLabelOne.setText(StringConstants.LBL_ACTIVATE_CODE);

        txtActivationCode = new Text(compositeOne, SWT.BORDER);
        GridData gdTxtActivationCode = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtActivationCode.widthHint = 422;
        gdTxtActivationCode.heightHint = 22;
        txtActivationCode.setLayoutData(gdTxtActivationCode);
        txtActivationCode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                enableActivateButton();
            }
        });
        new Label(compositeActivationCode, SWT.NONE);

        lblError = new Label(container, SWT.NONE);
        GridData gdLblError = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gdLblError.verticalIndent = 5;
        gdLblError.widthHint = 500;
        gdLblError.heightHint = 20;
        lblError.setLayoutData(gdLblError);
        lblError.setText("New Label");
        lblError.setAlignment(SWT.CENTER);
        lblError.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        new Label(container, SWT.NONE);

        Composite composite = new Composite(container, SWT.NONE);
        GridData gdComposite = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdComposite.widthHint = 142;
        composite.setLayoutData(gdComposite);
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.marginWidth = 0;
        composite.setLayout(glComposite);

        btnClose = new Button(composite, SWT.NONE);
        GridData gdBtnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnClear.heightHint = 26;
        gdBtnClear.widthHint = 62;
        btnClose.setLayoutData(gdBtnClear);
        btnClose.setText(StringConstants.BTN_CLOSE_TILE);
        btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(Dialog.CANCEL);
                close();
            }
        });

        btnActivate = new Button(composite, SWT.NONE);
        GridData gdBtnActivate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdBtnActivate.heightHint = 26;
        gdBtnActivate.widthHint = 72;
        btnActivate.setLayoutData(gdBtnActivate);
        btnActivate.setText(StringConstants.BTN_ACTIVATE_TILE);
        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                processActivate();
            }
        });

        enableActivateButton();

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
                    boolean result = ActivationInfoCollector.activate(txtActivationCode.getText().trim(), errorMessage);
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
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.marginHeight = 0;
        parent.getShell().setDefaultButton(btnActivate);
    }
}
