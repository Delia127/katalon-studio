package com.kms.katalon.composer.testdata.dialog;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteEntityDialog;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestDataReferencesDialog extends AbstractDeleteEntityDialog {

    // Fields
    private Map<String, List<TestSuiteTestCaseLink>> fTestDataReferences;
    private DataFileEntity fDataFileEntity;

    // Controls
    private TableViewer testSuiteTableViewer;
    private TableViewer testCaseLinkTableViewer;
    private Label lblStatus;

    public TestDataReferencesDialog(Shell parentShell, DataFileEntity dataFileEntity,
            Map<String, List<TestSuiteTestCaseLink>> testDataReferences, AbstractDeleteReferredEntityHandler handler) {
        super(parentShell, handler);
        fTestDataReferences = testDataReferences;
        fDataFileEntity = dataFileEntity;
    }

    @Override
    protected void registerControlModifyListeners() {
        testSuiteTableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateTestCaseLinkTableInput();
            }
        });

        mainComposite.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                lblStatus.pack(true);
            }
        });
    }

    private void updateTestCaseLinkTableInput() {
        IStructuredSelection selection = (IStructuredSelection) testSuiteTableViewer.getSelection();
        if (selection == null || selection.size() != 1) return;
        String testSuiteId = (String) selection.getFirstElement();
        testCaseLinkTableViewer.setInput(fTestDataReferences.get(testSuiteId));
    }

    @Override
    protected void setInput() {
        try {
            lblStatus.setText(MessageFormat.format(StringConstants.DIA_MSG_HEADER_TEST_DATA_REFERENCES,
                    TestDataController.getInstance().getIdForDisplay(fDataFileEntity)));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        testSuiteTableViewer.setInput(fTestDataReferences.keySet());
        testSuiteTableViewer.getTable().setSelection(0);
        updateTestCaseLinkTableInput();

        mainComposite.layout(true, true);
    }

    @Override
    protected Control createDialogComposite(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.marginWidth = 0;
        glMainComposite.marginHeight = 0;
        mainComposite.setLayout(glMainComposite);

        Composite compositeHeader = new Composite(mainComposite, SWT.NONE);
        GridLayout glCompositeHeader = new GridLayout(2, false);
        glCompositeHeader.horizontalSpacing = 15;
        compositeHeader.setLayout(glCompositeHeader);
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblImage = new Label(compositeHeader, SWT.NONE);
        lblImage.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

        lblStatus = new Label(compositeHeader, SWT.WRAP);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        SashForm sashForm = new SashForm(mainComposite, SWT.NONE);
        sashForm.setSashWidth(5);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createTestSuiteTable(sashForm);

        createTestCaseLinkTable(sashForm);

        sashForm.setWeights(new int[] { 45, 55 });

        return mainComposite;
    }

    private void createTestSuiteTable(Composite parent) {
        Composite compositeTestSuiteTable = new Composite(parent, SWT.NONE);

        testSuiteTableViewer = new TableViewer(compositeTestSuiteTable, SWT.FULL_SELECTION);
        Table tableTestSuite = testSuiteTableViewer.getTable();
        tableTestSuite.setHeaderVisible(true);

        TableViewerColumn tableViewerColumnTestSuiteID = new TableViewerColumn(testSuiteTableViewer, SWT.NONE);
        TableColumn tblclmnTestSuiteID = tableViewerColumnTestSuiteID.getColumn();
        tblclmnTestSuiteID.setWidth(200);
        tblclmnTestSuiteID.setText("Test Suite ID");
        tableViewerColumnTestSuiteID.setLabelProvider(new ColumnLabelProvider());

        testSuiteTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnTestSuiteID, new ColumnWeightData(98, 190));
        compositeTestSuiteTable.setLayout(tableLayout);
    }

    private void createTestCaseLinkTable(Composite parent) {
        Composite compositeTestCaseLinkTable = new Composite(parent, SWT.NONE);

        testCaseLinkTableViewer = new TableViewer(compositeTestCaseLinkTable, SWT.FULL_SELECTION);
        Table tableTestCaseLink = testCaseLinkTableViewer.getTable();
        tableTestCaseLink.setHeaderVisible(true);
        tableTestCaseLink.setLinesVisible(true);

        TableViewerColumn tableViewerColumnTestCaseOrder = new TableViewerColumn(testCaseLinkTableViewer, SWT.NONE);
        TableColumn tblclmnTestCaseLinkOrder = tableViewerColumnTestCaseOrder.getColumn();
        tblclmnTestCaseLinkOrder.setText(StringConstants.ID);
        tableViewerColumnTestCaseOrder.setLabelProvider(new ColumnLabelProvider());
        tableViewerColumnTestCaseOrder.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof TestSuiteTestCaseLink)) {
                    return "";
                }
                TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
                TestSuiteEntity testSuite = getSelectedTestSuite();
                return Integer.toString(testSuite.getTestSuiteTestCaseLinks().indexOf(testCaseLink) + 1);
            }
        });

        TableViewerColumn tableViewerColumnTestCaseID = new TableViewerColumn(testCaseLinkTableViewer, SWT.NONE);
        TableColumn tblclmnTestCaseID = tableViewerColumnTestCaseID.getColumn();
        tblclmnTestCaseID.setWidth(190);
        tblclmnTestCaseID.setText("Test Case ID");
        tableViewerColumnTestCaseID.setLabelProvider(new ColumnLabelProvider());
        tableViewerColumnTestCaseID.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof TestSuiteTestCaseLink)) {
                    return "";
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

    @Override
    protected String getDialogTitle() {
        return "Test Data's References Dialog";
    }
    
    private TestSuiteEntity getSelectedTestSuite() {
        IStructuredSelection selection = (IStructuredSelection) testSuiteTableViewer.getSelection();
        if (selection == null || selection.size() != 1) {
            return null;
        }
        String testSuiteId = (String) selection.getFirstElement();
        try {
            return TestSuiteController.getInstance().getTestSuiteByDisplayId(testSuiteId,
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }
}
