package com.kms.katalon.composer.testsuite.collection.part.dialog;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.execution.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionEntryItem;
import com.kms.katalon.composer.testsuite.collection.execution.provider.TestExecutionGroup;
import com.kms.katalon.composer.testsuite.collection.part.provider.TestExecutionItemLabelProvider;
import com.kms.katalon.composer.testsuite.collection.part.provider.TestExecutionItemTreeContentProvider;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;

public class RunConfigurationSelectionDialog extends AbstractDialog {

    private TreeViewer treeViewer;

    private RunConfigurationDescription testRunConfigurationEntry;

    public RunConfigurationSelectionDialog(Shell parentShell, RunConfigurationDescription preConfiguration) {
        super(parentShell);
        testRunConfigurationEntry = preConfiguration;
    }

    @Override
    protected void registerControlModifyListeners() {
        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!(event.getSelection() instanceof StructuredSelection)) {
                    return;
                }

                StructuredSelection selection = (StructuredSelection) event.getSelection();
                if (!(selection.getFirstElement() instanceof TestExecutionEntryItem)) {
                    getButton(OK).setEnabled(false);
                    testRunConfigurationEntry = null;
                    return;
                }
                getButton(OK).setEnabled(true);
                testRunConfigurationEntry = ((TestExecutionEntryItem) selection.getFirstElement()).toConfigurationEntity();
            }
        });

        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                if (selection.getFirstElement() instanceof TestExecutionEntryItem) {
                    okPressed();
                }
            }
        });
    }

    @Override
    protected void setInput() {
        treeViewer.setInput(TestExecutionGroupCollector.getInstance().getGroupAsArray());
        treeViewer.expandAll();
        initializeBounds();

        if (testRunConfigurationEntry != null) {
            TestExecutionGroup group = TestExecutionGroupCollector.getInstance().getGroup(testRunConfigurationEntry.getGroupName());
            TestExecutionConfigurationProvider executionProvider = TestExecutionGroupCollector.getInstance().getExecutionProvider(
                    testRunConfigurationEntry);

            TreePath treePath = new TreePath(new Object[] { group, executionProvider });
            treeViewer.setSelection(new TreeSelection(treePath));
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        treeViewer = new TreeViewer(parent, SWT.BORDER);

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnRunConfiguration = treeViewerColumn.getColumn();
        trclmnRunConfiguration.setWidth(300);
        treeViewer.setContentProvider(new TestExecutionItemTreeContentProvider());
        treeViewer.setLabelProvider(new TestExecutionItemLabelProvider());
        return treeViewer.getTree();
    }

    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_RUN_CONFIG_SELECTION;
    }

    public RunConfigurationDescription getSelectedConfiguration() {
        return testRunConfigurationEntry;
    }
}
