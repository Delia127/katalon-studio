package com.kms.katalon.composer.testsuite.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.control.StyledTextMessage;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.filters.TestDataTreeViewerFilter;
import com.kms.katalon.composer.testsuite.providers.TestDataIDColumnLabelProvider;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataLinkFinderDialog extends Dialog {
    private StyledText textSearch;
    private List<TestCaseTestDataLink> testDataLinks;
    private TableViewer treeViewer;

    private TestCaseTestDataLink selectedTestCaseLink;
    private TestCaseTestDataLink initSelectedTestDataLink;
    private TestDataTreeViewerFilter treeViewerFilter;

    public TestDataLinkFinderDialog(Shell parentShell, TestCaseTestDataLink testDataLink,
            List<TestCaseTestDataLink> testDataLinks) {
        super(parentShell);
        this.testDataLinks = testDataLinks;
        this.initSelectedTestDataLink = testDataLink;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_SHELL_TEST_DATA_LINK_BROWSER);
    }

    @Override
    public void create() {
        super.create();
        treeViewer.setInput(testDataLinks);
        registerListeners();
        initSelection();
    }

    private void registerListeners() {
        textSearch.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String searchString = ((Text) e.getSource()).getText();
                treeViewerFilter.setSearchText(searchString);
                treeViewer.refresh();
            }
        });

        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                selectedTestCaseLink = (TestCaseTestDataLink) selection.getFirstElement();
            }

        });

        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                selectedTestCaseLink = (TestCaseTestDataLink) selection.getFirstElement();
                okPressed();
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        Composite compositeSearch = new Composite(container, SWT.NONE);
        GridLayout gl_compositeSearch = new GridLayout(1, false);
        compositeSearch.setLayout(gl_compositeSearch);
        compositeSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        textSearch = new StyledText(compositeSearch, SWT.SINGLE);
        GridData gd_textSearch = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_textSearch.heightHint = 18;
        textSearch.setLayoutData(gd_textSearch);
        
        StyledTextMessage styledTextMessage = new StyledTextMessage(textSearch);
        styledTextMessage.setMessage(StringConstants.DIA_TXT_ENTER_TEXT_TO_SEARCH);

        Composite compositeTree = new Composite(container, SWT.NONE);
        compositeTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeTree.setLayout(new GridLayout(1, false));

        Label lblTestDatasHierarachy = new Label(compositeTree, SWT.NONE);
        lblTestDatasHierarachy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblTestDatasHierarachy.setText(StringConstants.DIA_LBL_TEST_DATA_HIERARCHY);

        treeViewer = new TableViewer(compositeTree, SWT.BORDER);
        Table tree = treeViewer.getTable();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableViewerColumn treeViewerColumn = new TableViewerColumn(treeViewer, SWT.NONE);
        TableColumn trclmnTestDataID = treeViewerColumn.getColumn();
        trclmnTestDataID.setWidth(380);
        trclmnTestDataID.setText(StringConstants.DIA_TREE_VIEWER_COL_ID);
        treeViewerColumn.setLabelProvider(new TestDataIDColumnLabelProvider(textSearch));

        treeViewer.setContentProvider(ArrayContentProvider.getInstance());

        treeViewerFilter = new TestDataTreeViewerFilter();
        treeViewer.setFilters(new ViewerFilter[] { treeViewerFilter });

        return container;
    }

    private void initSelection() {
        if (initSelectedTestDataLink == null) return;

        treeViewer.getTable().forceFocus();
        treeViewer.setSelection(new StructuredSelection(initSelectedTestDataLink));
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
    }

    public TestCaseTestDataLink getSelectedTestDataLink() {
        return (selectedTestCaseLink != null) ? selectedTestCaseLink : null;
    }

}
