package com.kms.katalon.composer.execution.settings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
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

    private Text txtRecipients, txtSubject, txtCc, txtBcc;

    private Link lnkEditTemplate;

    private Button btnSendTestEmail;

    private EmailConfigValidator validator;

    public MailSettingsPage() {
        super();
        noDefaultButton();
        store = new EmailSettingStore(ProjectController.getInstance().getCurrentProject());
        validator = new EmailConfigValidator();
    }

    public EmailSettingStore getSettingStore() {
        return store;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = createComposite(parent, 1, 1);

        createServerGroup(container);

        createPostExecuteGroup(container);

        registerControlListers();

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
            comboProtocol.setText(settingStore.getProtocol());
            btnChkAttachment.setSelection(settingStore.isAddAttachment());

            txtRecipients.setText(settingStore.getRecipients());
            txtCc.setText(settingStore.getEmailCc());
            txtBcc.setText(settingStore.getEmailBcc());
            txtSubject.setText(settingStore.getEmailSubject());
        } catch (IOException e) {
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
                emailConfig.setFrom(txtUsername.getText());
                emailConfig.setUsername(txtUsername.getText());
                emailConfig.setHost(txtHost.getText());
                emailConfig.setPassword(txtPassword.getText());
                emailConfig.setPort(txtPort.getText());
                emailConfig.setSecurityProtocol(MailSecurityProtocolType.valueOf(comboProtocol.getText()));
                emailConfig.addRecipients(txtRecipients.getText());
                emailConfig.setSubject(txtSubject.getText());
                emailConfig.setCc(txtCc.getText());
                emailConfig.setBcc(txtBcc.getText());
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
            EmailSettingStore settingStore = getSettingStore();
            settingStore.setHost(txtHost.getText());
            settingStore.setPort(txtPort.getText());
            settingStore.setUsername(txtUsername.getText());
            settingStore.setPassword(txtPassword.getText());
            settingStore.setProtocol(comboProtocol.getText());
            settingStore.setIsAddAttachment(btnChkAttachment.getSelection());
            settingStore.setEmailSubject(txtSubject.getText());
            settingStore.setEmailCc(txtCc.getText());
            settingStore.setEmailBcc(txtBcc.getText());
            settingStore.setRecipients(txtRecipients.getText());
            return super.performOk();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    private void createPostExecuteGroup(Composite container) {
        Group postExecuteGroup = createGroup(container, ComposerExecutionMessageConstants.PREF_GROUP_LBL_EXECUTION_MAIL,
                2, 1, GridData.FILL_HORIZONTAL);

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

        btnChkAttachment = new Button(postExecuteGroup, SWT.CHECK);
        btnChkAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        btnChkAttachment.setText(ComposerExecutionMessageConstants.PREF_LBL_INCLUDE_ATTACHMENT);

        btnSendTestEmail = new Button(postExecuteGroup, SWT.PUSH);
        GridData layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
        layoutData.widthHint = 90;
        btnSendTestEmail.setLayoutData(layoutData);
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
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
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
