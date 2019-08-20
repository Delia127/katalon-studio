package com.kms.katalon.composer.integration.slack.preferences;

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.slack.constants.ComposerIntegrationSlackMessageConstants;
import com.kms.katalon.composer.integration.slack.constants.ImageConstants;
import com.kms.katalon.composer.integration.slack.constants.SlackPreferenceConstants;
import com.kms.katalon.composer.integration.slack.constants.StringConstants;
import com.kms.katalon.composer.integration.slack.util.SlackUtil;
import com.kms.katalon.composer.integration.slack.util.SlackUtil.SlackMsgStatus;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.tracking.service.Trackings;


public class SlackPreferencePage extends FieldEditorPreferencePageWithHelp {
    private Composite fieldEditorParent;

    private BooleanFieldEditor enabled;

    private Group fieldsetSlack;

    private Group fieldsetSend;

    /** Slack User Token @see <a href="https://api.slack.com/web">Generate Slack User Token</a> */
    private StringFieldEditor token;

    private StringFieldEditor channel;

    private StringFieldEditor username;

    private BooleanFieldEditor asUser;

    private GifCLabel lblConnectionStatus;

    private Button btnTestConnection;

    private BooleanFieldEditor sendOpenProject;

    private BooleanFieldEditor sendCloseProject;

    private BooleanFieldEditor sendCreateTestCase;

    private BooleanFieldEditor sendUpdateTestCase;

    private BooleanFieldEditor sendCreateTestSuite;

    private BooleanFieldEditor sendUpdateTestSuite;

    private BooleanFieldEditor sendCreateTestData;

    private BooleanFieldEditor sendUpdateTestData;

    private BooleanFieldEditor sendCreateTestObject;

    private BooleanFieldEditor sendUpdateTestObject;

    private BooleanFieldEditor sendCreateFolder;

    private BooleanFieldEditor sendCreateKeyword;

    private BooleanFieldEditor sendCreatePackage;

    private BooleanFieldEditor sendPasteFromCopy;

    private BooleanFieldEditor sendPasteFromCut;

    private BooleanFieldEditor sendRenameItem;

    private BooleanFieldEditor sendDeleteItem;
    
    Button btnGetSlackPlugin;

    private boolean isValid = isValid();

    private boolean loaded = false;

    private boolean isSlackEnabled;

    private boolean asUserValue;

    private String tokenValue = "";

    private String channelValue = "";

    private String usernameValue = "";

    public SlackPreferencePage() {
        super(GRID);
        setMessage(StringConstants.PREF_LBL_TEAM_COLLABORATION);
    }

    @Override
    protected void createFieldEditors() {

        fieldEditorParent = new Composite(getFieldEditorParent(), SWT.NONE);
        fieldEditorParent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
        createSlackPlugin(fieldEditorParent);
        enabled = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_ENABLED,
                StringConstants.PREF_LBL_SLACK_ENABLED, fieldEditorParent);
        enabled.getDescriptionControl(fieldEditorParent).setToolTipText(StringConstants.PREF_LBL_TIP_SLACK_ENABLED);

        fieldsetSlack = new Group(fieldEditorParent, GRID);
        fieldsetSlack.setText(StringConstants.PREF_LBL_SLACK);
        fieldsetSlack.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

        token = new StringFieldEditor(SlackPreferenceConstants.SLACK_AUTH_TOKEN,
                StringConstants.PREF_LBL_SLACK_AUTH_TOKEN, fieldsetSlack);
        token.getTextControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_AUTH_TOKEN);

        channel = new StringFieldEditor(SlackPreferenceConstants.SLACK_CHANNEL_GROUP,
                StringConstants.PREF_LBL_SLACK_CHANNEL, fieldsetSlack);
        channel.getTextControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_CHANNEL_DESC);

        asUser = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_AS_USER, StringConstants.PREF_LBL_SLACK_AS_USER,
                fieldsetSlack);
        asUser.getDescriptionControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_AS_USER_DESC);
        asUser.fillIntoGrid(fieldsetSlack, 2);

        username = new StringFieldEditor(SlackPreferenceConstants.SLACK_USERNAME,
                StringConstants.PREF_LBL_SLACK_USERNAME, fieldsetSlack);
        username.getTextControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_USERNAME_DESC);

        btnTestConnection = new Button(fieldsetSlack, GRID);
        btnTestConnection.setText(StringConstants.PREF_LBL_TEST_CONNECTION);
        btnTestConnection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                testSlackConnection();
            }
        });
        btnGetSlackPlugin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch("https://store.katalon.com/product/4/Slack-Integration");
                Trackings.trackQuickDiscussion();
            }
        });

        lblConnectionStatus = new GifCLabel(fieldsetSlack, GRID);
        lblConnectionStatus.setText(StringConstants.EMPTY);
        lblConnectionStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

        addField(enabled);
        addField(token);
        addField(channel);
        addField(asUser);
        addField(username);

        fieldsetSend = new Group(fieldEditorParent, GRID);
        fieldsetSend.setText(StringConstants.PREF_LBL_SEND_MSG_TO_SLACK_WHEN);
        fieldsetSend.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

        sendOpenProject = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_OPEN_PROJECT,
                StringConstants.PREF_SEND_OPEN_PROJECT, fieldsetSend);

        sendCloseProject = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CLOSE_PROJECT,
                StringConstants.PREF_SEND_CLOSE_PROJECT, fieldsetSend);

        sendCreateTestCase = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_CASE,
                StringConstants.PREF_SEND_CREATE_TEST_CASE, fieldsetSend);

        sendUpdateTestCase = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_CASE,
                StringConstants.PREF_SEND_UPDATE_TEST_CASE, fieldsetSend);

        sendCreateTestSuite = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_SUITE,
                StringConstants.PREF_SEND_CREATE_TEST_SUITE, fieldsetSend);

        sendUpdateTestSuite = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_SUITE,
                StringConstants.PREF_SEND_UPDATE_TEST_SUITE, fieldsetSend);

        sendCreateTestData = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_DATA,
                StringConstants.PREF_SEND_CREATE_TEST_DATA, fieldsetSend);

        sendUpdateTestData = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_DATA,
                StringConstants.PREF_SEND_UPDATE_TEST_DATA, fieldsetSend);

        sendCreateTestObject = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_OBJECT,
                StringConstants.PREF_SEND_CREATE_TEST_OBJECT, fieldsetSend);

        sendUpdateTestObject = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_OBJECT,
                StringConstants.PREF_SEND_UPDATE_TEST_OBJECT, fieldsetSend);

        sendCreateFolder = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_FOLDER,
                StringConstants.PREF_SEND_CREATE_FOLDER, fieldsetSend);

        sendCreateKeyword = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_KEYWORD,
                StringConstants.PREF_SEND_CREATE_KEYWORD, fieldsetSend);

        sendCreatePackage = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_CREATE_PACKAGE,
                StringConstants.PREF_SEND_CREATE_PACKAGE, fieldsetSend);

        sendPasteFromCopy = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_PASTE_FROM_COPY,
                StringConstants.PREF_SEND_PASTE_FROM_COPY, fieldsetSend);

        sendPasteFromCut = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_PASTE_FROM_CUT,
                StringConstants.PREF_SEND_PASTE_FROM_CUT, fieldsetSend);

        sendRenameItem = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_RENAME_ITEM,
                StringConstants.PREF_SEND_RENAME_ITEM, fieldsetSend);

        sendDeleteItem = new BooleanFieldEditor(SlackPreferenceConstants.SLACK_SEND_DELETE_ITEM,
                StringConstants.PREF_SEND_DELETE_ITEM, fieldsetSend);

        addField(sendOpenProject);
        addField(sendCloseProject);
        addField(sendCreateTestCase);
        addField(sendUpdateTestCase);
        addField(sendCreateTestSuite);
        addField(sendUpdateTestSuite);
        addField(sendCreateTestData);
        addField(sendUpdateTestData);
        addField(sendCreateTestObject);
        addField(sendUpdateTestObject);
        addField(sendCreateFolder);
        addField(sendCreateKeyword);
        addField(sendCreatePackage);
        addField(sendPasteFromCopy);
        addField(sendPasteFromCut);
        addField(sendRenameItem);
        addField(sendDeleteItem);
        loaded = true;
    }
    private GridLayout noneMarginGridLayout() {
        GridLayout gl = new GridLayout();
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        return gl;
    }
    private void createSlackPlugin(Composite parent){
        Composite deprecatedComposite = new Composite(parent, SWT.NONE);
        deprecatedComposite.setLayout(noneMarginGridLayout());
        deprecatedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        deprecatedComposite.setLayout(new GridLayout());
        Group grpSlackPlugin = new Group(deprecatedComposite, SWT.NONE);
        grpSlackPlugin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpSlackPlugin  = new GridLayout(1, false);
        glGrpSlackPlugin.horizontalSpacing = 10;
        glGrpSlackPlugin.marginWidth = 10;
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        layoutData.widthHint = 300;
        
        grpSlackPlugin.setLayout(glGrpSlackPlugin);
        grpSlackPlugin.setText("");
        Label deprecatedMessage = new Label(grpSlackPlugin, SWT.WRAP); 
        deprecatedMessage.setLayoutData(layoutData);
        deprecatedMessage.setText(ComposerIntegrationSlackMessageConstants.SlackSettingsComposite_MSG_DEPRECATED);
        deprecatedMessage.setBackground(ColorUtil.getWarningLogBackgroundColor());
        new Label(grpSlackPlugin, SWT.NONE);
        btnGetSlackPlugin = new Button(grpSlackPlugin, SWT.NONE);
        btnGetSlackPlugin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
        btnGetSlackPlugin.setText(ComposerIntegrationSlackMessageConstants.PREF_LBL_GETSLACKPLUGIN);
    }
    @Override
    protected void initialize() {
        super.initialize();
        isSlackEnabled = enabled.getBooleanValue();
        asUserValue = asUser.getBooleanValue();
        tokenValue = token.getStringValue().trim();
        channelValue = channel.getStringValue().trim();
        usernameValue = username.getStringValue().trim();
        enableFields(isSlackEnabled);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        lblConnectionStatus.setText(StringConstants.EMPTY);
        if (event.getSource() == enabled) {
            isSlackEnabled = (boolean) event.getNewValue();
            enableFields(isSlackEnabled);
        } else if (event.getSource() == asUser) {
            asUserValue = (boolean) event.getNewValue();
            username.setEnabled(!asUserValue, fieldsetSlack);
        } else if (event.getSource() == token) {
            tokenValue = ((String) event.getNewValue()).trim();
        } else if (event.getSource() == channel) {
            channelValue = ((String) event.getNewValue()).trim();
        } else if (event.getSource() == username) {
            usernameValue = ((String) event.getNewValue()).trim();
        }
    }

    private void validate() {
        // Skip checking if having any invalid state
        if (!isValid) {
            if (isSlackEnabled && (StringUtils.isBlank(tokenValue))) {
                MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_AUTH_TOKEN
                        + StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
            } else if (isSlackEnabled && StringUtils.isBlank(channelValue)) {
                MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_CHANNEL
                        + StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
            }
            return;
        }

        // Otherwise, do the checking
        if (isSlackEnabled && (StringUtils.isBlank(tokenValue))) {
            MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_AUTH_TOKEN
                    + StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
            isValid = false;
        } else if (isSlackEnabled && StringUtils.isBlank(channelValue)) {
            MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_CHANNEL
                    + StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
            isValid = false;
        } else {
            isValid = true;
        }
        return;
    }

    private void enableFields(boolean isEnabled) {
        token.setEnabled(isEnabled, fieldsetSlack);
        token.getLabelControl(fieldsetSlack).setEnabled(true);
        channel.setEnabled(isEnabled, fieldsetSlack);
        channel.getLabelControl(fieldsetSlack).setEnabled(true);
        asUser.setEnabled(isEnabled, fieldsetSlack);
        username.setEnabled(isEnabled && !asUserValue, fieldsetSlack);
        username.getLabelControl(fieldsetSlack).setEnabled(true);
        sendOpenProject.setEnabled(isEnabled, fieldsetSend);
        sendCloseProject.setEnabled(isEnabled, fieldsetSend);
        sendCreateTestCase.setEnabled(isEnabled, fieldsetSend);
        sendUpdateTestCase.setEnabled(isEnabled, fieldsetSend);
        sendCreateTestSuite.setEnabled(isEnabled, fieldsetSend);
        sendUpdateTestSuite.setEnabled(isEnabled, fieldsetSend);
        sendCreateTestData.setEnabled(isEnabled, fieldsetSend);
        sendUpdateTestData.setEnabled(isEnabled, fieldsetSend);
        sendCreateTestObject.setEnabled(isEnabled, fieldsetSend);
        sendUpdateTestObject.setEnabled(isEnabled, fieldsetSend);
        sendCreateFolder.setEnabled(isEnabled, fieldsetSend);
        sendCreateKeyword.setEnabled(isEnabled, fieldsetSend);
        sendCreatePackage.setEnabled(isEnabled, fieldsetSend);
        sendPasteFromCopy.setEnabled(isEnabled, fieldsetSend);
        sendPasteFromCut.setEnabled(isEnabled, fieldsetSend);
        sendRenameItem.setEnabled(isEnabled, fieldsetSend);
        sendDeleteItem.setEnabled(isEnabled, fieldsetSend);
        btnTestConnection.setEnabled(isEnabled);
        lblConnectionStatus.setText(StringConstants.EMPTY);
        if (!isValid) {
            isValid = true;
        }
    }

    @Override
    public boolean okToLeave() {
        boolean ignoreChanges = false;
        if (isSlackEnabled && (StringUtils.isBlank(tokenValue) || StringUtils.isBlank(channelValue))) {
            ignoreChanges = MessageDialog.openQuestion(getShell(), getTitle(),
                    StringConstants.PREF_QUESTION_MSG_DO_YOU_WANT_TO_DISABLE_SLACK);
        }

        if (ignoreChanges) {
            // Disable Slack setting if fields are empty
            enabled.loadDefault();
            isSlackEnabled = enabled.getBooleanValue();
            enableFields(isSlackEnabled);
        }

        // Then free to leave
        return super.okToLeave();
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        isSlackEnabled = enabled.getBooleanValue();
        enableFields(isSlackEnabled);
    }

    @Override
    public boolean performOk() {
        if (loaded) {
            validate();
            if (isValid) {
                trimFields();
                return super.performOk();
            }
            return isValid;
        }
        return super.performOk();
    }

    @Override
    protected void performApply() {
        if (loaded) {
            validate();
            if (isValid) {
                trimFields();
                super.performApply();
            }
        }
    }

    private void trimFields() {
        token.setStringValue(token.getStringValue().trim());
        channel.setStringValue(channel.getStringValue().trim());
        username.setStringValue(username.getStringValue().trim());
    }

    private void testSlackConnection() {
        try {
            lblConnectionStatus.setText(StringConstants.EMPTY);
            // Show loading image
            lblConnectionStatus.setGifImage(ImageConstants.URL_16_LOADING.openStream());
            lblConnectionStatus.update();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        Job job = new Job("Test Slack connection") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    URI uri = SlackUtil.getInstance().buildSlackUri(tokenValue, channelValue, usernameValue,
                            asUserValue, StringConstants.PREF_MSG_TEST_CONNECTION);
                    Map<String, Object> response = SlackUtil.getInstance().getResponseFromSendingMsg(uri);
                    boolean isOk = (boolean) response.get(SlackUtil.RES_IS_OK);
                    String errorMsg = (String) response.get(SlackUtil.RES_ERROR_MSG);

                    String statusMessage = StringConstants.EMPTY;
                    if (isOk && errorMsg == null) {
                        statusMessage = StringConstants.PREF_SUCCESS_MSG_STATUS;
                    } else if (!isOk && errorMsg != null) {
                        statusMessage = SlackMsgStatus.getInstance().getMsgDescription(errorMsg);
                    } else {
                        statusMessage = StringConstants.PREF_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION;
                    }
                    return new Status(Status.OK, "com.kms.katalon.composer.integration.slack", statusMessage);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    return new Status(Status.ERROR, "com.kms.katalon.composer.integration.slack", e.getMessage());
                } finally {
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                final String message = event.getResult().getMessage();
                UISynchronizeService.getInstance().getSync().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        lblConnectionStatus.setText(message);
                        // Hide loading image
                        lblConnectionStatus.setImage(null);
                    }
                });
            }
        });
        job.schedule();
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_SLACK;
    }
}
