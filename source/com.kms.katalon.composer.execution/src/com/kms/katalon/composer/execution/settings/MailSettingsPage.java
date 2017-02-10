package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.MessageDialogWithLink;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.AddMailRecipientDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.setting.EmailSettingStore;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;

public class MailSettingsPage extends PreferencePageWithHelp {
    public static final String MAIL_CONFIG_USERNAME_HINT = "E.g: testemailkms@gmail.com";

    public static final String MAIL_CONFIG_PORT_HINT = "E.g: 465";

    public static final String MAIL_CONFIG_HOST_HINT = "E.g: smtp.gmail.com";

    private static final char PASSWORD_CHAR_MASK = '\u2022';

    private EmailSettingStore store;

    private Text txtHost, txtPort, txtUsername, txtPassword, txtSignature;

    private Combo comboProtocol;

    private Button btnChkAttachment;

    private ListViewer listViewerRecipients;

    private Button btnAddRecipient, btnDeleteRecipient, btnClearRecipient;

    public MailSettingsPage() {
        super();
        noDefaultButton();
        store = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
    }

    public EmailSettingStore getSettingStore() {
        return store;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = createComposite(parent, 1, 1);

        createServerGroup(container);

        createPostExecuteGroup(container);

        updateInput();

        return container;
    }

    private void updateInput() {
        try {
            EmailSettingStore settingStore = getSettingStore();
            txtHost.setText(settingStore.getHost());
            txtPort.setText(settingStore.getPort());
            txtUsername.setText(settingStore.getUsername());
            txtPassword.setText(settingStore.getPassword());
            txtSignature.setText(settingStore.getSignature());
            comboProtocol.setText(settingStore.getProtocol());
            btnChkAttachment.setSelection(settingStore.isAddAttachment());
            listViewerRecipients
                    .setInput(TestSuiteController.getInstance().mailRcpStringToArray(settingStore.getRecipients()));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        try {
            EmailSettingStore settingStore = getSettingStore();
            settingStore.setHost(txtHost.getText());
            settingStore.setPort(txtPort.getText());
            settingStore.setUsername(txtUsername.getText());
            settingStore.setPassword(txtPassword.getText());
            settingStore.setSignature(txtSignature.getText());
            settingStore.setProtocol(comboProtocol.getText());
            settingStore.setIsAddAttachment(btnChkAttachment.getSelection());
            settingStore.setRecipients(
                    TestSuiteController.getInstance().arrayMailRcpToString(listViewerRecipients.getList().getItems()));
            return super.performOk();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    private void createPostExecuteGroup(Composite container) {
        Group postExecuteGroup = createGroup(container, StringConstants.PREF_GROUP_LBL_EXECUTION_MAIL, 2, 1,
                GridData.FILL_BOTH);
        createLabel(postExecuteGroup, StringConstants.PREF_LBL_REPORT_RECIPIENTS);

        createCompositeRecipients(postExecuteGroup);

        createLabel(postExecuteGroup, StringConstants.PREF_LBL_SIGNATURE);
        txtSignature = new Text(postExecuteGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        GridData gridDataTxtSignature = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
        gridDataTxtSignature.heightHint = 60;
        txtSignature.setLayoutData(gridDataTxtSignature);

        createLabel(postExecuteGroup, "");

        Composite actionSpacer = createCompositeWithNoMargin(postExecuteGroup, 2, 1);

        btnChkAttachment = new Button(actionSpacer, SWT.CHECK);
        btnChkAttachment.setLayoutData(new GridData(GridData.FILL, SWT.CENTER, true, false, 1, 1));
        btnChkAttachment.setText(StringConstants.PREF_LBL_SEND_ATTACHMENT);

        Composite testSpacer = createCompositeWithNoMargin(actionSpacer, 1, 1);
        testSpacer.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));

        Button testButton = new Button(testSpacer, SWT.PUSH);
        testButton.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false, 1, 1));
        testButton.setText(StringConstants.PREF_LBL_SEND_TEST_EMAIL);
        testButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final EmailConfig conf = buildTestEmailConfig();
                if (conf == null) {
                    return;
                }
                sendTestEmail(conf);
            }
        });
    }

    private void createCompositeRecipients(Composite parent) {
        Composite compositeRecipients = createCompositeWithNoMargin(parent, 2, 1);

        listViewerRecipients = new ListViewer(compositeRecipients, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        GridData gdListMailRcp = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdListMailRcp.heightHint = 70;
        listViewerRecipients.getList().setLayoutData(gdListMailRcp);
        listViewerRecipients.setContentProvider(ArrayContentProvider.getInstance());

        Composite compositeRecipientsButtons = new Composite(compositeRecipients, SWT.NONE);

        GridData gridDataCompositeRecipients = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        compositeRecipientsButtons.setLayoutData(gridDataCompositeRecipients);

        GridLayout glCompositeMailRcpButtons = new GridLayout(1, false);
        glCompositeMailRcpButtons.marginWidth = 0;
        glCompositeMailRcpButtons.marginHeight = 0;
        compositeRecipientsButtons.setLayout(glCompositeMailRcpButtons);

        btnAddRecipient = new Button(compositeRecipientsButtons, SWT.FLAT);
        btnAddRecipient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddRecipient.setText(ComposerExecutionMessageConstants.LBL_SETT_RECIPIENT_ADD);
        btnAddRecipient.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addRecipient();
            }
        });

        btnDeleteRecipient = new Button(compositeRecipientsButtons, SWT.FLAT);
        btnDeleteRecipient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnDeleteRecipient.setText(ComposerExecutionMessageConstants.LBL_SETT_RECIPIENT_DEL);
        btnDeleteRecipient.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteSelectedRecipients();
            }
        });

        btnClearRecipient = new Button(compositeRecipientsButtons, SWT.FLAT);
        btnClearRecipient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnClearRecipient.setText(ComposerExecutionMessageConstants.LBL_SETT_RECIPIENT_CLR);
        btnClearRecipient.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearRecipients();
            }
        });
    }

    private void deleteSelectedRecipients() {
        Object[] emails = ((IStructuredSelection) listViewerRecipients.getSelection()).toArray();
        if (emails.length > 0) {
            listViewerRecipients.remove(emails);
        }
    }

    private void addRecipient() {
        AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(Display.getDefault().getActiveShell(),
                listViewerRecipients.getList().getItems());
        addMailDialog.open();
        if (addMailDialog.getReturnCode() != Dialog.OK) {
            return;
        }

        String[] emails = addMailDialog.getEmails();
        if (emails.length > 0) {
            listViewerRecipients.add(addMailDialog.getEmails());

        }
    }

    private void clearRecipients() {
        if (listViewerRecipients.getList().getItemCount() <= 0) {
            return;
        }
        listViewerRecipients.setInput(new String[0]);
    }

    private void createServerGroup(Composite container) {
        Group serverGroup = createGroup(container, StringConstants.PREF_GROUP_LBL_MAIL_SERVER, 4, 1,
                GridData.FILL_HORIZONTAL);

        txtHost = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_HOST, MAIL_CONFIG_HOST_HINT, 1);
        txtPort = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_PORT, MAIL_CONFIG_PORT_HINT, 1);
        txtPort.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                final String oldString = txtPort.getText();
                final String newString = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);
                try {
                    Integer.parseInt(newString);
                } catch (NumberFormatException ex) {
                    e.doit = false;
                }
            }
        });

        txtUsername = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_USERNAME,
                MAIL_CONFIG_USERNAME_HINT, 1);
        txtPassword = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_PASSWORD, "", 1);
        txtPassword.setEchoChar(PASSWORD_CHAR_MASK);

        createLabel(serverGroup, StringConstants.PREF_LBL_SECURITY_PROTOCOL);

        comboProtocol = new Combo(serverGroup, SWT.READ_ONLY);
        comboProtocol.setLayoutData(new GridData(SWT.LEFT, GridData.FILL, false, true, 1, 1));
        comboProtocol.setItems(MailSecurityProtocolType.getStringValues());
    }

    private void sendTestEmail(final EmailConfig conf) {
        String message = ComposerExecutionMessageConstants.PREF_MSG_TEST_EMAIL_IS_SENT_SUCCESSFULLY;
        String messageTitle = StringConstants.INFO;
        int messageType = MessageDialog.INFORMATION;
        Shell shell = getShell();
        try {

            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(StringConstants.PREF_SEND_TEST_EMAIL_JOB_NAME, IProgressMonitor.UNKNOWN);
                    try {
                        MailUtil.sendTestMail(conf);
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException ex) {
            Throwable rootException = ex.getTargetException();
            LoggerSingleton.logError(rootException);
            messageTitle = StringConstants.ERROR;
            messageType = MessageDialog.ERROR;
            message = rootException.getMessage();
            if (StringUtils.startsWith(message,
                    ComposerExecutionMessageConstants.PREF_FAILED_APACHE_MAIL_PREFIX_ERROR_MSG)) {
                message = StringUtils.removeStart(message,
                        ComposerExecutionMessageConstants.PREF_FAILED_APACHE_MAIL_PREFIX_ERROR_MSG);
                message = MessageFormat.format(ComposerExecutionMessageConstants.PREF_REPLACEMENT_APACHE_MAIL_ERROR_MSG,
                        message);
            }
        } catch (InterruptedException ex) {
            LoggerSingleton.logError(ex);
        } finally {
            MessageDialogWithLink.open(messageType, shell, messageTitle, message, SWT.NONE);
        }
    }

    private Text createTextFieldWithLabel(Composite parent, String labelText, String hintText, int hspan) {
        createLabel(parent, labelText);

        Text txtField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, hspan, 1);
        gridData.widthHint = 70;
        gridData.heightHint = 20;
        txtField.setLayoutData(gridData);
        txtField.setMessage(hintText);
        return txtField;
    }

    private void createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, GridData.CENTER, false, true, 1, 1));
        label.setText(labelText);
    }

    private Composite createComposite(Composite parent, int numColumns, int horizontalSpan) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, horizontalSpan, 1));
        container.setLayout(new GridLayout(numColumns, false));
        return container;
    }

    private Composite createCompositeWithNoMargin(Composite parent, int numColumns, int horizontalSpan) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, horizontalSpan, 1));
        GridLayout layout = new GridLayout(numColumns, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        container.setLayout(layout);
        return container;
    }

    private static Group createGroup(Composite parent, String text, int columns, int hspan, int fill) {
        Group g = new Group(parent, SWT.NONE);
        g.setLayout(new GridLayout(columns, false));
        g.setText(text);
        g.setFont(parent.getFont());
        GridData gd = new GridData(fill);
        gd.horizontalSpan = hspan;
        g.setLayoutData(gd);
        return g;
    }

    private EmailConfig buildTestEmailConfig() {
        String[] mailRecipients = getRecipients(
                TestSuiteController.getInstance().arrayMailRcpToString(listViewerRecipients.getList().getItems()));
        if (mailRecipients.length <= 0) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_RECIPIENTS);
            return null;
        }

        if (txtHost.getText().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_HOST);
            return null;
        }

        if (txtPort.getText().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_PORT);
            return null;
        } else {
            try {
                Integer.valueOf(txtPort.getText());
            } catch (NumberFormatException e) {
                MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_INVALID_PORT);
                return null;
            }
        }

        if (txtUsername.getText().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_USERNAME);
            return null;
        }

        if (txtPassword.getText().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_PASSWORD);
            return null;
        }

        EmailConfig conf = new EmailConfig();
        conf.addRecipients(Arrays.asList(mailRecipients));
        conf.setHost(txtHost.getText());
        conf.setPort(txtPort.getText());
        conf.setFrom(txtUsername.getText());
        conf.setSecurityProtocol(MailSecurityProtocolType.valueOf(comboProtocol.getText()));
        conf.setUsername(txtUsername.getText());
        conf.setPassword(txtPassword.getText());
        conf.setSendAttachment(false);
        return conf;
    }

    private static String[] getRecipients(String reportRecipients) {
        return StringUtils.split(reportRecipients.trim(), ";");
    }
    
    @Override
    protected boolean hasDocumentation() {
        return true;
    }
    
    @Override
    protected String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EMAIL;
    }
}
