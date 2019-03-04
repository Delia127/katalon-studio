package com.kms.katalon.composer.integration.jira.toolbar.handler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.integration.jira.toolbar.dialog.ImportJiraJQLDialog;
import com.kms.katalon.composer.integration.jira.toolbar.dialog.IssueSelectionDialog;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.integration.jira.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.entity.ImprovedIssue;
import com.kms.katalon.integration.jira.entity.JiraFilter;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class ImportJiraJQLHandler implements JiraUIComponent {

    private static final String JIRA_TOOL_ITEM = "com.kms.katalon.composer.integration.jira.handledtoolitem.jira";

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    @PostConstruct
    public void registerPlatformEvent() {
        eventBroker.subscribe(EventConstants.JIRA_PLUGIN_INSTALLED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                MUIElement groupElement = modelService.find(IdConstants.MAIN_TOOLBAR_ID, application);

                if (!(groupElement instanceof MElementContainer)) {
                    return;
                }

                MElementContainer<?> container = (MElementContainer<?>) groupElement;

                if (!(container instanceof MToolBar)) {
                    return;
                }

                MUIElement jiraToolItemElement = modelService
                        .find(JIRA_TOOL_ITEM, container);
                if (jiraToolItemElement == null) {
                    return;
                }
                MToolItem jiraToolItem = (MToolItem) jiraToolItemElement;
                jiraToolItem.setToBeRendered(false);
                jiraToolItem.setVisible(false);
            }
        });
        
        eventBroker.subscribe(EventConstants.JIRA_PLUGIN_UNINSTALLED, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                MUIElement groupElement = modelService.find(IdConstants.MAIN_TOOLBAR_ID, application);

                if (!(groupElement instanceof MElementContainer)) {
                    return;
                }

                MElementContainer<?> container = (MElementContainer<?>) groupElement;

                if (!(container instanceof MToolBar)) {
                    return;
                }

                MUIElement jiraToolItemElement = modelService
                        .find(JIRA_TOOL_ITEM, container);
                if (jiraToolItemElement == null) {
                    return;
                }
                MToolItem jiraToolItem = (MToolItem) jiraToolItemElement;
                jiraToolItem.setToBeRendered(true);
                jiraToolItem.setVisible(true);
            }
        });
    }

    @CanExecute
    public boolean canExecute() {
        try {
            return getCurrentProject() != null && getSettingStore().isIntegrationEnabled();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Execute
    public void execute() {
        Shell activeShell = Display.getCurrent().getActiveShell();
        ImportJiraJQLDialog dialog = new ImportJiraJQLDialog(activeShell);
        if (dialog.open() != ImportJiraJQLDialog.OK) {
            return;
        }
        JiraFilter filter = dialog.getFilter();
        IssueSelectionDialog selectionDialog = new IssueSelectionDialog(activeShell, filter.getIssues());
        if (selectionDialog.open() != IssueSelectionDialog.OK) {
            return;
        }
        createTestCasesAsIssues(selectionDialog.getSelectedFolder(), selectionDialog.getSelectedIssues());
    }

    public void createTestCasesAsIssues(FolderTreeEntity folderTreeEntity, List<JiraIssue> issues) {
        FolderEntity folder = getFolder(folderTreeEntity);
        if (folder == null || issues.isEmpty()) {
            return;
        }
        final TestCaseController testCaseController = TestCaseController.getInstance();
        Job job = new Job(ComposerJiraIntegrationMessageConstant.JOB_TASK_IMPORTING_ISSUES) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(StringUtils.EMPTY, issues.size());
                List<TestCaseTreeEntity> newTreeEntities = new ArrayList<>();
                try {
                    monitor.setTaskName(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_FETCHING_KATALON_FIELD);
                    Optional<Field> katalonCommentField = getKatalonCommentField();
                    monitor.worked(1);

                    for (JiraIssue issue : issues) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        try {
                            String newTestCaseName = testCaseController.getAvailableTestCaseName(folder,
                                    issue.getKey());
                            monitor.setTaskName(MessageFormat.format(
                                    ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_IMPORTING_ISSUE,
                                    newTestCaseName));
                            TestCaseEntity testCase = testCaseController.newTestCaseWithoutSave(folder,
                                    newTestCaseName);
                            testCase.setDescription(getDescriptionFromIssue(issue));
                            String comment = getComment(katalonCommentField, issue);
                            testCase.setComment(comment);

                            JiraObjectToEntityConverter.updateTestCase(issue, testCase);
                            testCaseController.saveNewTestCase(testCase);

                            if (StringUtils.isNotEmpty(comment)) {
                                GroovyGuiUtil.addContentToTestCase(testCase, getScriptAsComment(comment));
                            }

                            newTreeEntities.add(new TestCaseTreeEntity(testCase, folderTreeEntity));

                            monitor.worked(1);
                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                        }
                    }
                    return Status.OK_STATUS;
                } finally {
                    monitor.done();
                    UISynchronizeService.syncExec(() -> {
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity);
                        eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEMS, newTreeEntities.toArray());
                    });
                }
            }

            private Optional<Field> getKatalonCommentField() {
                try {
                    return new JiraIntegrationAuthenticationHandler().getKatalonCustomField(getCredential());
                } catch (JiraIntegrationException | IOException e) {
                    LoggerSingleton.logError(e);
                    return Optional.empty();
                }
            }

            private String getComment(Optional<Field> katalonField, JiraIssue issue) {
                if (!katalonField.isPresent()) {
                    return StringUtils.EMPTY;
                }
                ImprovedIssue fields = issue.getFields();
                if (fields == null) {
                    return StringUtils.EMPTY;
                }
                Map<String, Object> customFields = fields.getCustomFields();
                String customFieldId = katalonField.get().getId();
                if (!customFields.containsKey(customFieldId)) {
                    return StringUtils.EMPTY;
                }
                return ObjectUtils.toString(customFields.get(customFieldId));
            }

            private String getScriptAsComment(String comment) {
                StringBuilder commentBuilder = new StringBuilder();
                Arrays.asList(StringUtils.split(comment, "\r\n")).forEach(line -> {
                    commentBuilder.append(String.format("WebUI.comment('%s')\n", GroovyStringUtil.escapeGroovy(line)));
                });
                return commentBuilder.toString();
            }
        };
        job.setUser(true);
        job.schedule();

    }

    private String getDescriptionFromIssue(JiraIssue issue) {
        return String.format("%s: %s\n%s: %s", StringConstants.SUMMARY,
                StringUtils.defaultString(issue.getFields().getSummary()), StringConstants.DESCRIPTION,
                StringUtils.defaultString(issue.getFields().getDescription()));
    }

    private FolderEntity getFolder(FolderTreeEntity folderTreeEntity) {
        try {
            return folderTreeEntity.getObject();
        } catch (Exception ignored) {}
        return null;
    }
}
