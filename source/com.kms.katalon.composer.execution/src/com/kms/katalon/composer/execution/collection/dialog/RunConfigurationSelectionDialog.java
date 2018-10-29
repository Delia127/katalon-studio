package com.kms.katalon.composer.execution.collection.dialog;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionConfigurationProvider;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionGroup;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
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
                if (!(selection.getFirstElement() instanceof TestExecutionConfigurationProvider)) {
                    getButton(OK).setEnabled(false);
                    testRunConfigurationEntry = null;
                    return;
                }
                getButton(OK).setEnabled(true);
                testRunConfigurationEntry = ((TestExecutionConfigurationProvider) selection.getFirstElement())
                        .toConfigurationEntity(testRunConfigurationEntry);
            }
        });

        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                if (selection.getFirstElement() instanceof TestExecutionConfigurationProvider) {
                    okPressed();
                }
            }
        });
    }

    @Override
    protected void setInput() {
        List<TestExecutionGroup> groups = new ArrayList<>();
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        for (TestExecutionGroup grp : TestExecutionGroupCollector.getInstance().getGroupAsArray()) {
            if (grp.shouldBeDisplayed(project)) {
                groups.add(grp);
            }
        }
        treeViewer.setInput(groups.toArray(new TestExecutionGroup[groups.size()]));
        treeViewer.expandAll();
        initializeBounds();
        if (testRunConfigurationEntry == null) {
            return;
        }
        TestExecutionGroup group = TestExecutionGroupCollector.getInstance()
                .getGroup(testRunConfigurationEntry.getGroupName());
        TestExecutionConfigurationProvider executionProvider = TestExecutionGroupCollector.getInstance()
                .getExecutionProvider(testRunConfigurationEntry);
        if (group == null || executionProvider == null) {
            return;
        }
        TreePath treePath = new TreePath(new Object[] { group, executionProvider });
        treeViewer.setSelection(new TreeSelection(treePath));
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 400);
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
        return ComposerExecutionMessageConstants.DIA_TITLE_RUN_CONFIG_SELECTION;
    }

    public RunConfigurationDescription getSelectedConfiguration() {
        return testRunConfigurationEntry;
    }
}
