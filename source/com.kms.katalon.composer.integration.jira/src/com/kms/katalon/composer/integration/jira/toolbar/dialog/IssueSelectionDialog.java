package com.kms.katalon.composer.integration.jira.toolbar.dialog;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.control.TreeEntitySelectionComposite;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.FolderEntityTreeViewerFilter;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.integration.jira.report.provider.JiraIssueIDLabelProvider;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class IssueSelectionDialog extends AbstractDialog {
    public static final int CLMN_IMPORTED_IDX = 0;

    public static final int CLMN_STATUS_IDX = 3;

    public static final int CLMN_SUMMARY_IDX = 2;

    public static final int CLMN_ISSUE_IDX = 1;

    private List<JiraIssue> issues;

    private TableViewer issueViewer;

    private TreeEntitySelectionComposite folderTreeComposite;

    private FolderTreeEntity selectedFolder;

    private JiraImportedColumnLabelProvider importedIssueProvider;

    public IssueSelectionDialog(Shell parentShell, List<JiraIssue> issues) {
        super(parentShell);
        this.issues = issues;
    }

    @Override
    protected void registerControlModifyListeners() {
        TreeViewer treeViewer = folderTreeComposite.getTreeViewer();
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedFolderChanged();
            }
        });

        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object firstElement = treeViewer.getStructuredSelection().getFirstElement();
                treeViewer.setExpandedState(firstElement, !treeViewer.getExpandedState(firstElement));
            }
        });
    }

    private void refreshIssueTable() {
        Job job = new Job(ComposerJiraIntegrationMessageConstant.DIA_JOB_REFRESHING) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                UISynchronizeService.syncExec(() -> getButton(OK).setEnabled(false));

                importedIssueProvider.setExistedIssues(getExistedJiraIssues());

                UISynchronizeService.syncExec(() -> {
                    getButton(OK).setEnabled(!getSelectedIssues().isEmpty());

                    issueViewer.refresh();
                });
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    private void selectedFolderChanged() {
        selectedFolder = (FolderTreeEntity) folderTreeComposite.getTreeViewer()
                .getStructuredSelection()
                .getFirstElement();
        refreshIssueTable();
    }

    @Override
    protected void setInput() {
        try {
            FolderEntity testCaseRoot = FolderController.getInstance()
                    .getTestCaseRoot(ProjectController.getInstance().getCurrentProject());

            selectedFolder = TreeEntityUtil.createSelectedTreeEntityHierachy(testCaseRoot, testCaseRoot);
            folderTreeComposite.setInput(new Object[] { selectedFolder });
            folderTreeComposite.getTreeViewer().setSelection(new StructuredSelection(selectedFolder));
            selectedFolderChanged();

            issueViewer.setInput(issues);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        SashForm container = new SashForm(parent, SWT.NONE);

        createTestCaseFolderSelectionComposite(container);

        createIssueTableViewer(container);
        container.setWeights(new int[] { 3, 7 });

        return container;
    }

    private void createTestCaseFolderSelectionComposite(Composite container) {
        Composite folderSelectionComposite = new Composite(container, SWT.NONE);
        GridLayout leftCompositeLayout = new GridLayout(1, false);
        leftCompositeLayout.marginHeight = 0;
        folderSelectionComposite.setLayout(leftCompositeLayout);

        Label lblFolderSelection = new Label(folderSelectionComposite, SWT.NONE);
        lblFolderSelection.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        lblFolderSelection.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_CHOOSE_DESTINATION);

        EntityProvider contentProvider = new EntityProvider();
        folderTreeComposite = new TreeEntitySelectionComposite(folderSelectionComposite, SWT.BORDER, contentProvider,
                new FolderEntityTreeViewerFilter(contentProvider), new EntityLabelProvider());
        folderTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout treeLayout = new GridLayout(1, false);
        treeLayout.marginWidth = 0;
        treeLayout.marginHeight = 0;
        folderTreeComposite.setLayout(treeLayout);

        folderTreeComposite.getTreeViewer().setAutoExpandLevel(TreeViewer.ALL_LEVELS);
    }

    private void createIssueTableViewer(Composite container) {
        issueViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = issueViewer.getTable();
        table.setHeaderVisible(true);

        TableViewerColumn tableViewerColumnImported = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnImported = tableViewerColumnImported.getColumn();
        tblclmnImported.setWidth(30);
        importedIssueProvider = new JiraImportedColumnLabelProvider(CLMN_IMPORTED_IDX);
        tableViewerColumnImported.setLabelProvider(importedIssueProvider);

        TableViewerColumn tableViewerColumnId = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnId = tableViewerColumnId.getColumn();
        tblclmnId.setWidth(80);
        tblclmnId.setText(ComposerJiraIntegrationMessageConstant.CM_ISSUE);
        tableViewerColumnId.setLabelProvider(new JiraIssueIDLabelProvider(CLMN_ISSUE_IDX));

        TableViewerColumn tableViewerColumnSummary = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnSummary = tableViewerColumnSummary.getColumn();
        tblclmnSummary.setWidth(250);
        tblclmnSummary.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_SUMMARY);
        tableViewerColumnSummary.setLabelProvider(new JiraImportedIssueLabelProvider(CLMN_SUMMARY_IDX));

        TableViewerColumn tableViewerColumnIssueType = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnType = tableViewerColumnIssueType.getColumn();
        tblclmnType.setWidth(150);
        tblclmnType.setText(StringConstants.STATUS);
        tableViewerColumnIssueType.setLabelProvider(new JiraImportedIssueLabelProvider(CLMN_STATUS_IDX));
        issueViewer.setContentProvider(ArrayContentProvider.getInstance());
        table.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(issueViewer, ToolTip.NO_RECREATE);
    }

    public List<JiraIssue> getSelectedIssues() {
        return issues.parallelStream()
                .filter(issue -> !getExistedJiraIssues().containsKey(issue.getId()))
                .collect(Collectors.toList());
    }

    public FolderTreeEntity getSelectedFolder() {
        return selectedFolder;
    }

    public Map<Long, JiraIssue> getExistedJiraIssues() {
        try {
            FolderEntity folder = selectedFolder.getObject();
            Map<Long, JiraIssue> existedIssues = new HashMap<>();
            FolderController.getInstance().getChildren(folder).forEach(child -> {
                if (!(child instanceof TestCaseEntity)) {
                    return;
                }
                JiraIssue jiraIssue = JiraObjectToEntityConverter.getJiraIssue((TestCaseEntity) child);
                if (jiraIssue != null) {
                    existedIssues.put(jiraIssue.getId(), jiraIssue);
                }
            });
            return existedIssues;
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(850, 400);
    }

    @Override
    public String getDialogTitle() {
        return ComposerJiraIntegrationMessageConstant.DIA_TITLE_JIRA_ISSUES;
    }
}
