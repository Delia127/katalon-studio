package com.kms.katalon.composer.execution.preferences;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MessageDialogWithLink;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.preferences.editor.MultiLineStringFieldEditor;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.entity.EmailConfig;
import com.kms.katalon.execution.preferences.MailPreferenceDefaultValueInitializer;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

@SuppressWarnings("restriction")
public class MailPreferencePage extends FieldEditorPreferencePage {

    private static final char PASSWORD_CHAR_MASK = '\u2022';

    private StringFieldEditor recipientFieldEditor, hostFieldEditor, portFieldEditor, userNameFieldEditor,
            passwordFieldEditor;

    private ComboFieldEditor protocolFieldEditor;

    private Composite subSpacer_1;

    public MailPreferencePage() {
        super();
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite comp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);

        Group group = SWTFactory.createGroup(comp, StringConstants.PREF_GROUP_LBL_MAIL_SERVER, 2, 2,
                GridData.FILL_HORIZONTAL);

        hostFieldEditor = addStringFieldEditor(group, ExecutionPreferenceConstants.MAIL_CONFIG_HOST,
                StringConstants.PREF_LBL_HOST, 1, false);
        ((ExposedStringFieldEditor) hostFieldEditor)
                .setHintText(MailPreferenceDefaultValueInitializer.MAIL_CONFIG_HOST_DEFAULT_VALUE);
        portFieldEditor = addStringFieldEditor(group, ExecutionPreferenceConstants.MAIL_CONFIG_PORT,
                StringConstants.PREF_LBL_PORT, 1, false);
        ((ExposedStringFieldEditor) portFieldEditor)
                .setHintText(MailPreferenceDefaultValueInitializer.MAIL_CONFIG_PORT_DEFAULT_VALUE);
        userNameFieldEditor = addStringFieldEditor(group, ExecutionPreferenceConstants.MAIL_CONFIG_USERNAME,
                StringConstants.PREF_LBL_USERNAME, 1, false);
        ((ExposedStringFieldEditor) userNameFieldEditor)
                .setHintText(MailPreferenceDefaultValueInitializer.MAIL_CONFIG_USERNAME_DEFAULT_VALUE);

        passwordFieldEditor = addStringFieldEditor(group, ExecutionPreferenceConstants.MAIL_CONFIG_PASSWORD,
                StringConstants.PREF_LBL_PASSWORD, 1, false);
        ((ExposedStringFieldEditor) passwordFieldEditor).setEchoChar(PASSWORD_CHAR_MASK);

        Composite spacer = SWTFactory.createComposite(group, 2, 2, GridData.FILL_HORIZONTAL);

        protocolFieldEditor = new ComboFieldEditor(ExecutionPreferenceConstants.MAIL_CONFIG_SECURITY_PROTOCOL,
                StringConstants.PREF_LBL_SECURITY_PROTOCOL, MailUtil.getMailSecurityProtocolTypeArrayValues(), spacer);
        protocolFieldEditor.fillIntoGrid(spacer, protocolFieldEditor.getNumberOfControls());
        addField(protocolFieldEditor);

        group = SWTFactory.createGroup(comp, StringConstants.PREF_GROUP_LBL_EXECUTION_MAIL, 2, 2, GridData.FILL_BOTH);

        recipientFieldEditor = addStringFieldEditor(group, ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS,
                StringConstants.PREF_LBL_REPORT_RECIPIENTS, 2, false);
        addStringFieldEditor(group, ExecutionPreferenceConstants.MAIL_CONFIG_SIGNATURE,
                StringConstants.PREF_LBL_SIGNATURE, 2, true);
        spacer = SWTFactory.createComposite(group, 2, 2, GridData.FILL_HORIZONTAL);

        Composite subSpacer = SWTFactory.createComposite(spacer, 2, 1, GridData.FILL_HORIZONTAL);

        FieldEditor fieldEditor = new BooleanFieldEditor(ExecutionPreferenceConstants.MAIL_CONFIG_ATTACHMENT,
                StringConstants.PREF_LBL_SEND_ATTACHMENT, subSpacer);
        fieldEditor.fillIntoGrid(subSpacer, fieldEditor.getNumberOfControls());
        addField(fieldEditor);

        subSpacer_1 = SWTFactory.createComposite(spacer, 2, 1, GridData.FILL_HORIZONTAL);
        subSpacer_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        Label testLabel = new Label(subSpacer_1, SWT.NONE);
        testLabel.setText(StringConstants.PREF_LBL_SEND_TEST_EMAIL);
        Button testButton = new Button(subSpacer_1, SWT.PUSH);
        testButton.setText(StringConstants.PREF_BUTTON_SEND_TEST_EMAIL);
        testButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final EmailConfig conf = buildTestEmailConfig();
                if (conf == null) {
                    return;
                }

                try {
                    Shell shell = getShell();
                    new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {

                        @Override
                        public void run(IProgressMonitor monitor)
                                throws InvocationTargetException, InterruptedException {
                            monitor.beginTask(StringConstants.PREF_SEND_TEST_EMAIL_JOB_NAME, IProgressMonitor.UNKNOWN);
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    String message = ComposerExecutionMessageConstants.PREF_MSG_TEST_EMAIL_IS_SENT_SUCCESSFULLY;
                                    String messageTitle = StringConstants.INFO;
                                    int messageType = MessageDialog.INFORMATION;
                                    try {
                                        MailUtil.sendTestMail(conf);
                                    } catch (Exception ex) {
                                        LoggerSingleton.logError(ex);
                                        messageTitle = StringConstants.ERROR;
                                        messageType = MessageDialog.ERROR;
                                        message = ex.getMessage();
                                        if (StringUtils.startsWith(message,
                                                ComposerExecutionMessageConstants.PREF_FAILED_APACHE_MAIL_PREFIX_ERROR_MSG)) {
                                            message = StringUtils.removeStart(message,
                                                    ComposerExecutionMessageConstants.PREF_FAILED_APACHE_MAIL_PREFIX_ERROR_MSG);
                                            message = MessageFormat.format(
                                                    ComposerExecutionMessageConstants.PREF_REPLACEMENT_APACHE_MAIL_ERROR_MSG,
                                                    message);
                                        }
                                    } finally {
                                        monitor.done();
                                        MessageDialogWithLink.open(messageType, shell, messageTitle, message, SWT.NONE);
                                    }
                                }
                            });
                        }
                    });
                } catch (InvocationTargetException | InterruptedException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
        initialize();
        checkState();

        return comp;
    }

    protected StringFieldEditor addStringFieldEditor(Composite parent, String preferenceName,
            String preferenceLabelText, int hspan, boolean isMulti) {
        StringFieldEditor fieldEditor = null;
        Composite spacer = SWTFactory.createComposite(parent, 2, hspan, GridData.FILL_HORIZONTAL);
        if (isMulti) {
            fieldEditor = new MultiLineStringFieldEditor(preferenceName, preferenceLabelText,
                    MultiLineStringFieldEditor.UNLIMITED, 60, spacer);
        } else {
            fieldEditor = new ExposedStringFieldEditor(preferenceName, preferenceLabelText, spacer);
        }
        fieldEditor.fillIntoGrid(spacer, fieldEditor.getNumberOfControls());
        addField(fieldEditor);
        return fieldEditor;
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }

    private EmailConfig buildTestEmailConfig() {
        String[] mailRecipients = getRecipients(recipientFieldEditor.getStringValue());
        if (mailRecipients.length <= 0) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_RECIPIENTS);
            return null;
        }

        if (hostFieldEditor.getStringValue().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_HOST);
            return null;
        }

        if (portFieldEditor.getStringValue().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_PORT);
            return null;
        } else {
            try {
                Integer.valueOf(portFieldEditor.getStringValue());
            } catch (NumberFormatException e) {
                MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_INVALID_PORT);
                return null;
            }
        }

        if (userNameFieldEditor.getStringValue().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_USERNAME);
            return null;
        }

        if (passwordFieldEditor.getStringValue().isEmpty()) {
            MessageDialog.openWarning(getShell(), StringConstants.WARN, StringConstants.WARN_EMPTY_PASSWORD);
            return null;
        }

        EmailConfig conf = new EmailConfig();
        conf.addRecipients(Arrays.asList(mailRecipients));
        conf.setHost(hostFieldEditor.getStringValue());
        conf.setPort(portFieldEditor.getStringValue());
        conf.setFrom(userNameFieldEditor.getStringValue());
        conf.setSecurityProtocol(MailSecurityProtocolType.valueOf(protocolFieldEditor.getValue()));
        conf.setUsername(userNameFieldEditor.getStringValue());
        conf.setPassword(passwordFieldEditor.getStringValue());
        conf.setSendAttachment(false);
        return conf;
    }

    private static String[] getRecipients(String reportRecipients) {
        return StringUtils.split(reportRecipients.trim(), ";");
    }

    private class ExposedStringFieldEditor extends StringFieldEditor {
        public ExposedStringFieldEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        public void setHintText(String hint) {
            getTextControl().setMessage(hint);
        }

        public void setEchoChar(char echoChar) {
            getTextControl().setEchoChar(echoChar);
        }
    }
}
