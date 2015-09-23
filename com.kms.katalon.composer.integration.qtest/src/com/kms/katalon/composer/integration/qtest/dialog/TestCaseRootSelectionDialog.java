package com.kms.katalon.composer.integration.qtest.dialog;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseRootSelectionTreeContentProvider;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseRootSelectionTreeLabelProvider;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;

public class TestCaseRootSelectionDialog extends Dialog {
    private Composite container;
    private QTestModule moduleRoot;
    private TreeViewer treeViewer;

    private QTestModule selectedModule;
    private Label lblHeader;
    private boolean updateNeeded;

    private boolean exit;
    private String projectDir;

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    private QTestProject qTestProject;
    private Label lblStatus;

    public void setQTestProject(QTestProject qTestProject) {
        this.qTestProject = qTestProject;
    }

    public TestCaseRootSelectionDialog(Shell parentShell, QTestModule module, boolean updateNeeded) {
        super(parentShell);
        this.moduleRoot = module;
        this.updateNeeded = updateNeeded;
        exit = false;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }

    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        lblHeader = new Label(composite, SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblHeader.setText("System needs a specific module on qTest for the integration. " + "Please choose one. "
                + "All test cases and folders will be uploaded or created inside your selection.");

        lblStatus = new Label(composite, SWT.NONE);
        lblStatus.setText("Please wait...");

        Composite compositeTable = new Composite(container, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeViewer = new TreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
        Tree tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnName = treeViewerColumn.getColumn();
        trclmnName.setText("Name");

        TreeColumnLayout tableLayout = new TreeColumnLayout();
        tableLayout.setColumnData(trclmnName, new ColumnWeightData(98, 0));
        compositeTable.setLayout(tableLayout);

        treeViewer.setContentProvider(new TestCaseRootSelectionTreeContentProvider());
        treeViewer.setLabelProvider(new TestCaseRootSelectionTreeLabelProvider());
        return container;
    }

    @Override
    public void create() {
        super.create();
        setInput();
        addModifyListeners();
    }

    private void addModifyListeners() {
        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                validate();
            }
        });

        getShell().addListener(SWT.Activate, new Listener() {

            public void handleEvent(Event event) {
                if (exit) {
                    close();
                }
            }
        });

    }

    private void validate() {
        IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
        if (selection == null || selection.isEmpty()) {
            getButton(OK).setEnabled(false);
        } else {
            selectedModule = (QTestModule) selection.getFirstElement();
            getButton(OK).setEnabled(true);
        }
    }

    private void setInput() {
        if (updateNeeded) {
            getButton(OK).setEnabled(false);
            getButton(CANCEL).setEnabled(false);
            Display display = treeViewer.getControl().getDisplay();
            display.asyncExec(new Runnable() {

                @Override
                public void run() {
                    try {
                        moduleRoot = QTestIntegrationFolderManager.updateModuleViaAPI(projectDir, qTestProject.getId(),
                                moduleRoot);

                        treeViewer.setInput(Arrays.asList(moduleRoot));
                        treeViewer.expandAll();
                        lblStatus.dispose();
                        container.layout(true);
                        validate();

                        getButton(OK).setEnabled(true);
                        getButton(CANCEL).setEnabled(true);
                    } catch (Exception e) {
                        getShell().setVisible(false);
                        MultiStatusErrorDialog.showErrorDialog(e,
                                "Unable to load test suite's parent from qTest server.", e.getClass().getSimpleName());

                        exit = true;
                    }
                }
            });
        } else {
            treeViewer.setInput(Arrays.asList(moduleRoot));
            treeViewer.expandAll();
            validate();
        }
    }

    public QTestModule getSelectedModule() {
        return selectedModule;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Test case root selection");
    }

}
