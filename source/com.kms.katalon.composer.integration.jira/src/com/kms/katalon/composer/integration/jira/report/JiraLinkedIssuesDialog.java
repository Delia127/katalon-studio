package com.kms.katalon.composer.integration.jira.report;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.control.DropdownToolItemSelectionListener;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.integration.jira.report.dialog.CreateAsSubTaskBrowserDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.JiraIssueBrowserDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.LinkJiraIssueDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.JiraIssueProgressResult;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.LinkJiraIssueProgressDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.NewIssueProgressDialog;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.UpdateJiraIssueProgressDialog;
import com.kms.katalon.composer.integration.jira.report.provider.JiraIssueIDLabelProvider;
import com.kms.katalon.composer.integration.jira.report.provider.JiraIssueLabelProvider;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;
import com.kms.katalon.integration.jira.issue.EditIssueHTMLLinkProvider;
import com.kms.katalon.integration.jira.issue.NewIssueHTMLLinkProvider;
import com.kms.katalon.integration.jira.issue.NewSubtaskHTMLLinkProvider;

public class JiraLinkedIssuesDialog extends AbstractDialog implements JiraUIComponent {

    public static final int CLMN_ID_IDEX = 0;

    public static final int CLMN_SUMMARY_IDEX = 1;

    public static final int CLMN_STATUS_IDEX = 2;

    private TableViewer tableViewer;

    private JiraIssueCollection jiraIssueCollection;

    private ToolItem tltmRemove, tltmEdit;

    private TestCaseLogRecord logRecord;

    private boolean changed;

    public JiraLinkedIssuesDialog(Shell parentShell, JiraIssueCollection jiraIssueCollection,
            TestCaseLogRecord logRecord) {
        super(parentShell);
        this.jiraIssueCollection = jiraIssueCollection;
        this.logRecord = logRecord;
    }

    @Override
    protected void registerControlModifyListeners() {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection structuredSelection = tableViewer.getStructuredSelection();
                tltmRemove.setEnabled(!structuredSelection.isEmpty());
                tltmEdit.setEnabled(structuredSelection.size() == 1);
            }
        });

        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                jiraIssueCollection.getIssues().removeAll(tableViewer.getStructuredSelection().toList());
                tableViewer.refresh();
                changed = true;
            }
        });

        tltmEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openEditIssueDialog();
            }
        });
    }

    @Override
    protected void setInput() {
        tableViewer.setInput(jiraIssueCollection.getIssues());
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        createToolBar(composite);

        createTableCompiste(composite);
        return composite;
    }

    private void createTableCompiste(Composite composite) {
        Composite issueTableComposite = new Composite(composite, SWT.NONE);
        issueTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumnLayout tableLayout = new TableColumnLayout();
        issueTableComposite.setLayout(tableLayout);

        tableViewer = new TableViewer(issueTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn tableViewerColumnID = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnID = tableViewerColumnID.getColumn();
        tblclmnID.setText(StringConstants.ID);
        tableViewerColumnID.setLabelProvider(new JiraIssueIDLabelProvider(CLMN_ID_IDEX));
        tableLayout.setColumnData(tblclmnID, new ColumnWeightData(20, 100));

        TableViewerColumn tableViewerColumnSummary = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnSummary = tableViewerColumnSummary.getColumn();
        tblclmnSummary.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_SUMMARY);
        tableViewerColumnSummary.setLabelProvider(new JiraIssueLabelProvider(CLMN_SUMMARY_IDEX));
        tableLayout.setColumnData(tblclmnSummary, new ColumnWeightData(50, 150));

        TableViewerColumn tableViewerColumnStatus = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnStatus = tableViewerColumnStatus.getColumn();
        tblclmnStatus.setText(StringConstants.STATUS);
        tableViewerColumnStatus.setLabelProvider(new JiraIssueLabelProvider(CLMN_STATUS_IDEX));
        tableLayout.setColumnData(tblclmnStatus, new ColumnWeightData(20, 150));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
    }

    private void createToolBar(Composite composite) {
        ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolItem tltmAdd = new ToolItem(toolBar, SWT.DROP_DOWN);
        tltmAdd.setText(StringConstants.ADD);
        tltmAdd.setImage(ImageConstants.IMG_16_ADD);
        tltmAdd.addSelectionListener(new DropdownToolItemSelectionListener() {

            @Override
            protected Menu getMenu() {
                Menu addMenu = new Menu(getShell());

                MenuItem newIssueItem = new MenuItem(addMenu, SWT.PUSH);
                newIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_NEW_JIRA_ISSUE);
                newIssueItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        try {
                            openNewIssueDialog(new JiraIssueBrowserDialog(getShell(), logRecord,
                                    new NewIssueHTMLLinkProvider(logRecord, getSettingStore())));
                        } catch (URISyntaxException | IOException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    }
                });

                MenuItem newAsSubTaskItem = new MenuItem(addMenu, SWT.PUSH);
                newAsSubTaskItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_AS_SUB_TASK);
                newAsSubTaskItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        openCreateAsSubTaskDialog();
                    }
                });

                MenuItem linkIssueItem = new MenuItem(addMenu, SWT.PUSH);
                linkIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_LINK_TO_JIRA_ISSUE);
                linkIssueItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        openLinkIssueDialog();
                    }
                });
                return addMenu;
            }
        });

        tltmEdit = new ToolItem(toolBar, SWT.NONE);
        tltmEdit.setImage(ImageConstants.IMG_16_EDIT);
        tltmEdit.setText(StringConstants.EDIT);
        tltmEdit.setEnabled(false);

        tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.setImage(ImageConstants.IMG_16_REMOVE);
        tltmRemove.setText(StringConstants.REMOVE);
        tltmRemove.setEnabled(false);
    }

    private void openEditIssueDialog() {
        try {
            Shell shell = getShell();
            JiraIssue oldIssue = (JiraIssue) tableViewer.getStructuredSelection().getFirstElement();
            JiraIssueBrowserDialog browserDialog = new JiraIssueBrowserDialog(shell, logRecord,
                    new EditIssueHTMLLinkProvider(logRecord, getSettingStore(), oldIssue));

            if (browserDialog.open() != Dialog.OK) {
                return;
            }
            JiraIssueProgressResult result = new UpdateJiraIssueProgressDialog(shell, browserDialog.getIssueKey(),
                    logRecord).run();
            if (checkResult(result)) {
                Collections.replaceAll(jiraIssueCollection.getIssues(), oldIssue, result.getJiraIssue());
                tableViewer.refresh();
            }
        } catch (ClassCastException | URISyntaxException | IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void openCreateAsSubTaskDialog() {
        Shell shell = getShell();
        LinkJiraIssueDialog linkDialog = new LinkJiraIssueDialog(shell,
                ComposerJiraIntegrationMessageConstant.DIA_TITLE_CREATE_NEW_AS_SUB_TASK,
                ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_CREATE_NEW_AS_SUB_TASK,
                ComposerJiraIntegrationMessageConstant.DIA_LBL_CREATE_NEW_AS_SUB_TASK);
        if (linkDialog.open() != Dialog.OK) {
            return;
        }
        JiraIssueProgressResult result = new UpdateJiraIssueProgressDialog(shell, linkDialog.getIssueKey(), logRecord)
                .run();
        if (!checkResult(result)) {
            return;
        }

        try {
            JiraIssue parentIssue = result.getJiraIssue();
            CreateAsSubTaskBrowserDialog browserDialog = new CreateAsSubTaskBrowserDialog(getShell(), logRecord,
                    new NewSubtaskHTMLLinkProvider(logRecord, getSettingStore(), parentIssue));
            if (browserDialog.open() != Dialog.OK) {
                return;
            }
            String issueKey = browserDialog.getIssueKey();
            if (parentIssue.getKey().equals(issueKey)) {
                return;
            }
            checkResultAndUpdateTable(new NewIssueProgressDialog(getShell(), issueKey, logRecord).run());
            
        } catch (URISyntaxException | IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void openLinkIssueDialog() {
        Shell shell = getShell();
        LinkJiraIssueDialog linkDialog = new LinkJiraIssueDialog(shell,
                ComposerJiraIntegrationMessageConstant.DIA_TITLE_LINK_TO_EXISTING_ISSUE,
                ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_LINK_TO_EXISTING_ISSUE,
                ComposerJiraIntegrationMessageConstant.DIA_LBL_LINK_TO_EXISTING_ISSUE);
        if (linkDialog.open() != Dialog.OK) {
            return;
        }
        JiraIssueProgressResult result = new LinkJiraIssueProgressDialog(shell, linkDialog.getIssueKey(), logRecord)
                .run();
        if (!checkResult(result)) {
            return;
        }
        JiraIssue newIssue = result.getJiraIssue();
        addIssueToCollection(newIssue);
        refreshAndSetSelection(newIssue);
    }

    private JiraIssue addIssueToCollection(JiraIssue newIssue) {
        int index = indexInCollection(newIssue, jiraIssueCollection);
        List<JiraIssue> issues = jiraIssueCollection.getIssues();
        if (index >= 0) {
            issues.remove(index);
            issues.add(index, newIssue);
        } else {
            issues.add(newIssue);
        }
        return newIssue;
    }

    private int indexInCollection(JiraIssue newIssue, JiraIssueCollection issueCollection) {
        List<JiraIssue> listIssues = issueCollection.getIssues();
        for (int index = 0; index < listIssues.size(); index++) {
            if (newIssue.getKey().equals(listIssues.get(index).getKey())) {
                return index;
            }
        }
        return -1;
    }

    private void openNewIssueDialog(JiraIssueBrowserDialog browserDialog) {
        if (browserDialog.open() != Dialog.OK) {
            return;
        }
        String issueKey = browserDialog.getIssueKey();
        JiraIssueProgressResult result = new NewIssueProgressDialog(getShell(), issueKey, logRecord).run();
        checkResultAndUpdateTable(result);
    }

    private void checkResultAndUpdateTable(JiraIssueProgressResult result) {
        if (checkResult(result)) {
            JiraIssue newJiraIssue = result.getJiraIssue();
            jiraIssueCollection.getIssues().add(newJiraIssue);
            refreshAndSetSelection(newJiraIssue);
        }
    }

    private void refreshAndSetSelection(JiraIssue newJiraIssue) {
        tableViewer.refresh();
        tableViewer.setSelection(new StructuredSelection(newJiraIssue));
        changed = true;
    }

    private boolean checkResult(JiraIssueProgressResult result) {
        if (result.hasError()) {
            JiraIntegrationException error = result.getError();
            MultiStatusErrorDialog.showErrorDialog(error, error.getMessage(), error.getMessage());
            LoggerSingleton.logError(error);
            return false;
        }
        return result.isComplete();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }

    @Override
    public String getDialogTitle() {
        return ComposerJiraIntegrationMessageConstant.DIA_TITLE_LINKED_JIRA_ISSUES;
    }

    public boolean isChanged() {
        return changed;
    }

    public JiraIssueCollection getJiraIssueCollection() {
        return jiraIssueCollection;
    }
}
