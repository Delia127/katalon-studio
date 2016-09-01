package com.kms.katalon.composer.integration.qtest.wizard.page;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseRootSelectionTreeContentProvider;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseRootSelectionTreeLabelProvider;
import com.kms.katalon.composer.integration.qtest.wizard.SetupWizardDialog;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.exception.QTestException;

public class QTestModuleSelectionWizardPage extends AbstractWizardPage {

    private TreeViewer treeViewer;
    private Label lblHeader;
    private Label lblStatus;

    private QTestModule moduleRoot;
    private QTestModule selectedModule;

    private QTestProject fQTestProject;

    private Composite composite;
    private Composite connectingComposite;
    private GifCLabel connectingLabel;
    private InputStream inputStream;
    private Composite headerComposite;

    public QTestModuleSelectionWizardPage() {
    }

    @Override
    public boolean canFlipToNextPage() {
        return selectedModule != null;
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createStepArea(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        headerComposite = new Composite(composite, SWT.NONE);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glHeaderComposite = new GridLayout(1, false);
        glHeaderComposite.marginHeight = 0;
        headerComposite.setLayout(glHeaderComposite);

        lblHeader = new Label(headerComposite, SWT.WRAP);
        lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        lblHeader.setText(StringConstants.DIA_INFO_TEST_CASE_ROOT);

        connectingComposite = new Composite(composite, SWT.NONE);
        connectingComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glConnectingComposite = new GridLayout(2, false);
        glConnectingComposite.marginHeight = 0;
        connectingComposite.setLayout(glConnectingComposite);

        connectingLabel = new GifCLabel(connectingComposite, SWT.NONE);
        connectingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        lblStatus = new Label(connectingComposite, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblStatus.setText(StringConstants.CM_MSG_PLEASE_WAIT);

        Composite compositeTable = new Composite(composite, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeViewer = new TreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn trclmnName = treeViewerColumn.getColumn();
        trclmnName.setText(StringConstants.NAME);

        TreeColumnLayout tableLayout = new TreeColumnLayout();
        tableLayout.setColumnData(trclmnName, new ColumnWeightData(98, 0));
        compositeTable.setLayout(tableLayout);

        treeViewer.setContentProvider(new TestCaseRootSelectionTreeContentProvider());
        treeViewer.setLabelProvider(new TestCaseRootSelectionTreeLabelProvider());
    }

    @Override
    public void setInput(final Map<String, Object> sharedData) {
        final QTestProject qTestProject = (QTestProject) sharedData.get("qTestProject");
        setConnectingCompositeVisible(true);
        Job job = new Job("") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    if (!qTestProject.equals(fQTestProject)) {
                        fQTestProject = qTestProject;
                        selectedModule = null;

                        // Disable next button
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                firePageChanged();
                            }
                        });

                        moduleRoot = QTestIntegrationFolderManager.getModuleRoot(
                                SetupWizardDialog.getCredential(sharedData), qTestProject.getId());

                        moduleRoot = QTestIntegrationFolderManager.updateModuleViaAPI(
                                SetupWizardDialog.getCredential(sharedData), qTestProject.getId(), moduleRoot);
                    }

                    if (treeViewer == null || moduleRoot == null || monitor.isCanceled()) {
                        return Status.OK_STATUS;
                    }

                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                treeViewer.setInput(Arrays.asList(moduleRoot));
                                treeViewer.expandAll();
                                composite.layout(true, true);

                                if (selectedModule != null) {
                                    treeViewer.setSelection(new StructuredSelection(selectedModule));
                                }
                                setConnectingCompositeVisible(false);
                            } catch (IllegalStateException | IllegalArgumentException | SWTException e) {
                                //Display is disposed
                            }
                        }
                    });

                    return Status.OK_STATUS;
                } catch (QTestException | IOException e) {
                    UISynchronizeService.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MultiStatusErrorDialog.showErrorDialog(e, MessageFormat.format(
                                    StringConstants.WZ_P_MODULE_MSG_GET_MODULES_FAILED, qTestProject.getName()), e
                                    .getMessage());
                        }
                    });
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        job.setUser(false);
        job.schedule();
    }

    private void setConnectingCompositeVisible(boolean isConnectingCompositeVisible) {
        if (isConnectingCompositeVisible) {
            try {
                inputStream = ImageConstants.URL_16_LOADING.openStream();
                connectingLabel.setGifImage(inputStream);
            } catch (IOException ex) {
            } finally {
                closeQuietlyWithLog(inputStream);
                inputStream = null;
            }
        } else {
            closeQuietlyWithLog(inputStream);
            inputStream = null;
        }
        connectingComposite.setVisible(isConnectingCompositeVisible);
        ((GridData) connectingComposite.getLayoutData()).exclude = !isConnectingCompositeVisible;
        connectingComposite.getParent().layout(true, true);
    }

    @Override
    public void registerControlModifyListeners() {
        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateTreeViewerSelection();
            }
        });
        
        composite.addDisposeListener(new DisposeListener() {
            
            @Override
            public void widgetDisposed(DisposeEvent e) {
                closeQuietlyWithLog(inputStream);
            }
        });
    }

    private void updateTreeViewerSelection() {
        IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
        selectedModule = (QTestModule) selection.getFirstElement();
        firePageChanged();
    }

    @Override
    public Map<String, Object> storeControlStates() {
        Map<String, Object> sharedData = new HashMap<String, Object>();
        sharedData.put("qTestModule", selectedModule);
        return sharedData;
    }

    @Override
    public String getTitle() {
        return StringConstants.WZ_P_MODULE_TITLE;
    }

}
