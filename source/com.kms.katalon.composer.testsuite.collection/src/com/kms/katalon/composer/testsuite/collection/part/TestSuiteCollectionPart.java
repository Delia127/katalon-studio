package com.kms.katalon.composer.testsuite.collection.part;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.editors.DefaultTableColumnViewerEditor;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.testsuite.collection.constant.ImageConstants;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.part.job.TestSuiteCollectionBuilderJob;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.composer.testsuite.collection.part.provider.TestSuiteRunConfigLabelProvider;
import com.kms.katalon.composer.testsuite.collection.part.provider.ToolbarItemListener;
import com.kms.katalon.composer.testsuite.collection.part.support.RunConfigurationChooserEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.support.RunEnabledEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.support.TestSuiteIdEditingSupport;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteCollectionPart extends EventServiceAdapter implements TableViewerProvider, IComposerPart {

    @Inject
    private IEventBroker eventBroker;

    private MPart mpart;

    private TestSuiteCollectionEntity originalTestSuite;

    private TestSuiteCollectionEntity cloneTestSuite;

    private CTableViewer tableViewer;

    private TableColumn tblclmnRun;

    private long lastModified;

    @PostConstruct
    public void initialize(Composite parent, MPart mpart) {
        this.mpart = mpart;
        registerEventListeners();

        createControls(parent);
        registerControlModifyListeners();

        updateTestSuiteCollections((TestSuiteCollectionEntity) mpart.getObject());
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, this);
        eventBroker.subscribe(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, this);
        eventBroker.subscribe(EventConstants.TEST_SUITE_COLLECTION_UPDATED, this);
        eventBroker.subscribe(EventConstants.TEST_SUITE_UPDATED, this);
    }

    private IFileInfo getFileInfo(TestSuiteCollectionEntity testSuiteCollection) {
        return EFS.getLocalFileSystem().fromLocalFile(testSuiteCollection.toFile()).fetchInfo();
    }

    private void updateTestSuiteCollections(TestSuiteCollectionEntity testSuiteCollection) {
        if (testSuiteCollection == null) {
            close();
        }
        originalTestSuite = testSuiteCollection;
        cloneTestSuite = (TestSuiteCollectionEntity) originalTestSuite.clone();
        cloneTestSuite.reuseWrappers(originalTestSuite);

        mpart.setElementId(EntityPartUtil.getTestSuiteCollectionPartId(cloneTestSuite.getId()));
        mpart.setLabel(cloneTestSuite.getName());
        updateInput();

        lastModified = getFileInfo(originalTestSuite).getLastModified();
    }

    private void updateInput() {
        tableViewer.setInput(cloneTestSuite.getTestSuiteRunConfigurations());
        updateRunColumn();
    }

    private void registerControlModifyListeners() {
        tblclmnRun.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isAllRunEnabled = !cloneTestSuite.isAllRunEnabled();
                cloneTestSuite.enableRunForAll(isAllRunEnabled);
                updateRunColumnHeader(isAllRunEnabled);
                tableViewer.refresh();
                markDirty();
            }
        });

        tableViewer.getTable().addListener(SWT.EraseItem, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                checkUpdated();
            }
        });

    }

    private void createControls(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        createToolbarComposite(parent);

        createTableComposite(parent);
    }

    private void createToolbarComposite(Composite parent) {
        Composite toolbarComposite = new Composite(parent, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(1, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        SelectionAdapter selectionListener = new ToolbarItemListener(this);
        createToolItemWithSelectionListener(toolBar, StringConstants.ADD, ImageConstants.IMG_24_ADD, selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.REMOVE, ImageConstants.IMG_24_REMOVE,
                selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.UP, ImageConstants.IMG_24_UP, selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.DOWN, ImageConstants.IMG_24_DOWN,
                selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.PA_ACTION_EXECUTE_TEST_SUITE_COLLECTION,
                ImageConstants.IMG_24_EXECUTE, selectionListener);
    }

    private void createToolItemWithSelectionListener(ToolBar toolbar, String name, Image image,
            SelectionAdapter selectionListener) {
        ToolItem toolItem = new ToolItem(toolbar, SWT.NONE);
        toolItem.setText(name);
        toolItem.setImage(image);
        toolItem.addSelectionListener(selectionListener);
    }

    private void createTableComposite(Composite parent) {
        Composite testSuiteTableComposite = new Composite(parent, SWT.NONE);
        testSuiteTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumnLayout tableLayout = new TableColumnLayout();
        testSuiteTableComposite.setLayout(tableLayout);

        tableViewer = new CTableViewer(testSuiteTableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table testSuiteWrapperTable = tableViewer.getTable();
        testSuiteWrapperTable.setLinesVisible(true);
        testSuiteWrapperTable.setHeaderVisible(true);

        TableViewerColumn tbvcNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNo = tbvcNo.getColumn();
        tblclmnNo.setText(StringConstants.NO_);
        tbvcNo.setLabelProvider(new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.NO_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnNo, new ColumnWeightData(1, 60));

        TableViewerColumn tbvcId = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnId = tbvcId.getColumn();
        tblclmnId.setText(StringConstants.ID);
        tbvcId.setEditingSupport(new TestSuiteIdEditingSupport(this));
        tbvcId.setLabelProvider(new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.ID_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnId, new ColumnWeightData(50, 300));

        TableViewerColumn tbvcRunWith = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnEnviroment = tbvcRunWith.getColumn();
        tblclmnEnviroment.setText(StringConstants.PA_TABLE_COLUMN_RUN_WITH);
        tbvcRunWith.setEditingSupport(new RunConfigurationChooserEditingSupport(this));
        tbvcRunWith.setLabelProvider(new TestSuiteRunConfigLabelProvider(this,
                TestSuiteRunConfigLabelProvider.RUN_WITH_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnEnviroment, new ColumnWeightData(20, 150));

        TableViewerColumn tbvcRun = new TableViewerColumn(tableViewer, SWT.NONE);
        tblclmnRun = tbvcRun.getColumn();
        tblclmnRun.setText(StringConstants.RUN);
        tbvcRun.setEditingSupport(new RunEnabledEditingSupport(this));
        tbvcRun.setLabelProvider(new TestSuiteRunConfigLabelProvider(this,
                TestSuiteRunConfigLabelProvider.RUN_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnRun, new ColumnWeightData(10, 70));

        tableViewer.setContentProvider(new ArrayContentProvider());
        DefaultTableColumnViewerEditor.create(tableViewer);

        tableViewer.enableTooltipSupport();
    }

    @Override
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    @Override
    public List<TestSuiteRunConfiguration> getTableItems() {
        return cloneTestSuite.getTestSuiteRunConfigurations();
    }

    @Override
    public void markDirty() {
        mpart.setDirty(true);
    }

    @Override
    public boolean containsTestSuite(TestSuiteEntity testSuite) {
        for (TestSuiteRunConfiguration wrapper : getTableItems()) {
            if (ObjectUtils.equals(testSuite, wrapper.getConfiguration())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateRunColumn() {
        updateRunColumnHeader(cloneTestSuite.isAllRunEnabled());
    }

    private void updateRunColumnHeader(boolean runAllEnabled) {
        if (runAllEnabled) {
            tblclmnRun.setImage(ImageConstants.IMG_16_CHECKED);
            return;
        }
        tblclmnRun.setImage(ImageConstants.IMG_16_UNCHECKED);
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.EXPLORER_RENAMED_SELECTED_ITEM: {
                Object[] objects = getObjects(event);
                if (objects == null || objects.length != 2) {
                    return;
                }

                if (ObjectUtils.equals(originalTestSuite.getIdForDisplay(), objects[1])) {
                    updateTestSuiteCollections(originalTestSuite);
                }
                break;
            }
            case EventConstants.EXPLORER_DELETED_SELECTED_ITEM: {
                Object object = getObject(event);
                if (!(object instanceof String)) {
                    return;
                }
                String folderId = (String) object;
                if (originalTestSuite.getIdForDisplay().startsWith(folderId)) {
                    close();
                }
                break;
            }
            case EventConstants.TEST_SUITE_UPDATED:
            case EventConstants.TEST_SUITE_COLLECTION_UPDATED: {
                Object[] objects = getObjects(event);
                if (objects == null || objects.length != 2) {
                    return;
                }
                if (originalTestSuite.equals(objects[1])) {
                    updateTestSuiteCollections(originalTestSuite);
                }
                break;
            }
        }
    }

    @PreDestroy
    public void close() {
        eventBroker.unsubscribe(this);
        EntityPartUtil.closePart(originalTestSuite);
    }

    @Focus
    public void focus() {
        checkUpdated();
    }

    private void checkUpdated() {
        if (lastModified != getFileInfo(originalTestSuite).getLastModified()) {
            updateTestSuiteCollections(originalTestSuite);
        }
    }

    @Persist
    public void save() {
        TestSuiteCollectionEntity backup = (TestSuiteCollectionEntity) originalTestSuite.clone();
        backup.reuseWrappers(originalTestSuite);
        originalTestSuite.reuseWrappers(cloneTestSuite);
        try {
            TestSuiteCollectionController.getInstance().updateTestSuiteCollection(originalTestSuite);
            updateTestSuiteCollections(originalTestSuite);
            mpart.setDirty(false);
        } catch (DALException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_MSG_UNABLE_TO_UPDATE_TEST_SUITE_COLLECTION,
                    e.getMessage());
            originalTestSuite.reuseWrappers(backup);
        }
    }

    @Override
    public void executeTestRun(final ToolItem toolItem) {
        if (mpart.isDirty()) {
            MessageDialog.openInformation(null, StringConstants.INFO, "Please save before executing.");
            return;
        }
        AbstractExecutionHandler.openConsoleLog();
        toolItem.setEnabled(false);

        TestSuiteCollectionBuilderJob job = new TestSuiteCollectionBuilderJob(originalTestSuite);
        job.schedule();
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        toolItem.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    public String getEntityId() {
        return originalTestSuite.getIdForDisplay();
    }

}
