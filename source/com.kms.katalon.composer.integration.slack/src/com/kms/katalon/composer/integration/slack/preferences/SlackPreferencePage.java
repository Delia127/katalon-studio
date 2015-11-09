package com.kms.katalon.composer.integration.slack.preferences;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.google.gson.stream.JsonReader;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.slack.constants.StringConstants;
import com.kms.katalon.composer.integration.slack.util.SlackUtil.SlackMsgStatus;
import com.kms.katalon.constants.PreferenceConstants;

public class SlackPreferencePage extends FieldEditorPreferencePage {
	private Composite fieldEditorParent;
	private BooleanFieldEditor enabled;
	private Group fieldsetSlack;
	private Group fieldsetSend;
	/** Slack User Token @see <a href="https://api.slack.com/web">Generate Slack User Token</a> */
	private StringFieldEditor token;
	private StringFieldEditor channel;
	private StringFieldEditor username;
	private BooleanFieldEditor asUser;
	private Label lblConnectionStatus;
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
	private boolean isValid = isValid();
	private boolean loaded = false;

	public SlackPreferencePage() {
		super(GRID);
		setMessage(StringConstants.PREF_LBL_TEAM_COLLABORATION);
	}

	@Override
	protected void createFieldEditors() {
		fieldEditorParent = new Composite(getFieldEditorParent(), SWT.NONE);
		fieldEditorParent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

		enabled = new BooleanFieldEditor(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_ENABLED,
				StringConstants.PREF_LBL_SLACK_ENABLED, fieldEditorParent);
		enabled.getDescriptionControl(fieldEditorParent).setToolTipText(StringConstants.PREF_LBL_TIP_SLACK_ENABLED);

		fieldsetSlack = new Group(fieldEditorParent, GRID);
		fieldsetSlack.setText(StringConstants.PREF_LBL_SLACK);
		fieldsetSlack.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

		token = new StringFieldEditor(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_AUTH_TOKEN,
				StringConstants.PREF_LBL_SLACK_AUTH_TOKEN, fieldsetSlack);
		token.getTextControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_AUTH_TOKEN);

		channel = new StringFieldEditor(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_CHANNEL_GROUP,
				StringConstants.PREF_LBL_SLACK_CHANNEL, fieldsetSlack);
		channel.getTextControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_CHANNEL_DESC);

		asUser = new BooleanFieldEditor(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_AS_USER,
				StringConstants.PREF_LBL_SLACK_AS_USER, fieldsetSlack);
		asUser.getDescriptionControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_AS_USER_DESC);

		username = new StringFieldEditor(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_USERNAME,
				StringConstants.PREF_LBL_SLACK_USERNAME, fieldsetSlack);
		username.getTextControl(fieldsetSlack).setToolTipText(StringConstants.PREF_LBL_SLACK_USERNAME_DESC);

		btnTestConnection = new Button(fieldsetSlack, GRID);
		btnTestConnection.setText(StringConstants.PREF_LBL_TEST_CONNECTION);
		btnTestConnection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				readResponse();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Do nothing
			}
		});

		lblConnectionStatus = new Label(fieldsetSlack, GRID);
		lblConnectionStatus.setText("");
		lblConnectionStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

		addField(enabled);
		addField(token);
		addField(channel);
		addField(asUser);
		addField(username);

		fieldsetSend = new Group(fieldEditorParent, GRID);
		fieldsetSend.setText(StringConstants.PREF_LBL_SEND_MSG_TO_SLACK_WHEN);
		fieldsetSend.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

		sendOpenProject = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_OPEN_PROJECT,
				StringConstants.PREF_SEND_OPEN_PROJECT, fieldsetSend);

		sendCloseProject = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CLOSE_PROJECT,
				StringConstants.PREF_SEND_CLOSE_PROJECT, fieldsetSend);

		sendCreateTestCase = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_CASE,
				StringConstants.PREF_SEND_CREATE_TEST_CASE, fieldsetSend);

		sendUpdateTestCase = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_CASE,
				StringConstants.PREF_SEND_UPDATE_TEST_CASE, fieldsetSend);

		sendCreateTestSuite = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_SUITE,
				StringConstants.PREF_SEND_CREATE_TEST_SUITE, fieldsetSend);

		sendUpdateTestSuite = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_SUITE,
				StringConstants.PREF_SEND_UPDATE_TEST_SUITE, fieldsetSend);

		sendCreateTestData = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_DATA,
				StringConstants.PREF_SEND_CREATE_TEST_DATA, fieldsetSend);

		sendUpdateTestData = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_DATA,
				StringConstants.PREF_SEND_UPDATE_TEST_DATA, fieldsetSend);

		sendCreateTestObject = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_OBJECT,
				StringConstants.PREF_SEND_CREATE_TEST_OBJECT, fieldsetSend);

		sendUpdateTestObject = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_OBJECT,
				StringConstants.PREF_SEND_UPDATE_TEST_OBJECT, fieldsetSend);

        sendCreateFolder = new BooleanFieldEditor(
                PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_FOLDER,
                StringConstants.PREF_SEND_CREATE_FOLDER, fieldsetSend);

		sendCreateKeyword = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_KEYWORD,
				StringConstants.PREF_SEND_CREATE_KEYWORD, fieldsetSend);

		sendCreatePackage = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_PACKAGE,
				StringConstants.PREF_SEND_CREATE_PACKAGE, fieldsetSend);

		sendPasteFromCopy = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_PASTE_FROM_COPY,
				StringConstants.PREF_SEND_PASTE_FROM_COPY, fieldsetSend);

		sendPasteFromCut = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_PASTE_FROM_CUT,
				StringConstants.PREF_SEND_PASTE_FROM_CUT, fieldsetSend);

		sendRenameItem = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_RENAME_ITEM,
				StringConstants.PREF_SEND_RENAME_ITEM, fieldsetSend);

		sendDeleteItem = new BooleanFieldEditor(
				PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_DELETE_ITEM,
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

	@Override
	protected void initialize() {
		super.initialize();
		enableFields(enabled.getBooleanValue());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		lblConnectionStatus.setText("");
		if (event.getSource() == enabled) {
			enableFields((boolean) event.getNewValue());
		} else if (event.getSource() == asUser) {
			username.setEnabled(!(boolean) event.getNewValue(), fieldsetSlack);
		}
	}

	private void validate() {
		// Skip checking if having any invalid state
		if (!isValid) {
			if (enabled.getBooleanValue() && (StringUtils.isBlank(token.getStringValue()))) {
				MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_AUTH_TOKEN
						+ StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
			} else if (enabled.getBooleanValue() && StringUtils.isBlank(channel.getStringValue())) {
				MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_CHANNEL
						+ StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
			}
			return;
		}

		// Otherwise, do the checking
		if (enabled.getBooleanValue() && (StringUtils.isBlank(token.getStringValue()))) {
			MessageDialog.openWarning(getShell(), getTitle(), StringConstants.PREF_LBL_SLACK_AUTH_TOKEN
					+ StringConstants.PREF_ERROR_MSG_X_CANNOT_BE_EMPTY_OR_BLANK);
			isValid = false;
		} else if (enabled.getBooleanValue() && StringUtils.isBlank(channel.getStringValue())) {
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
		channel.setEnabled(isEnabled, fieldsetSlack);
		asUser.setEnabled(isEnabled, fieldsetSlack);
		username.setEnabled(isEnabled && !asUser.getBooleanValue(), fieldsetSlack);
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
		lblConnectionStatus.setText("");
		if (!isValid) {
			isValid = true;
		}
	}

	private String getSlackApiUrl(String msg) throws Exception {
		String charset = "UTF-8";
		String url = "https://slack.com/api/chat.postMessage?";
		url += "token=" + URLEncoder.encode(token.getStringValue().trim(), charset);
		url += "&channel=" + URLEncoder.encode(channel.getStringValue().trim(), charset);
		if (!username.getStringValue().trim().isEmpty()) {
			url += "&username=" + URLEncoder.encode(username.getStringValue().trim(), charset);
		}
		if (asUser.getBooleanValue()) {
			url += "&as_user=true";
		}
		url += "&text=" + URLEncoder.encode(msg, charset);
		return url;
	}

	@Override
	public boolean okToLeave() {
		boolean ignoreChanges = false;
		if (enabled.getBooleanValue()
				&& (StringUtils.isBlank(token.getStringValue()) || StringUtils.isBlank(channel.getStringValue()))) {
			ignoreChanges = MessageDialog.openQuestion(getShell(), getTitle(),
					StringConstants.PREF_QUESTION_MSG_DO_YOU_WANT_TO_DISABLE_SLACK);
		}

		if (ignoreChanges) {
			// Disable Slack setting if fields are empty
			enabled.loadDefault();
			enableFields(enabled.getBooleanValue());
		}

		// Then free to leave
		return super.okToLeave();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		enableFields(enabled.getBooleanValue());
	}

	@Override
	public boolean performOk() {
		if (loaded) {
			validate();
			if (isValid) {
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
				super.performApply();
			}
		}
	}

	@SuppressWarnings("restriction")
	private void readResponse() {
		try {
			boolean connectSuccessfully = false;
			String errorMsg = null;

			URL api = new URL(getSlackApiUrl(StringConstants.PREF_MSG_TEST_CONNECTION));
			HttpURLConnection con = (HttpURLConnection) api.openConnection();
			con.setRequestMethod("GET");
			// con.setRequestProperty("User-Agent", "Katalon Studio");
			InputStreamReader in = new InputStreamReader(con.getInputStream());

			JsonReader reader = new JsonReader(in);
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (StringUtils.equals(name, "ok")) {
					connectSuccessfully = reader.nextBoolean();
				} else if (StringUtils.equals(name, "error")) {
					errorMsg = reader.nextString();
				} else {
					reader.skipValue(); // avoid some unhandled events
				}
			}
			reader.endObject();
			reader.close();

			if (connectSuccessfully && errorMsg == null) {
				lblConnectionStatus.setText(StringConstants.PREF_SUCCESS_MSG_STATUS);
			} else if (!connectSuccessfully && errorMsg != null) {
				lblConnectionStatus.setText(SlackMsgStatus.getMsgDescription(errorMsg));
			} else {
				lblConnectionStatus.setText(StringConstants.PREF_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION);
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}

}
