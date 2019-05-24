package com.kms.katalon.composer.testdata.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityDialog;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestDataReferencesDialog extends AbstractDeleteReferredEntityDialog {
    private Map<String, List<TestSuiteTestCaseLink>> referencesInTestSuite;

    private List<TestSuiteTestCaseLink> allAffectedTestCase;

    private TableViewer testCaseLinkTableViewer;

    public TestDataReferencesDialog(Shell parentShell, String testDataId,
            Map<String, List<TestSuiteTestCaseLink>> referencesInTestSuite, List<TestCaseEntity> referencesInTestCase,
            boolean showYesNoToAllButtons) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_DATA_REFERENCES);
        setEntityId(testDataId);
        this.referencesInTestSuite = referencesInTestSuite;
        setAllAffectedTestCase(referencesInTestCase);
        setShowYesNoToAllButtons(showYesNoToAllButtons);
    }

    @Override
    protected Control createDialogBody(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.NONE);
        sashForm.setSashWidth(5);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createSourceTable(sashForm);

        createTestCaseLinkTable(sashForm);

        sashForm.setWeights(new int[] { 45, 55 });
        return parent;
    }

    @Override
    protected void registerControlModifyListeners() {
        super.registerControlModifyListeners();

        tableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateTestCaseLinkTableInput();
            }
        });
    }

    private void updateTestCaseLinkTableInput() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.size() != 1)
            return;
        String testSuiteId = (String) selection.getFirstElement();
        if (StringUtils.equals(testSuiteId, StringConstants.DIA_LBL_REFERENCED_TEST_CASES)) {
            testCaseLinkTableViewer.setInput(allAffectedTestCase);
        } else {
            testCaseLinkTableViewer.setInput(referencesInTestSuite.get(testSuiteId));
        }
    }

    @Override
    protected void setInput() {
        List<String> sources = new ArrayList<String>();
        sources.add(StringConstants.DIA_LBL_REFERENCED_TEST_CASES);
        sources.addAll(referencesInTestSuite.keySet());

        tableViewer.setInput(sources);
        tableViewer.getTable().setSelection(0);
        updateTestCaseLinkTableInput();

        mainComposite.layout(true, true);
    }

    private void createSourceTable(Composite parent) {
        Composite compositeSourceTable = new Composite(parent, SWT.NONE);

        tableViewer = new TableViewer(compositeSourceTable, SWT.FULL_SELECTION);
        Table tableTestSuite = tableViewer.getTable();
        tableTestSuite.setHeaderVisible(true);

        TableViewerColumn tableViewerColumnTestSuiteID = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnTestSuiteID = tableViewerColumnTestSuiteID.getColumn();
        tblclmnTestSuiteID.setWidth(200);
        tblclmnTestSuiteID.setText(StringConstants.DIA_COL_REFERENCED_BY);
        tableViewerColumnTestSuiteID.setLabelProvider(new ColumnLabelProvider());

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnTestSuiteID, new ColumnWeightData(98, 190));
        compositeSourceTable.setLayout(tableLayout);
    }

    private void createTestCaseLinkTable(Composite parent) {
        Composite compositeTestCaseLinkTable = new Composite(parent, SWT.NONE);

        testCaseLinkTableViewer = new TableViewer(compositeTestCaseLinkTable, SWT.FULL_SELECTION);
        Table tableTestCaseLink = testCaseLinkTableViewer.getTable();
        tableTestCaseLink.setHeaderVisible(true);
        tableTestCaseLink.setLinesVisible(ControlUtils.shouldLineVisble(tableTestCaseLink.getDisplay()));

        TableViewerColumn tableViewerColumnTestCaseOrder = new TableViewerColumn(testCaseLinkTableViewer, SWT.NONE);
        TableColumn tblclmnTestCaseLinkOrder = tableViewerColumnTestCaseOrder.getColumn();
        tblclmnTestCaseLinkOrder.setText(StringConstants.NO_);
        tableViewerColumnTestCaseOrder.setLabelProvider(new ColumnLabelProvider());
        tableViewerColumnTestCaseOrder.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof TestSuiteTestCaseLink)) {
                    return StringConstants.EMPTY;
                }
                TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
                Object o = getSelectedSource();
                if (o != null && o instanceof TestSuiteEntity) {
                    // The selection is TestSuiteEntity
                    TestSuiteEntity testSuite = (TestSuiteEntity) o;
                    return Integer.toString(testSuite.getTestSuiteTestCaseLinks().indexOf(testCaseLink) + 1);
                } else {
                    if (o != null) {
                        return Integer.toString(allAffectedTestCase.indexOf(testCaseLink) + 1);
                    } else {
                        return StringConstants.EMPTY;
                    }
                }
            }
        });

        TableViewerColumn tableViewerColumnTestCaseID = new TableViewerColumn(testCaseLinkTableViewer, SWT.NONE);
        TableColumn tblclmnTestCaseID = tableViewerColumnTestCaseID.getColumn();
        tblclmnTestCaseID.setWidth(190);
        tblclmnTestCaseID.setText(StringConstants.DIA_COL_TEST_CASE_ID);
        tableViewerColumnTestCaseID.setLabelProvider(new ColumnLabelProvider());
        tableViewerColumnTestCaseID.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof TestSuiteTestCaseLink)) {
                    return StringConstants.EMPTY;
                }
                TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
                return testCaseLink.getTestCaseId();
            }
        });

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnTestCaseLinkOrder, new ColumnWeightData(0, 40));
        tableLayout.setColumnData(tblclmnTestCaseID, new ColumnWeightData(80, 190));

        compositeTestCaseLinkTable.setLayout(tableLayout);
        testCaseLinkTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    }

    private Object getSelectedSource() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.size() != 1) {
            return null;
        }
        String testSuiteId = (String) selection.getFirstElement();

        try {
            if (StringUtils.equals(testSuiteId, StringConstants.DIA_LBL_REFERENCED_TEST_CASES)) {
                return StringConstants.DIA_LBL_REFERENCED_TEST_CASES;
            } else {
                return TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteId,
                        ProjectController.getInstance().getCurrentProject());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    private void setAllAffectedTestCase(List<TestCaseEntity> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            return;
        }
        allAffectedTestCase = new ArrayList<TestSuiteTestCaseLink>();
        for (TestCaseEntity tc : testCases) {
            TestSuiteTestCaseLink link = new TestSuiteTestCaseLink();
            link.setTestCaseId(tc.getIdForDisplay());
            allAffectedTestCase.add(link);
        }
    }
}
