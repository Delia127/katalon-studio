package com.kms.katalon.activation.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class ActivationOfflineDialogV2 extends AbstractDialog {

    public static final int REQUEST_ONLINE_CODE = 1001;
    
    public static final int REQUEST_SIGNUP_CODE = 1002;

    private Label lblRequestCodeDetail;

    private Button btnCopyToClipboard;

    private Text txtActivationCode;

    private Label lblProgressMessage;

    private Link lnkOnlineRequest;

    private Link lnkActivationCode;

    private Button btnActivate;
    
    private boolean navigateFromSignUp;
    
    private Link lnkAgreeTerm;

    public ActivationOfflineDialogV2(Shell parentShell, boolean navigateFromSignUp) {
        super(parentShell, false);
        this.navigateFromSignUp = navigateFromSignUp;
    }

    @Override
    protected void registerControlModifyListeners() {
        txtActivationCode.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                btnActivate.setEnabled(validateInput());
            }
        });

        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Clipboard cb = new Clipboard(getShell().getDisplay());
                cb.setContents(new Object[] { lblRequestCodeDetail.getText() },
                        new Transfer[] { TextTransfer.getInstance() });
            }
        });

        lnkOnlineRequest.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (navigateFromSignUp) {
                    setReturnCode(REQUEST_SIGNUP_CODE);
                } else {
                    setReturnCode(REQUEST_ONLINE_CODE);
                }
                close();
            }
        });

        lnkActivationCode.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(MessageConstants.ActivationOfflineDialogV2_ACTIVATION_URL);
            }
        });
        
        lnkAgreeTerm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(StringConstants.AGREE_TERM_URL);
            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnActivate.setEnabled(false);
                setProgressMessage(MessageConstants.ActivationDialogV2_MSG_ACTIVATING, false);
                StringBuilder errorMessage = new StringBuilder();
                boolean result = ActivationInfoCollector.activate(txtActivationCode.getText().trim(), errorMessage);
                if (result == true) {
                    setReturnCode(OK);
                    close();
                } else {
                    setProgressMessage(errorMessage.toString(), true);
                    btnActivate.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void setInput() {
        btnActivate.setEnabled(validateInput());

        lblRequestCodeDetail.setText(ComposerActivationInfoCollector.genRequestActivationInfo());
        lblRequestCodeDetail.getParent().layout();
    }

    private boolean validateInput() {
        return txtActivationCode.getText().length() > 0;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout();
        glContainer.verticalSpacing = 15;
        container.setLayout(glContainer);

        Composite requestCodeComposite = new Composite(container, SWT.NONE);
        requestCodeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        requestCodeComposite.setLayout(new GridLayout(3, false));

        Label lblRequestCode = new Label(requestCodeComposite, SWT.NONE);
        lblRequestCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblRequestCode.setText(MessageConstants.ACTIVATION_REQUEST_CODE);

        lblRequestCodeDetail = new Label(requestCodeComposite, SWT.NONE);
        lblRequestCodeDetail.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblRequestCodeDetail.setForeground(ColorUtil.getTextLinkColor());
        increateFontSize(lblRequestCodeDetail, 2);
        
        btnCopyToClipboard = new Button(requestCodeComposite, SWT.PUSH);
        btnCopyToClipboard.setText(MessageConstants.BTN_COPY_TITLE);

        Composite messageComposite = new Composite(container, SWT.NONE);
        GridLayout glMessageComposite = new GridLayout();
        glMessageComposite.verticalSpacing = 10;
        messageComposite.setLayout(glMessageComposite);
        messageComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        lnkActivationCode = new Link(messageComposite, SWT.WRAP);
        lnkActivationCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        lnkActivationCode
                .setText(MessageConstants.ActivationOfflineDialogV2_LBL_ACTIVATION_URL);

        Composite activationCodeComposite = new Composite(messageComposite, SWT.NONE);
        activationCodeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout glActivationCode = new GridLayout(2, false);
        glActivationCode.marginHeight = 0;
        glActivationCode.marginWidth = 0;
        activationCodeComposite.setLayout(glActivationCode);

        Label lblActivationCode = new Label(activationCodeComposite, SWT.NONE);
        lblActivationCode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblActivationCode.setText(MessageConstants.ActivationOfflineDialogV2_LBL_ACTIVATION_CODE);

        txtActivationCode = new Text(activationCodeComposite, SWT.BORDER);
        GridData gdActivationCode = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdActivationCode.heightHint = 22;
        txtActivationCode.setLayoutData(gdActivationCode);

        lblProgressMessage = new Label(messageComposite, SWT.NONE);
        lblProgressMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        lnkAgreeTerm = new Link(messageComposite, SWT.WRAP);
        lnkAgreeTerm.setText(MessageConstants.ActivationDialogV2_LBL_AGREE_TERM);
        
        return container;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite bottomBar = new Composite(parent, SWT.NONE);
        bottomBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottomBar.setLayout(new GridLayout(2, false));

        Composite bottomLeftComposite = new Composite(bottomBar, SWT.NONE);
        bottomLeftComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottomLeftComposite.setLayout(new GridLayout());

        lnkOnlineRequest = new Link(bottomLeftComposite, SWT.NONE);
        lnkOnlineRequest.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        lnkOnlineRequest.setText(String.format("<a>%s</a>", MessageConstants.ActivationOfflineDialogV2_LNK_BACK));

        Composite bottomRightComposite = new Composite(bottomBar, SWT.NONE);
        bottomRightComposite.setLayout(new GridLayout());
        bottomRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        btnActivate = new Button(bottomRightComposite, SWT.PUSH);
        btnActivate.setText(MessageConstants.BTN_ACTIVATE_TITLE);
        getShell().setDefaultButton(btnActivate);

        return bottomBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // Do nothing
    }

    private void setProgressMessage(String message, boolean isError) {
        lblProgressMessage.setText(message);
        if (isError) {
            lblProgressMessage.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblProgressMessage.setForeground(ColorUtil.getDefaultTextColor());
        }
        lblProgressMessage.getParent().layout();
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y);
    }
    
    @Override
    public String getDialogTitle() {
        return MessageConstants.DIALOG_OFFLINE_TITLE;
    }
    
    private void increateFontSize(Label label, int sizeIncreased) {
        FontData fontData = label.getFont().getFontData()[0];
        fontData.setHeight(fontData.getHeight() + sizeIncreased);
        label.setFont(new Font(label.getDisplay(), fontData));
    }
}
