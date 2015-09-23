package com.kms.katalon.composer.testsuite.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.filters.TestDataTreeViewerFilter;
import com.kms.katalon.composer.testsuite.providers.TestDataIDColumnLabelProvider;
import com.kms.katalon.composer.testsuite.providers.TestDataTreeContentProvider;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataLinkFinderDialog extends Dialog {
    private Text textSearch;
    private List<TestCaseTestDataLink> testDataLinks;
    private TreeViewer treeViewer;

    private TestDataLinkTreeNode selectedTreeNode;
    private TestCaseTestDataLink initSelectedTestDataLink;
    private TestDataTreeContentProvider contentProvider;
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
                treeViewer.expandAll();
            }
        });

        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                selectedTreeNode = (TestDataLinkTreeNode) selection.getFirstElement();
            }

        });

        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
                selectedTreeNode = (TestDataLinkTreeNode) selection.getFirstElement();
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

        textSearch = new Text(compositeSearch, SWT.BORDER);
        GridData gd_textSearch = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_textSearch.heightHint = 18;
        textSearch.setLayoutData(gd_textSearch);
        textSearch.setMessage(StringConstants.DIA_TXT_ENTER_TEXT_TO_SEARCH);

        Composite compositeTree = new Composite(container, SWT.NONE);
        compositeTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeTree.setLayout(new GridLayout(1, false));

        Label lblTestDatasHierarachy = new Label(compositeTree, SWT.NONE);
        lblTestDatasHierarachy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblTestDatasHierarachy.setText(StringConstants.DIA_LBL_TEST_DATA_HIERARCHY);

        treeViewer = new TreeViewer(compositeTree, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnTestDataID = treeViewerColumn.getColumn();
        trclmnTestDataID.setWidth(380);
        trclmnTestDataID.setText(StringConstants.DIA_TREE_VIEWER_COL_ID);
        treeViewerColumn.setLabelProvider(new TestDataIDColumnLabelProvider(textSearch));

        contentProvider = new TestDataTreeContentProvider();
        treeViewer.setContentProvider(contentProvider);

        treeViewerFilter = new TestDataTreeViewerFilter();
        treeViewer.setFilters(new ViewerFilter[] { treeViewerFilter });

        return container;
    }

    private void initSelection() {
        if (initSelectedTestDataLink == null)
            return;

        TestDataLinkTreeNode initSelectedTreeNode = contentProvider.getTreeNode(initSelectedTestDataLink);

        if (initSelectedTreeNode != null) {
            treeViewer.getTree().forceFocus();
            treeViewer.setSelection(new StructuredSelection(initSelectedTreeNode));
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }
    
    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    public TestCaseTestDataLink getSelectedTestDataLink() {
        return (selectedTreeNode != null) ? selectedTreeNode.getTestDataLink() : null;
    }

}
