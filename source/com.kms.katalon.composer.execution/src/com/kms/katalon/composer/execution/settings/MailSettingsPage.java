package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.MessageDialogWithLink;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.ReportFormatType;
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

    private Text txtHost, txtPort, txtUsername, txtPassword;

    private Combo comboProtocol;

    private Button btnChkAttachment;

    private Text txtSender, txtRecipients, txtSubject, txtCc, txtBcc;

    private Link lnkEditTemplate;

    private Button btnSendTestEmail;

    private EmailConfigValidator validator;

    private Group grpReportFormatOptions;

    private Composite attachmentOptionsComposite;

    private Map<ReportFormatType, Button> formatOptionCheckboxes;

    private Button chckEncrypt;
    
    private Button sendEmailFailedTestRadio;
    
    private Button sendEmailAllcasesRadio;
    
    private Button chckUseUsernameAsSender;

    public MailSettingsPage() {
        super();
        noDefaultButton();
        store = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
        validator = new EmailConfigValidator();
        formatOptionCheckboxes = new HashMap<>();
    }

    public EmailSettingStore getSettingStore() {
        return store;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = createComposite(parent, 1, 1);

        createServerGroup(container);

        createPostExecuteGroup(container);

        createReportFormatGroup(container);

        addSendTestFailedOnlyCheckbox(container);
        
        createSendTestEmailButton(container);

        registerControlListers();

        updateInput();

        return container;
    }

    private void updateInput() {
        try {
            EmailSettingStore settingStore = getSettingStore();
            boolean encrytionEnabled = settingStore.isEncryptionEnabled();
            chckEncrypt.setSelection(encrytionEnabled);
            txtHost.setText(settingStore.getHost(encrytionEnabled));
            txtPort.setText(settingStore.getPort(encrytionEnabled));
            txtUsername.setText(settingStore.getUsername(encrytionEnabled));
            txtPassword.setText(settingStore.getPassword(encrytionEnabled));
            comboProtocol.setText(settingStore.getProtocol(encrytionEnabled));
            btnChkAttachment.setSelection(settingStore.isAddAttachment());
            updateReportFormatOptionsStatus();

            chckUseUsernameAsSender.setSelection(settingStore.useUsernameAsSender());
            
            String sender = settingStore.getSender();
            if (settingStore.useUsernameAsSender()) {
                sender = txtUsername.getText();
            }
            txtSender.setText(sender);
            
            if (chckUseUsernameAsSender.getSelection()) {
                txtSender.setEnabled(false);;
            } else {
                txtSender.setEnabled(true);
            }
            txtRecipients.setText(settingStore.getRecipients(encrytionEnabled));
            txtCc.setText(settingStore.getEmailCc());
            txtBcc.setText(settingStore.getEmailBcc());
            txtSubject.setText(settingStore.getEmailSubject());

            settingStore.getReportFormatOptions().forEach(format -> {
                formatOptionCheckboxes.get(format).setSelection(true);
            });
            sendEmailFailedTestRadio.setSelection(settingStore.isSendEmailTestFailedOnly());
            sendEmailAllcasesRadio.setSelection(!settingStore.isSendEmailTestFailedOnly());
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void registerControlListers() {
        lnkEditTemplate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.SETTINGS_PAGE_CHANGE,
                        StringConstants.EMAIL_TEMPLATE_PAGE_ID);
            }
        });

        btnSendTestEmail.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EmailConfig emailConfig = new EmailConfig();
                emailConfig.setFrom(txtSender.getText());
                emailConfig.setUsername(txtUsername.getText());
                emailConfig.setHost(txtHost.getText());
                emailConfig.setPassword(txtPassword.getText());
                emailConfig.setPort(txtPort.getText());
                emailConfig.setSecurityProtocol(MailSecurityProtocolType.valueOf(comboProtocol.getText()));
                emailConfig.addRecipients(txtRecipients.getText());
                emailConfig.setSubject(txtSubject.getText());
                emailConfig.setCc(txtCc.getText());
                emailConfig.setBcc(txtBcc.getText());
                emailConfig.setAttachmentOptions(getSelectedAttachmentOptions());
                try {
                    emailConfig.setHtmlMessage(getSettingStore().getEmailHTMLTemplate());
                } catch (IOException | URISyntaxException ex) {
                    LoggerSingleton.logError(ex);
                }
                sendTestEmail(emailConfig);
            }
        });

        txtPort.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                e.doit = StringUtils.isNumeric(e.text);
                if (e.doit) {
                    setValidationAndEnableSendEmail("port", StringUtils.isNotEmpty(e.text));
                }
            }
        });

        txtHost.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setValidationAndEnableSendEmail("host", StringUtils.isNotEmpty(txtHost.getText()));
            }
        });

        txtPassword.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setValidationAndEnableSendEmail("password", StringUtils.isNotEmpty(txtPassword.getText()));
            }
        });

        txtUsername.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setValidationAndEnableSendEmail("username", validator.isValidEmail(txtUsername.getText()));
                if (chckUseUsernameAsSender.getSelection()) {
                    txtSender.setText(txtUsername.getText());
                }
            }
        });
        
        chckUseUsernameAsSender.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (chckUseUsernameAsSender.getSelection()) {
                    txtSender.setEnabled(false);
                    txtSender.setText(txtUsername.getText());
                } else {
                    txtSender.setEnabled(true);
                }
            }
        });
        
        txtSender.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                setValidationAndEnableSendEmail("sender", validator.isValidEmail(txtSender.getText()));
            }
        });

        txtRecipients.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setValidationAndEnableSendEmail("recipients", validator.isValidListEmail(txtRecipients.getText()));
            }
        });

        txtCc.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = txtCc.getText();
                setValidationAndEnableSendEmail("cc", StringUtils.isBlank(text) || validator.isValidEmail(text));
            }
        });

        txtBcc.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = txtBcc.getText();
                setValidationAndEnableSendEmail("bcc", StringUtils.isBlank(text) || validator.isValidEmail(text));
            }
        });

        btnChkAttachment.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateReportFormatOptionsStatus();
            }
        });
    }

    private List<ReportFormatType> getSelectedAttachmentOptions() {
        return formatOptionCheckboxes.entrySet()
                .stream()
                .filter(e -> e.getValue().getSelection())
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    private void updateReportFormatOptionsStatus() {
        ControlUtils.recursiveSetEnabled(attachmentOptionsComposite, btnChkAttachment.getSelection());
    }

    private void setValidationAndEnableSendEmail(String property, boolean validated) {
        validator.setValidation(property, validated);
        btnSendTestEmail.setEnabled(validator.isValidated());
    }

    @Override
    protected void performApply() {
        super.performApply();
    }

    @Override
    public boolean performOk() {
        if (!isControlCreated()) {
            return super.performOk();
        }
        try {
            boolean encrytionEnabled = chckEncrypt.getSelection();

            EmailSettingStore settingStore = getSettingStore();
            settingStore.enableAuthenticationEncryption(encrytionEnabled);
            settingStore.setHost(txtHost.getText(), encrytionEnabled);
            settingStore.setPort(txtPort.getText(), encrytionEnabled);
            settingStore.setUsername(txtUsername.getText(), encrytionEnabled);
            settingStore.setPassword(txtPassword.getText(), encrytionEnabled);
            settingStore.setProtocol(comboProtocol.getText(), encrytionEnabled);
            settingStore.setIsAddAttachment(btnChkAttachment.getSelection());
            settingStore.setEmailSubject(txtSubject.getText());
            settingStore.setEmailCc(txtCc.getText());
            settingStore.setEmailBcc(txtBcc.getText());
            settingStore.setUseUsernameAsSender(chckUseUsernameAsSender.getSelection());
            settingStore.setSender(txtSender.getText());
            settingStore.setRecipients(txtRecipients.getText(), encrytionEnabled);
            settingStore.setReportFormatOptions(getSelectedAttachmentOptions());
            settingStore.setSendEmailTestFailedOnly(sendEmailFailedTestRadio.getSelection());
            return super.performOk();
        } catch (IOException | GeneralSecurityException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    private void createPostExecuteGroup(Composite container) {
        Group postExecuteGroup = createGroup(container, ComposerExecutionMessageConstants.PREF_GROUP_LBL_EXECUTION_MAIL,
                2, 1, GridData.FILL_HORIZONTAL);
        
        chckUseUsernameAsSender = new Button(postExecuteGroup, SWT.CHECK);
        chckUseUsernameAsSender.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        chckUseUsernameAsSender.setText(ComposerExecutionMessageConstants.PREF_CHECK_USE_USERNAME_AS_SENDER);
        
        txtSender = createTextFieldWithLabel(postExecuteGroup, ComposerExecutionMessageConstants.PREF_LBL_REPORT_SENDER,
                StringUtils.EMPTY, 1);

        txtRecipients = createTextFieldWithLabel(postExecuteGroup,
                ComposerExecutionMessageConstants.PREF_LBL_REPORT_RECIPIENTS,
                ComposerExecutionMessageConstants.PREF_TXT_PH_RECIPIENTS, 1);

        txtCc = createTextFieldWithLabel(postExecuteGroup, ComposerExecutionMessageConstants.PREF_LBL_CC,
                StringUtils.EMPTY, 1);

        txtBcc = createTextFieldWithLabel(postExecuteGroup, ComposerExecutionMessageConstants.PREF_LBL_BCC,
                StringUtils.EMPTY, 1);

        txtSubject = createTextFieldWithLabel(postExecuteGroup, ComposerExecutionMessageConstants.PREF_LBL_SUBJECT,
                StringUtils.EMPTY, 1);

        Label lblBody = new Label(postExecuteGroup, SWT.NONE);
        lblBody.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblBody.setText(ComposerExecutionMessageConstants.PREF_LBL_BODY);

        lnkEditTemplate = new Link(postExecuteGroup, SWT.NONE);
        lnkEditTemplate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lnkEditTemplate.setText(String.format("<a>%s</a>", ComposerExecutionMessageConstants.PREF_LNK_EDIT_TEMPLATE));
    }

    private void createReportFormatGroup(Composite container) {
        grpReportFormatOptions = new Group(container, SWT.NONE);
        grpReportFormatOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        grpReportFormatOptions.setText(ComposerExecutionMessageConstants.PREF_LBL_REPORT_FORMAT);

        GridLayout reportFormatLayout = new GridLayout(1, true);
        reportFormatLayout.marginLeft = 0;
        reportFormatLayout.marginRight = 0;
        reportFormatLayout.marginHeight = 5;
        grpReportFormatOptions.setLayout(reportFormatLayout);

        btnChkAttachment = new Button(grpReportFormatOptions, SWT.CHECK);
        btnChkAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnChkAttachment.setText(ComposerExecutionMessageConstants.PREF_LBL_INCLUDE_ATTACHMENT);

        attachmentOptionsComposite = new Composite(grpReportFormatOptions, SWT.NONE);
        attachmentOptionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout attachmentsLayout = new GridLayout(1, true);
        attachmentsLayout.marginLeft = 15;
        attachmentsLayout.marginRight = 0;
        attachmentsLayout.marginHeight = 0;
        attachmentOptionsComposite.setLayout(attachmentsLayout);

        for (ReportFormatType formatType : ReportFormatType.values()) {
            Button btnFormmatingType = new Button(attachmentOptionsComposite, SWT.CHECK);
            btnFormmatingType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            btnFormmatingType.setText(formatType.toString());
            btnFormmatingType.setData(formatType);

            formatOptionCheckboxes.put(formatType, btnFormmatingType);
        }
    }
    
    private void addSendTestFailedOnlyCheckbox(Composite container) {
        Composite radioSelectionComposite = new Composite(container, SWT.NONE);
        radioSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        
        GridLayout glRadioSelection = new GridLayout(1, false);
        glRadioSelection.marginHeight = 0;
        glRadioSelection.marginWidth = 0;
        glRadioSelection.marginLeft = 10;
        radioSelectionComposite.setLayout(glRadioSelection);
        
        sendEmailAllcasesRadio = new Button(radioSelectionComposite, SWT.RADIO);
        sendEmailAllcasesRadio.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        sendEmailAllcasesRadio.setText(StringConstants.DIA_MSG_SEND_EMAIL_REPORT_FOR_ALL_CASES);
        
        sendEmailFailedTestRadio = new Button(radioSelectionComposite, SWT.RADIO);
        sendEmailFailedTestRadio.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        sendEmailFailedTestRadio.setText(StringConstants.DIA_MSG_SEND_EMAIL_REPORT_FOR_FAILED_TEST_ONLY);
    }

    private void createSendTestEmailButton(Composite parent) {
        btnSendTestEmail = new Button(parent, SWT.PUSH);
        btnSendTestEmail.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        btnSendTestEmail.setText(ComposerExecutionMessageConstants.PREF_LBL_SEND_TEST_EMAIL);
    }

    private void createServerGroup(Composite container) {
        Group serverGroup = createGroup(container, StringConstants.PREF_GROUP_LBL_MAIL_SERVER, 4, 1,
                GridData.FILL_HORIZONTAL);

        txtHost = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_HOST, MAIL_CONFIG_HOST_HINT, 1);
        txtPort = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_PORT, MAIL_CONFIG_PORT_HINT, 1);

        txtUsername = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_USERNAME,
                MAIL_CONFIG_USERNAME_HINT, 1);
        txtPassword = createTextFieldWithLabel(serverGroup, StringConstants.PREF_LBL_PASSWORD, "", 1);
        txtPassword.setEchoChar(PASSWORD_CHAR_MASK);

        createLabel(serverGroup, StringConstants.PREF_LBL_SECURITY_PROTOCOL);

        comboProtocol = new Combo(serverGroup, SWT.READ_ONLY);
        comboProtocol.setLayoutData(new GridData(SWT.LEFT, GridData.FILL, false, true, 1, 1));
        comboProtocol.setItems(MailSecurityProtocolType.getStringValues());

        chckEncrypt = new Button(serverGroup, SWT.CHECK);
        chckEncrypt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 4, 1));
        chckEncrypt.setText(ComposerExecutionMessageConstants.PREF_CHECK_ENABLE_AUTHENTICATION_ENCRYPTION);
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
        GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false, hspan, 1);
        txtField.setLayoutData(gridData);
        txtField.setMessage(hintText);
        return txtField;
    }

    private void createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, GridData.CENTER, false, false, 1, 1));
        label.setText(labelText);
    }

    private Composite createComposite(Composite parent, int numColumns, int horizontalSpan) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, horizontalSpan, 1));
        container.setLayout(new GridLayout(numColumns, false));
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

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EMAIL;
    }

    private class EmailConfigValidator {
        private Map<String, Boolean> validation;

        private static final String EMAIL_TEXT_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        public EmailConfigValidator() {
            validation = new HashMap<>();
            validation.put("host", false);
            validation.put("port", false);
            validation.put("username", false);
            validation.put("password", false);
            validation.put("sender", false);
            validation.put("recipients", false);
            validation.put("cc", true);
            validation.put("bcc", true);
        }

        private boolean isValidated() {
            return !(validation.entrySet().parallelStream().filter(field -> !field.getValue()).count() > 0);
        }

        public void setValidation(String key, boolean value) {
            validation.put(key, value);
        }

        public boolean isValidEmail(String email) {
            if (StringUtils.isBlank(email)) {
                return false;
            }
            return Pattern.matches(EMAIL_TEXT_PATTERN, email.trim());
        }

        public boolean isValidListEmail(String lstEmail) {
            if (StringUtils.isBlank(lstEmail)) {
                return false;
            }
            for (String email : lstEmail.trim().split(MailUtil.EMAIL_SEPARATOR)) {
                if (!Pattern.matches(EMAIL_TEXT_PATTERN, email.trim())) {
                    return false;
                }
            }
            return true;
        }
    }
}
