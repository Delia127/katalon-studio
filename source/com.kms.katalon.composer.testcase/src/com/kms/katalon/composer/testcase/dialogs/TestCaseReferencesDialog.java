package com.kms.katalon.composer.testcase.dialogs;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
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
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.handlers.DeleteTestCaseHandler;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestCaseReferencesDialog extends AbstractDeleteEntityDialog {

    // Control
    private TableViewer tableViewer;

    // Field
    private List<TestSuiteEntity> fTestSuiteEntities;
    private TestCaseEntity fTestCaseEntity;

    private Label lblStatus;

    public TestCaseReferencesDialog(Shell parentShell, TestCaseEntity testCase,
            List<TestSuiteEntity> testSuiteEntities, DeleteTestCaseHandler handler) {
        super(parentShell, handler);
        setTestSuiteEntities(testSuiteEntities);
        setTestCaseEntity(testCase);
    }

    private void setTestSuiteEntities(List<TestSuiteEntity> testSuiteEntities) {
        this.fTestSuiteEntities = testSuiteEntities;
    }

    @Override
    protected void registerControlModifyListeners() {
        mainComposite.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                lblStatus.pack(true);
            }
        });
    }

    @Override
    protected void setInput() {
        try {
            lblStatus.setText(MessageFormat.format(StringConstants.DIA_MSG_HEADER_TEST_CASE_REFERENCES,
                    TestCaseController.getInstance().getIdForDisplay(fTestCaseEntity)));

            tableViewer.setInput(fTestSuiteEntities);
            getButton(CANCEL).forceFocus();
            mainComposite.layout(true, true);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_TEST_CASE_REFERENCES;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    private void setTestCaseEntity(TestCaseEntity testCaseEntity) {
        this.fTestCaseEntity = testCaseEntity;
    }

    @Override
    protected Control createDialogComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        glComposite.verticalSpacing = 10;
        composite.setLayout(glComposite);

        Composite compositeHeader = new Composite(composite, SWT.NONE);
        compositeHeader.setLayout(new GridLayout(2, false));
        compositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStatusImg = new Label(compositeHeader, SWT.NONE);
        lblStatusImg.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

        lblStatus = new Label(compositeHeader, SWT.WRAP);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn tbvclmOrder = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumn = tbvclmOrder.getColumn();
        tblclmnNewColumn.setWidth(40);
        tblclmnNewColumn.setText(StringConstants.NO_);
        tbvclmOrder.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof TestSuiteEntity)) {
                    return "";
                }
                return Integer.toString(fTestSuiteEntities.indexOf(element) + 1);
            }
        });

        TableViewerColumn tbvclmTestSuiteID = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnTestSuiteID = tbvclmTestSuiteID.getColumn();
        tblclmnTestSuiteID.setWidth(350);
        tblclmnTestSuiteID.setText(StringConstants.DIA_FIELD_TEST_SUITE_ID);
        tbvclmTestSuiteID.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == null || !(element instanceof TestSuiteEntity)) {
                    return "";
                }
                try {
                    return TestSuiteController.getInstance().getIdForDisplay((TestSuiteEntity) element);
                } catch (Exception e) {
                    return "";
                }
            }
        });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        return composite;
    }
}
