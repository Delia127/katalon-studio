package com.kms.katalon.composer.integration.slack.handlers;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.integration.slack.constants.StringConstants;
import com.kms.katalon.composer.integration.slack.util.SlackUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

@SuppressWarnings("restriction")
public class SlackSendMsgHandler implements EventHandler {
	@Inject
	private IEventBroker eventBroker;

	@Inject
	private Logger logger;

	private SlackUtil slackUtil;

	private IPreferenceStore PREFERENCE;
	private boolean OPEN_PROJECT;
	private boolean CLOSE_PROJECT;
	private boolean CREATE_TC;
	private boolean UPDATE_TC;
	private boolean CREATE_TS;
	private boolean UPDATE_TS;
	private boolean CREATE_TD;
	private boolean UPDATE_TD;
	private boolean CREATE_TO;
	private boolean UPDATE_TO;
	private boolean CREATE_KW;
	private boolean CREATE_FD;
	private boolean CREATE_PK;
	private boolean COPY_PASTE;
	private boolean CUT_PASTE;
	private boolean RENAME_ITEM;
	private boolean DELETE_ITEM;

	/**
	 * Subscribe Event Broker Listeners for
	 * <ul>
	 * <li>Open/Close project</li>
	 * <li>Create new entity</li>
	 * <li>Update entity</li>
	 * <li>Rename entity</li>
	 * <li>Delete entity</li>
	 * <li>Paste entity from copy/cut</li>
	 * </ul>
	 */
	@Inject
	public void subscribeEventBrokerListeners() {
		// Project Open/Close
		eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
		eventBroker.subscribe(EventConstants.PROJECT_CLOSED, this);

		// Create
		eventBroker.subscribe(EventConstants.EXPLORER_SET_SELECTED_ITEM, this);

		// Delete
		eventBroker.subscribe(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, this);

		// Update
		eventBroker.subscribe(EventConstants.TESTCASE_UPDATED, this);
		eventBroker.subscribe(EventConstants.TEST_SUITE_UPDATED, this);
		eventBroker.subscribe(EventConstants.TEST_DATA_UPDATED, this);
		eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);

		// Rename
		eventBroker.subscribe(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, this);

		// Paste from copy (create new entity)
		eventBroker.subscribe(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM, this);

		// Paste from cut (move entity)
		eventBroker.subscribe(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM, this);
	}

	/**
	 * Handle subscribed events to send message to Slack for Team Collaboration
	 * 
	 * @param event Event
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		getSlackPreferences();
		try {
			Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			Object[] names;
			slackUtil = new SlackUtil();
			switch (event.getTopic()) {
				case EventConstants.PROJECT_OPENED:
					// Open project
					if (OPEN_PROJECT)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_OPEN_PROJECT
								+ slackUtil.fmtBold(ProjectController.getInstance().getCurrentProject().getName()));
					break;

				case EventConstants.PROJECT_CLOSED:
					// Close project
					if (CLOSE_PROJECT)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_CLOSE_PROJECT
								+ slackUtil.fmtBold(ProjectController.getInstance().getCurrentProject().getName()));
					break;

				case EventConstants.TESTCASE_UPDATED:
					// Update test case
					if (UPDATE_TC)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_UPDATE
								+ slackUtil.fmtBold(TestCaseController.getInstance().getIdForDisplay(
										(TestCaseEntity) ((Object[]) object)[1])));
					break;
				case EventConstants.TEST_SUITE_UPDATED:
					// Update test suite
					if (UPDATE_TS)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_UPDATE
								+ slackUtil.fmtBold(TestSuiteController.getInstance().getIdForDisplay(
										(TestSuiteEntity) ((Object[]) object)[1])));
					break;
				case EventConstants.TEST_DATA_UPDATED:
					// Update test data (data file)
					if (UPDATE_TD)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_UPDATE
								+ slackUtil.fmtBold(TestDataController.getInstance().getIdForDisplay(
										(DataFileEntity) ((Object[]) object)[1])));
					break;
				case EventConstants.TEST_OBJECT_UPDATED:
					// Update test object (object repository)
					if (UPDATE_TO)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_UPDATE
								+ slackUtil.fmtBold(ObjectRepositoryController.getInstance().getIdForDisplay(
										(WebElementEntity) ((Object[]) object)[1])));
					break;

				case EventConstants.EXPLORER_RENAMED_SELECTED_ITEM:
					// Rename
					if (RENAME_ITEM) {
						names = (Object[]) object;
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_RENAME + slackUtil.fmtBold((String) names[0])
								+ " to " + slackUtil.fmtBold((String) names[1]));
					}
					break;

				case EventConstants.EXPLORER_SET_SELECTED_ITEM:
					// Create new entity
					sendMsgForCreateNewEntity(object);
					break;

				case EventConstants.EXPLORER_DELETED_SELECTED_ITEM:
					// Delete entity
					if (DELETE_ITEM)
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_DELETE + slackUtil.fmtBold((String) object));
					break;

				case EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM:
					// Paste entity
					if (COPY_PASTE) {
						names = (Object[]) object;
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_COPY + slackUtil.fmtBold((String) names[0])
								+ " to " + slackUtil.fmtBold((String) names[1]));
					}
					break;

				case EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM:
					// Paste entity
					if (CUT_PASTE) {
						names = (Object[]) object;
						slackUtil.sendMessage(StringConstants.EMOJI_MSG_MOVE + slackUtil.fmtBold((String) names[0])
								+ " to " + slackUtil.fmtBold((String) names[1]));
					}
					break;

				default:
					break;
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void sendMsgForCreateNewEntity(Object object) throws Exception {
		slackUtil = new SlackUtil();
		if (CREATE_TC && object != null && object instanceof TestCaseTreeEntity) {
			// Create new test case
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW
					+ slackUtil.fmtBold(TestCaseController.getInstance().getIdForDisplay(
							(TestCaseEntity) ((TestCaseTreeEntity) object).getObject())));
		} else if (CREATE_TS && object != null && object instanceof TestSuiteTreeEntity) {
			// Create new test suite
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW
					+ slackUtil.fmtBold(TestSuiteController.getInstance().getIdForDisplay(
							(TestSuiteEntity) ((TestSuiteTreeEntity) object).getObject())));
		} else if (CREATE_TD && object != null && object instanceof TestDataTreeEntity) {
			// Create new test data (data file)
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW
					+ slackUtil.fmtBold(TestDataController.getInstance().getIdForDisplay(
							(DataFileEntity) ((TestDataTreeEntity) object).getObject())));
		} else if (CREATE_TO && object != null && object instanceof WebElementTreeEntity) {
			// Create new test object (object repository)
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW
					+ slackUtil.fmtBold(ObjectRepositoryController.getInstance().getIdForDisplay(
							(WebElementEntity) ((WebElementTreeEntity) object).getObject())));
		} else if (CREATE_KW && object != null && object instanceof KeywordTreeEntity) {
			// Create new keyword
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW
					+ slackUtil.fmtBold(((IFile) ((ICompilationUnit) ((KeywordTreeEntity) object).getObject())
							.getResource()).getProjectRelativePath().toString()));
		} else if (CREATE_FD && object != null && object instanceof FolderTreeEntity) {
			// Create new folder
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW
					+ slackUtil.fmtBold(((FolderEntity) ((FolderTreeEntity) object).getObject()).getRelativePathForUI()
							.replace('\\', IPath.SEPARATOR) + IPath.SEPARATOR));
		} else if (CREATE_PK && object != null && object instanceof PackageTreeEntity) {
			// Create new package
			String packageName = (String) ((PackageTreeEntity) object).getText();
			String packageParent = ((IPackageFragment) ((PackageTreeEntity) object).getObject()).getParent()
					.getElementName() + IPath.SEPARATOR;
			slackUtil.sendMessage(StringConstants.EMOJI_MSG_NEW + slackUtil.fmtBold(packageParent + packageName));
		}
	}

	/**
	 * Get Slack Preferences
	 */
	public void getSlackPreferences() {
		PREFERENCE = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.IntegrationSlackPreferenceConstants.QUALIFIER);
		OPEN_PROJECT = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_OPEN_PROJECT);
		CLOSE_PROJECT = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CLOSE_PROJECT);
		CREATE_TC = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_CASE);
		UPDATE_TC = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_CASE);
		CREATE_TS = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_SUITE);
		UPDATE_TS = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_SUITE);
		CREATE_TD = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_DATA);
		UPDATE_TD = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_DATA);
		CREATE_TO = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_TEST_OBJECT);
		UPDATE_TO = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_OBJECT);
		CREATE_KW = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_KEYWORD);
		CREATE_FD = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_FOLDER);
		CREATE_PK = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_CREATE_PACKAGE);
		COPY_PASTE = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_PASTE_FROM_COPY);
		CUT_PASTE = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_PASTE_FROM_CUT);
		RENAME_ITEM = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_RENAME_ITEM);
		DELETE_ITEM = PREFERENCE
				.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_SEND_DELETE_ITEM);
	}

}
