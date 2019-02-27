package com.kms.katalon.composer.integration.slack.handlers;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.text.MessageFormat;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.slack.constants.SlackPreferenceConstants;
import com.kms.katalon.composer.integration.slack.constants.StringConstants;
import com.kms.katalon.composer.integration.slack.util.SlackUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class SlackSendMsgHandler implements EventHandler {
    @Inject
    private IEventBroker eventBroker;

    private SlackUtil slackUtil;

    private ScopedPreferenceStore PREFERENCE;

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
        slackUtil = new SlackUtil();
        if (!slackUtil.isSlackEnabled()) {
            return;
        }
        try {
            Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            Object[] names;
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            switch (event.getTopic()) {
                case EventConstants.PROJECT_OPENED:
                    if (currentProject == null) {
                        return;
                    }
                    // Open project
                    if (OPEN_PROJECT)
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_OPEN_PROJECT,
                                slackUtil.fmtBold(currentProject.getName())));
                    break;

                case EventConstants.PROJECT_CLOSED:
                    if (currentProject == null) {
                        return;
                    }
                    // Close project
                    if (CLOSE_PROJECT)
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_CLOSE_PROJECT,
                                slackUtil.fmtBold(currentProject.getName())));
                    break;

                case EventConstants.TESTCASE_UPDATED:
                    // Update test case
                    if (UPDATE_TC)
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_UPDATE,
                                slackUtil.fmtBold(((TestCaseEntity) ((Object[]) object)[1]).getIdForDisplay())));
                    break;
                case EventConstants.TEST_SUITE_UPDATED:
                    // Update test suite
                    if (UPDATE_TS)
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_UPDATE,
                                slackUtil.fmtBold(((TestSuiteEntity) ((Object[]) object)[1]).getIdForDisplay())));
                    break;
                case EventConstants.TEST_DATA_UPDATED:
                    // Update test data (data file)
                    if (UPDATE_TD)
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_UPDATE,
                                slackUtil.fmtBold(((DataFileEntity) ((Object[]) object)[1]).getIdForDisplay())));
                    break;
                case EventConstants.TEST_OBJECT_UPDATED:
                    // Update test object (object repository)
                    if (UPDATE_TO)
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_UPDATE,
                                slackUtil.fmtBold(((WebElementEntity) ((Object[]) object)[1]).getIdForDisplay())));
                    break;

                case EventConstants.EXPLORER_RENAMED_SELECTED_ITEM:
                    // Rename
                    if (RENAME_ITEM) {
                        names = (Object[]) object;
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_RENAME,
                                slackUtil.fmtBold((String) names[0]), slackUtil.fmtBold((String) names[1])));
                    }
                    break;

                case EventConstants.EXPLORER_SET_SELECTED_ITEM:
                    // Create new entity
                    sendMsgForCreateNewEntity(object);
                    break;

                case EventConstants.EXPLORER_DELETED_SELECTED_ITEM:
                    // Delete entity
                    if (DELETE_ITEM) {
                        boolean isFolderPkg = StringUtils.endsWith((String) object, "/")
                                || (StringUtils.startsWith((String) object, FolderType.KEYWORD.toString()) && !StringUtils
                                        .endsWith((String) object, GroovyConstants.GROOVY_FILE_EXTENSION));
                        slackUtil.sendMessage(MessageFormat.format(
                                isFolderPkg ? StringConstants.EMOJI_MSG_DELETE_FOLDER
                                        : StringConstants.EMOJI_MSG_DELETE, slackUtil.fmtBold((String) object)));
                    }
                    break;

                case EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM:
                    // Paste entity
                    if (COPY_PASTE) {
                        names = (Object[]) object;
                        slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_COPY,
                                slackUtil.fmtBold((String) names[0]), slackUtil.fmtBold((String) names[1])));
                    }
                    break;

                case EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM:
                    // Paste entity
                    if (CUT_PASTE) {
                        names = (Object[]) object;
                        slackUtil.sendMessage(MessageFormat.format(
                                StringConstants.EMOJI_MSG_MOVE + slackUtil.fmtBold((String) names[0]),
                                slackUtil.fmtBold((String) names[1])));
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void sendMsgForCreateNewEntity(Object object) throws Exception {
        if (CREATE_TC && object != null && object instanceof TestCaseTreeEntity) {
            // Create new test case
            slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_NEW,
                    slackUtil.fmtBold(((TestCaseEntity) ((TestCaseTreeEntity) object).getObject()).getIdForDisplay())));
        } else if (CREATE_TS && object != null && object instanceof TestSuiteTreeEntity) {
            // Create new test suite
            slackUtil
                    .sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_NEW, slackUtil
                            .fmtBold(((TestSuiteEntity) ((TestSuiteTreeEntity) object).getObject()).getIdForDisplay())));
        } else if (CREATE_TD && object != null && object instanceof TestDataTreeEntity) {
            // Create new test data (data file)
            slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_NEW,
                    slackUtil.fmtBold(((DataFileEntity) ((TestDataTreeEntity) object).getObject()).getIdForDisplay())));
        } else if (CREATE_TO && object != null && object instanceof WebElementTreeEntity) {
            // Create new test object (object repository)
            slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_NEW, slackUtil
                    .fmtBold(((WebElementEntity) ((WebElementTreeEntity) object).getObject()).getIdForDisplay())));
        } else if (CREATE_KW && object != null && object instanceof KeywordTreeEntity) {
            // Create new keyword
            slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_NEW, slackUtil
                    .fmtBold(((IFile) ((ICompilationUnit) ((KeywordTreeEntity) object).getObject()).getResource())
                            .getProjectRelativePath().toString())));
        } else if (CREATE_FD && object != null && object instanceof FolderTreeEntity) {
            // Create new folder
            slackUtil.sendMessage(MessageFormat.format(
                    StringConstants.EMOJI_MSG_NEW,
                    slackUtil.fmtBold(((FolderEntity) ((FolderTreeEntity) object).getObject()).getRelativePathForUI()
                            .replace('\\', IPath.SEPARATOR) + IPath.SEPARATOR)));
        } else if (CREATE_PK && object != null && object instanceof PackageTreeEntity) {
            // Create new package
            String packageName = (String) ((PackageTreeEntity) object).getText();
            String packageParent = ((IPackageFragment) ((PackageTreeEntity) object).getObject()).getParent()
                    .getElementName() + IPath.SEPARATOR;
            slackUtil.sendMessage(MessageFormat.format(StringConstants.EMOJI_MSG_NEW,
                    slackUtil.fmtBold(packageParent + packageName)));
        }
    }

    /**
     * Get Slack Preferences
     */
    public void getSlackPreferences() {
        PREFERENCE = getPreferenceStore(SlackSendMsgHandler.class);
        OPEN_PROJECT = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_OPEN_PROJECT);
        CLOSE_PROJECT = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CLOSE_PROJECT);
        CREATE_TC = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_CASE);
        UPDATE_TC = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_CASE);
        CREATE_TS = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_SUITE);
        UPDATE_TS = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_SUITE);
        CREATE_TD = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_DATA);
        UPDATE_TD = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_DATA);
        CREATE_TO = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_TEST_OBJECT);
        UPDATE_TO = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_UPDATE_TEST_OBJECT);
        CREATE_KW = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_KEYWORD);
        CREATE_FD = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_FOLDER);
        CREATE_PK = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_CREATE_PACKAGE);
        COPY_PASTE = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_PASTE_FROM_COPY);
        CUT_PASTE = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_PASTE_FROM_CUT);
        RENAME_ITEM = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_RENAME_ITEM);
        DELETE_ITEM = PREFERENCE.getBoolean(SlackPreferenceConstants.SLACK_SEND_DELETE_ITEM);
    }

}
