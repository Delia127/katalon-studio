package com.kms.katalon.composer.execution.preferences;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.preferences.editor.MultiLineStringFieldEditor;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.execution.util.MailUtil.EmailConfig;
import com.kms.katalon.execution.util.MailUtil.MailSecurityProtocolType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class MailPreferencePage extends FieldEditorPreferencePage {
    private StringFieldEditor recipientFieldEditor, hostFieldEditor, portFieldEditor, userNameFieldEditor,
            passwordFieldEditor;
    private ComboFieldEditor protocolFieldEditor;
    private Composite subSpacer_1;

    public MailPreferencePage() {
        super();
        setPreferenceStore((IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite comp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);

        Group group = SWTFactory.createGroup(comp, StringConstants.PREF_GROUP_LBL_MAIL_SERVER, 2, 2,
                GridData.FILL_HORIZONTAL);

        hostFieldEditor = addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_HOST,
                StringConstants.PREF_LBL_HOST, 1, false);
        portFieldEditor = addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PORT,
                StringConstants.PREF_LBL_PORT, 1, false);
        userNameFieldEditor = addStringFieldEditor(group,
                PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME,
                StringConstants.PREF_LBL_USERNAME, 1, false);
        passwordFieldEditor = addStringFieldEditor(group,
                PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PASSWORD,
                StringConstants.PREF_LBL_PASSWORD, 1, false);

        Composite spacer = SWTFactory.createComposite(group, 2, 2, GridData.FILL_HORIZONTAL);

        protocolFieldEditor = new ComboFieldEditor(
                PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SECURITY_PROTOCOL,
                StringConstants.PREF_LBL_SECURITY_PROTOCOL, MailUtil.getMailSecurityProtocolTypeArrayValues(), spacer);
        protocolFieldEditor.fillIntoGrid(spacer, protocolFieldEditor.getNumberOfControls());
        addField(protocolFieldEditor);

        group = SWTFactory.createGroup(comp, StringConstants.PREF_GROUP_LBL_EXECUTION_MAIL, 2, 2, GridData.FILL_BOTH);

        recipientFieldEditor = addStringFieldEditor(group,
                PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS,
                StringConstants.PREF_LBL_REPORT_RECIPIENTS, 2, false);
        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SIGNATURE,
                StringConstants.PREF_LBL_SIGNATURE, 2, true);
        spacer = SWTFactory.createComposite(group, 2, 2, GridData.FILL_HORIZONTAL);
        
        Composite subSpacer = SWTFactory.createComposite(spacer, 2, 1, GridData.FILL_HORIZONTAL);

        FieldEditor fieldEditor = new BooleanFieldEditor(
                PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_ATTACHMENT,
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
                Job job = new Job(StringConstants.PREF_LBL_SEND_TEST_EMAIL) {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        try {
                            monitor.beginTask(StringConstants.PREF_SEND_TEST_EMAIL_JOB_NAME, 1);
                            MailUtil.sendTestMail(conf);
                            monitor.worked(1);
                            return Status.OK_STATUS;
                        } catch (Exception e1) {
                            LoggerSingleton.logError(e1);
                            MessageDialog.openError(
                                    getShell(),
                                    StringConstants.ERROR,
                                    MessageFormat.format(StringConstants.ERROR_SEND_TEST_EMAIL_FAIL, e1.getClass()
                                            .getName() + " " + e1.getMessage()));
                            return Status.CANCEL_STATUS;
                        } finally {
                            monitor.done();
                        }
                    }
                };
                job.setUser(true);
                job.schedule();
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
            fieldEditor = new StringFieldEditor(preferenceName, preferenceLabelText, spacer);
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
        conf.tos = mailRecipients;
        conf.host = hostFieldEditor.getStringValue();
        conf.port = portFieldEditor.getStringValue();
        conf.from = userNameFieldEditor.getStringValue();
        conf.securityProtocol = MailSecurityProtocolType.valueOf(protocolFieldEditor.getValue());
        conf.username = userNameFieldEditor.getStringValue();
        conf.password = passwordFieldEditor.getStringValue();
        conf.sendAttachment = false;
        return conf;
    }

    private static String[] getRecipients(String reportRecipients) {
        return StringUtils.split(reportRecipients.trim(), ";");
    }
}
