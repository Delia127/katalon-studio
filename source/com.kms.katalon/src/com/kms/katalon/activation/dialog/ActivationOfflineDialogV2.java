package com.kms.katalon.activation.dialog;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;

public class ActivationOfflineDialogV2 extends AbstractDialog {

    public static final int REQUEST_ONLINE_CODE = 1001;
    
    public static final int REQUEST_SIGNUP_CODE = 1002;

    public static final String[] filterExtLicense = { "*.lic;*.LIC", "*.*" };

    public static final String[] filterNameLicense = { "License Files (*.lic;*.LIC)", "All Files (*.*)" };

    private Label lblProgressMessage;
    
    private Link lnkOfflineActivation;

    private Button btnOnlineRequest;
    
    private Button btnCopyToClipboard;
    
    private Text txtMachineKeyDetail;
    
    private Text txtLicenseFile;
    
    private Button btnChooseFile;

    private Button btnActivate;
    
    private boolean navigateFromSignUp;
    
    private Link lnkAgreeTerm;

    public ActivationOfflineDialogV2(Shell parentShell, boolean navigateFromSignUp) {
        super(parentShell, false);
        this.navigateFromSignUp = navigateFromSignUp;
    }

    @Override
    protected void registerControlModifyListeners() {
        txtLicenseFile.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                btnActivate.setEnabled(validateInput());
            }
        });
        
        btnCopyToClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Clipboard cb = new Clipboard(getShell().getDisplay());
                cb.setContents(new Object[] { txtMachineKeyDetail.getText() },
                        new Transfer[] { TextTransfer.getInstance() });
            }
        });
        
        btnOnlineRequest.addSelectionListener(new SelectionAdapter() {
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
        
        lnkAgreeTerm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(StringConstants.AGREE_TERM_URL);
            }
        });
        
        lnkOfflineActivation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);

            }
        });

        btnActivate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnActivate.setEnabled(false);
                setProgressMessage(MessageConstants.ActivationDialogV2_MSG_ACTIVATING, false);
                StringBuilder errorMessage = new StringBuilder();
                String licenseFilePath = txtLicenseFile.getText();
                
                try {
                    String activationCode = FileUtils.readFileToString(new File(licenseFilePath));
                    boolean result = ActivationInfoCollector.activateOffline(activationCode, errorMessage);
                    if (result == true) {
                        setReturnCode(OK);
                        close();
                    } else {
                        setProgressMessage(errorMessage.toString(), true);
                        btnActivate.setEnabled(true);
                    }
                } catch (IOException ex) {
                    LoggerSingleton.logError(ex);
                    setProgressMessage(ex.getMessage(), true);
                }
            }
        });
        
        btnChooseFile.addSelectionListener(new SelectionAdapter() {
           @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SINGLE);
                fileDialog.setFilterExtensions(filterExtLicense);
                fileDialog.setFilterNames(filterNameLicense);
                fileDialog.setText(MessageConstants.DIALOG_SELECT_LICENSE);
                String filePath = fileDialog.open();
                txtLicenseFile.setText(filePath);
            } 
        });
    }

    @Override
    protected void setInput() {
        btnActivate.setEnabled(validateInput());
    }

    private boolean validateInput() {
        //require length > 4, extension file is .lic
        return txtLicenseFile.getText().length() > 4;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout();
        glContainer.verticalSpacing = 5;
        container.setLayout(glContainer);

        GridData gdLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdLabel.widthHint = 80;

        GridData gdBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gdBtn.widthHint = 80;

        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        composite.setLayout(new GridLayout(3, false));

        Label lblMachineKey = new Label(composite, SWT.NONE);
        lblMachineKey.setLayoutData(gdLabel);
        lblMachineKey.setText(MessageConstants.ActivationOfflineDialogV2_LBL_MACHINE_KEY);

        txtMachineKeyDetail = new Text(composite, SWT.READ_ONLY);
        txtMachineKeyDetail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtMachineKeyDetail.setForeground(ColorUtil.getTextLinkColor());
        try {
            txtMachineKeyDetail.setText(MachineUtil.getMachineId());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        btnCopyToClipboard = new Button(composite, SWT.PUSH);
        btnCopyToClipboard.setLayoutData(gdBtn);
        btnCopyToClipboard.setText(MessageConstants.BTN_COPY_TITLE);

        Label lblLicenseFile = new Label(composite, SWT.NONE);
        lblLicenseFile.setLayoutData(gdLabel);
        lblLicenseFile.setText(MessageConstants.ActivationOfflineDialogV2_LBL_LICENSE_FILE);

        txtLicenseFile = new Text(composite, SWT.BORDER);
        txtLicenseFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtLicenseFile.setFocus();

        btnChooseFile = new Button(composite, SWT.PUSH);
        btnChooseFile.setLayoutData(gdBtn);
        btnChooseFile.setText(MessageConstants.ActivationOfflineDialogV2_BTN_CHOOSE_FILE);

        lblProgressMessage = new Label(container, SWT.WRAP);
        GridData gdLblProgressMessage = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
        lblProgressMessage.setLayoutData(gdLblProgressMessage);

        Composite offlineComposite = new Composite(container, SWT.NONE);
        GridLayout glOfflineComposite = new GridLayout();
        glOfflineComposite.verticalSpacing = 5;
        offlineComposite.setLayout(glOfflineComposite);
        offlineComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        lnkOfflineActivation = new Link(offlineComposite, SWT.NONE);
        lnkOfflineActivation.setText(MessageConstants.ActivationDialogV2_LBL_LEARN_ABOUT_KS);

        lnkAgreeTerm = new Link(offlineComposite, SWT.WRAP);
        GridData gdLnkAgreeTerm = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lnkAgreeTerm.setLayoutData(gdLnkAgreeTerm);
        lnkAgreeTerm.setText(MessageConstants.ActivationDialogV2_LBL_AGREE_TERM);

        return container;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite bottomBar = new Composite(parent, SWT.NONE);
        bottomBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottomBar.setLayout(new GridLayout(2, false));

        Composite bottomLeftComposite = new Composite(bottomBar, SWT.NONE);
        bottomLeftComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        bottomLeftComposite.setLayout(new GridLayout());

        btnOnlineRequest = new Button(bottomLeftComposite, SWT.PUSH);
        GridData gdBtnOnlineRequest = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdBtnOnlineRequest.widthHint = 84;
        btnOnlineRequest.setLayoutData(gdBtnOnlineRequest);
        btnOnlineRequest.setText(MessageConstants.ActivationOfflineDialogV2_LNK_BACK);
        getShell().setDefaultButton(btnOnlineRequest);

        Composite bottomRightComposite = new Composite(bottomBar, SWT.NONE);
        bottomRightComposite.setLayout(new GridLayout());
        bottomRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        btnActivate = new Button(bottomRightComposite, SWT.PUSH);
        GridData gdBtnActivate = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdBtnActivate.widthHint = 84;
        btnActivate.setLayoutData(gdBtnActivate);
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
            lblProgressMessage.setForeground(ColorUtil.getTextRunningColor());
        }
        lblProgressMessage.getParent().layout();
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y + 20);
    }
    
    @Override
    public String getDialogTitle() {
        return MessageConstants.DIALOG_OFFLINE_TITLE;
    }
}
