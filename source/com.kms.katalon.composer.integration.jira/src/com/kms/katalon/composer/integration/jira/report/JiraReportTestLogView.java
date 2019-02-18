package com.kms.katalon.composer.integration.jira.report;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.control.DropdownToolItemSelectionListener;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.integration.jira.report.dialog.progress.JiraIssueProgressResult;
import com.kms.katalon.composer.integration.jira.report.provider.JiraIssueIDLabelProvider;
import com.kms.katalon.composer.integration.jira.report.provider.JiraIssueLabelProvider;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;

public class JiraReportTestLogView extends TestCaseLogDetailsIntegrationView implements JiraUIComponent {

    public static final int CLMN_ID_IDEX = 0;

    public static final int CLMN_SUMMARY_IDEX = 1;

    public static final int CLMN_STATUS_IDEX = 2;

    private TableViewer tableViewer;

    private JiraIssueCollection jiraIssueCollection;

    private ToolItem tltmRemove, tltmEdit;

    private TestCaseLogRecord logRecord;

    private JiraCreateIssueHandler createIssueHandler;

    private Composite container;

    public JiraReportTestLogView(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        super(reportEntity, testSuiteLogRecord);
    }

    @Override
    public Composite createContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        createToolBar(container);

        createTableCompiste(container);

        registerControlModifyListeners();

        enableContainer();

        return container;
    }

    @Override
    public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
        logRecord = testCaseLogRecord;
        enableContainer();
        if (logRecord == null) {
            return;
        }
        
        setInput(testCaseLogRecord);
    }

    private void setInput(TestCaseLogRecord testCaseLogRecord) {
        int index = getTestCaseLogRecordIndex(logRecord, reportEntity);
        Optional<JiraIssueCollection> optionalJiraIssueCollection = JiraObjectToEntityConverter
                .getOptionalJiraIssueCollection(reportEntity, index);
        jiraIssueCollection = new JiraIssueCollection(testCaseLogRecord.getId());
        if (optionalJiraIssueCollection.isPresent()) {
            jiraIssueCollection = optionalJiraIssueCollection.get();
        }
        tableViewer.setInput(jiraIssueCollection.getIssues());
        createIssueHandler = new JiraCreateIssueHandler(getShell(), logRecord);
    }

    private void enableContainer() {
        ControlUtils.recursiveSetEnabled(container, logRecord != null);
    }

    @Override
    public void createTableContextMenu(Menu parentMenu, ISelection selection) {
    }

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
                
                saveJiraReport();
            }
        });

        tltmEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openEditIssueDialog();
            }
        });
    }

    private Shell getShell() {
        return container.getShell();
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
                        openNewIssueDialog();
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

    private int getNumSteps() {
        return logRecord.getChildRecords().length;
    }

    private void openEditIssueDialog() {
        JiraIssue oldIssue = (JiraIssue) tableViewer.getStructuredSelection().getFirstElement();
        JiraIssueProgressResult handlerResult = createIssueHandler.openEditIssueDialog(oldIssue);
        if (createIssueHandler.checkResult(handlerResult)) {
            Collections.replaceAll(jiraIssueCollection.getIssues(), oldIssue, handlerResult.getJiraIssue());
            tableViewer.refresh();
        }
    }

    public void openCreateAsSubTaskDialog() {
        JiraIssueProgressResult handlerResult = createIssueHandler.openCreateAsSubTaskDialog(getNumSteps());
        checkResultAndUpdateTable(handlerResult);
    }

    public void openLinkIssueDialog() {
        JiraIssueProgressResult handlerResult = createIssueHandler.openLinkIssueDialog();
        if (!createIssueHandler.checkResult(handlerResult)) {
            return;
        }
        JiraIssue newJiraIssue = handlerResult.getJiraIssue();
        addIssueToCollection(newJiraIssue);
        refreshAndSetSelection(newJiraIssue);
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

    public void openNewIssueDialog() {
        JiraIssueProgressResult handlerResult = createIssueHandler.openNewIssueDialog(getNumSteps());
        checkResultAndUpdateTable(handlerResult);
    }

    private void checkResultAndUpdateTable(JiraIssueProgressResult result) {
        if (createIssueHandler.checkResult(result)) {
            JiraIssue newJiraIssue = result.getJiraIssue();
            jiraIssueCollection.getIssues().add(newJiraIssue);
            refreshAndSetSelection(newJiraIssue);
        }
    }

    private void refreshAndSetSelection(JiraIssue newJiraIssue) {
        tableViewer.refresh();
        tableViewer.setSelection(new StructuredSelection(newJiraIssue));

        saveJiraReport();
    }

    public JiraIssueCollection getJiraIssueCollection() {
        return jiraIssueCollection;
    }

    private void saveJiraReport() {
        try {
            updateJiraReport(logRecord, jiraIssueCollection, reportEntity);
        } catch (JiraIntegrationException e) {
            MultiStatusErrorDialog.showErrorDialog(StringConstants.ERROR,
                    ComposerJiraIntegrationMessageConstant.VIEW_MSG_UNABLE_TO_UPDATE_REPORT, e.getMessage());
        }
    }
}
