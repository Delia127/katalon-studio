package com.kms.katalon.composer.testsuite.collection.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.control.CMenu;
import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.editors.DefaultTableColumnViewerEditor;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.impl.util.MenuUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testsuite.collection.constant.ComposerTestsuiteCollectionMessageConstants;
import com.kms.katalon.composer.testsuite.collection.constant.ImageConstants;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.listeners.TestSuiteTableDragListener;
import com.kms.katalon.composer.testsuite.collection.listeners.TestSuiteTableDropListener;
import com.kms.katalon.composer.testsuite.collection.part.job.TestSuiteCollectionBuilderJob;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.composer.testsuite.collection.part.provider.TestSuiteRunConfigLabelProvider;
import com.kms.katalon.composer.testsuite.collection.part.provider.ToolbarItemListener;
import com.kms.katalon.composer.testsuite.collection.part.provider.ToolbarItemListener.ActionId;
import com.kms.katalon.composer.testsuite.collection.part.support.ExecutionProfileEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.support.RunConfigurationChooserEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.support.RunConfigurationDataEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.support.RunEnabledEditingSupport;
import com.kms.katalon.composer.testsuite.collection.part.support.TestSuiteIdEditingSupport;
import com.kms.katalon.composer.testsuite.collection.transfer.TestSuiteRunConfigurationTransfer;
import com.kms.katalon.composer.testsuite.collection.view.TestSuiteCollectionViewFactory;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity.ExecutionMode;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteCollectionPart extends EventServiceAdapter implements TableViewerProvider, IComposerPartEvent {
    private static final int MINIMUM_COMPOSITE_SIZE = 300;

    private static final String HK_NEW = "M1+N";

    private static final String HK_DEL = "Delete";

    private static final String HK_MOVE_ITEMS_UP = "M1+ARROW_UP";

    private static final String HK_MOVE_ITEMS_DOWN = "M1+ARROW_DOWN";

    private static final String HK_EXECUTE = "M1+E";

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private MPart mpart;

    private TestSuiteCollectionEntity originalTestSuite;

    private TestSuiteCollectionEntity cloneTestSuite;

    private CTableViewer tableViewer;

    private TableColumn tblclmnRun;

    private long lastModified;

    private ToolItem toolItemExecute;

    private ImageButton btnExpandExecutionInformation;

    private ToolbarItemListener selectionListener;

    private Composite toolbarComposite, testSuiteTableComposite, compositeExecution, compositeExecutionHeader,
            compositeExecutionInformation;

    private boolean isExecutionInfoCompositeExpanded;

    private Button btnSequential, btnParallel;

    private Label lblExecutionInformation;

    private CMenu menu;

    private Callable<Boolean> enableWhenItemSelected;

    private Listener layoutExecutionInformationCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isExecutionInfoCompositeExpanded = !isExecutionInfoCompositeExpanded;
            layoutExecutionInfo();
        }
    };

    private Spinner spnMaxConcurrentThread;

    private Composite customViews;

    private Composite parent;

    private Map<String, ExpandableTestSuiteCollectionComposite> viewCompositeMap = new HashMap<>();

    @PostConstruct
    public void initialize(Composite parent, MPart mpart) {
        this.mpart = mpart;
        this.isExecutionInfoCompositeExpanded = true;
        this.parent = parent;
        registerEventListeners();

        new HelpToolBarForMPart(mpart, DocumentationMessageConstants.TEST_SUITE_COLLECTION);
        createControls();
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
        cloneTestSuite = (TestSuiteCollectionEntity) SerializationUtils.clone(originalTestSuite);
        cloneTestSuite.reuseWrappers(originalTestSuite);

        mpart.setElementId(EntityPartUtil.getTestSuiteCollectionPartId(cloneTestSuite.getId()));
        mpart.setLabel(cloneTestSuite.getName());
        updateInput();

        lastModified = getFileInfo(originalTestSuite).getLastModified();

        createViewsFromViewFactory();
    }

    private void updateInput() {
        tableViewer.setInput(cloneTestSuite.getTestSuiteRunConfigurations());
        updateExecutionInfoInput();
        updateRunColumn();

        mpart.setDirty(false);
    }

    private void updateExecutionInfoInput() {
        setStatusForRadioExecutionMode(cloneTestSuite.getExecutionMode());
        spnMaxConcurrentThread.setSelection(cloneTestSuite.getMaxConcurrentInstances());
    }

    private void setStatusForRadioExecutionMode(ExecutionMode mode) {
        if (mode == ExecutionMode.PARALLEL) {
            btnParallel.setSelection(true);
            btnSequential.setSelection(false);
            spnMaxConcurrentThread.setEnabled(true);
        } else {
            btnSequential.setSelection(true);
            btnParallel.setSelection(false);
            spnMaxConcurrentThread.setEnabled(false);
        }
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

        btnExpandExecutionInformation.addListener(SWT.MouseDown, layoutExecutionInformationCompositeListener);

        lblExecutionInformation.addListener(SWT.MouseDown, layoutExecutionInformationCompositeListener);

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() == btnSequential && cloneTestSuite.getExecutionMode() != ExecutionMode.SEQUENTIAL) {
                    cloneTestSuite.setExecutionMode(ExecutionMode.SEQUENTIAL);
                    setStatusForRadioExecutionMode(cloneTestSuite.getExecutionMode());
                    markDirty();
                    return;
                }
                if (e.getSource() == btnParallel && cloneTestSuite.getExecutionMode() != ExecutionMode.PARALLEL) {
                    cloneTestSuite.setExecutionMode(ExecutionMode.PARALLEL);
                    setStatusForRadioExecutionMode(cloneTestSuite.getExecutionMode());
                    markDirty();
                }
            }
        };

        btnSequential.addSelectionListener(selectionListener);
        btnParallel.addSelectionListener(selectionListener);

        spnMaxConcurrentThread.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                cloneTestSuite.setMaxConcurrentInstances(spnMaxConcurrentThread.getSelection());
                markDirty();
            }
        });
    }

    private void createControls() {
        parent.setLayout(new GridLayout(1, false));

        createGeneralInformationComposite();

        createCustomViewComposite();

        createToolbarComposite();

        createTableComposite();
    }

    private void createCustomViewComposite() {
        customViews = new Composite(parent, SWT.NONE);
        customViews.setBackground(ColorUtil.getCompositeBackgroundColor());
        GridLayout glCustomViews = new GridLayout(1, true);
        glCustomViews.marginHeight = 0;
        glCustomViews.marginWidth = 0;
        customViews.setLayout(glCustomViews);
        customViews.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    }

    private void createViewsFromViewFactory() {

        TestSuiteCollectionViewFactory.getInstance().getSortedBuilders().forEach(entryBuilder -> {
            String name = entryBuilder.getName();
            if (viewCompositeMap.get(name) == null) {
                AbstractTestSuiteCollectionUIDescriptionView descView = entryBuilder.getView(getTestSuiteCollection(),
                        getMPart(), getMPart());
                ExpandableTestSuiteCollectionComposite view = new ExpandableTestSuiteCollectionComposite(customViews,
                        name, descView);
                viewCompositeMap.put(name, view);
            }
        });
    }

    private void createGeneralInformationComposite() {
        compositeExecution = new Composite(parent, SWT.NONE);
        compositeExecution.setBackground(ColorUtil.getCompositeBackgroundColor());
        compositeExecution.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeInformation = new GridLayout(1, false);
        glCompositeInformation.marginWidth = 0;
        glCompositeInformation.marginHeight = 0;
        glCompositeInformation.verticalSpacing = 0;
        compositeExecution.setLayout(glCompositeInformation);

        compositeExecutionHeader = new Composite(compositeExecution, SWT.NONE);
        GridLayout glCompositeInformationHeader = new GridLayout(2, false);
        glCompositeInformationHeader.marginWidth = 0;
        glCompositeInformationHeader.marginHeight = 0;
        compositeExecutionHeader.setLayout(glCompositeInformationHeader);
        compositeExecutionHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeExecutionHeader.setCursor(compositeExecutionHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandExecutionInformation = new ImageButton(compositeExecutionHeader, SWT.NONE);
        btnExpandExecutionInformation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        redrawBtnExpandGeneralInfo();

        lblExecutionInformation = new Label(compositeExecutionHeader, SWT.NONE);
        lblExecutionInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblExecutionInformation.setFont(JFaceResources.getFontRegistry().getBold("")); //$NON-NLS-1$
        lblExecutionInformation.setText(ComposerTestsuiteCollectionMessageConstants.LBL_EXECUTION_INFO);

        compositeExecutionInformation = new Composite(compositeExecution, SWT.NONE);
        GridLayout glCompositeInformationDetails = new GridLayout(1, true);
        glCompositeInformationDetails.marginLeft = 45;
        glCompositeInformationDetails.horizontalSpacing = 40;
        compositeExecutionInformation.setLayout(glCompositeInformationDetails);
        compositeExecutionInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        Composite compositeTestSuiteCollectionExecutionMode = new Composite(compositeExecutionInformation, SWT.NONE);
        GridData gdCompositeTestSuiteCollectionExecutionMode = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdCompositeTestSuiteCollectionExecutionMode.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeTestSuiteCollectionExecutionMode.setLayoutData(gdCompositeTestSuiteCollectionExecutionMode);
        GridLayout glCompositeTestSuiteCollectionExecutionMode = new GridLayout(2, false);
        glCompositeTestSuiteCollectionExecutionMode.verticalSpacing = 10;
        compositeTestSuiteCollectionExecutionMode.setLayout(glCompositeTestSuiteCollectionExecutionMode);

        Label lblTestSuiteCollectionExecutionMode = new Label(compositeTestSuiteCollectionExecutionMode, SWT.NONE);
        GridData gdLblTestSuiteCollectionExecutionMode = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gdLblTestSuiteCollectionExecutionMode.widthHint = 100;
        gdLblTestSuiteCollectionExecutionMode.verticalIndent = 5;
        lblTestSuiteCollectionExecutionMode.setLayoutData(gdLblTestSuiteCollectionExecutionMode);
        lblTestSuiteCollectionExecutionMode
                .setText(ComposerTestsuiteCollectionMessageConstants.LBL_TEST_SUTE_COLLECTION_EXECUTION_MODE);

        Composite compositeExecutionRadioGroup = new Composite(compositeTestSuiteCollectionExecutionMode, SWT.NONE);
        compositeExecutionRadioGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        compositeExecutionRadioGroup.setLayout(new GridLayout());

        btnSequential = new Button(compositeExecutionRadioGroup, SWT.RADIO);
        btnSequential.setText(
                ComposerTestsuiteCollectionMessageConstants.BTN_TEST_SUITE_COLLECTION_EXECUTION_MODE_SEQUENTIAL);
        btnSequential.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        Composite cpstParallel = new Composite(compositeExecutionRadioGroup, SWT.NONE);
        GridLayout glParallel = new GridLayout(3, false);
        glParallel.marginWidth = 0;
        glParallel.marginHeight = 0;
        cpstParallel.setLayout(glParallel);
        cpstParallel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        btnParallel = new Button(cpstParallel, SWT.RADIO);
        btnParallel
                .setText(ComposerTestsuiteCollectionMessageConstants.BTN_TEST_SUITE_COLLECTION_EXECUTION_MODE_PARALLEL);
        Label lblMaxConcurrentThread = new Label(cpstParallel, SWT.NONE);
        lblMaxConcurrentThread.setText("Max concurrent instances: ");
        GridData gdLblMaxInstances = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdLblMaxInstances.horizontalIndent = 20;
        lblMaxConcurrentThread.setLayoutData(gdLblMaxInstances);

        spnMaxConcurrentThread = new Spinner(cpstParallel, SWT.BORDER);
        spnMaxConcurrentThread.setMinimum(TestSuiteCollectionEntity.MIN_CONCURRENT_INSTANCES);
        spnMaxConcurrentThread.setMaximum(TestSuiteCollectionEntity.MAX_CONCURRENT_INSTANCES);

        GridData gdTxtConcurrentThread = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdTxtConcurrentThread.widthHint = 70;
        spnMaxConcurrentThread.setLayoutData(gdTxtConcurrentThread);
    }

    private void layoutExecutionInfo() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                compositeExecutionInformation.setVisible(isExecutionInfoCompositeExpanded);
                if (!isExecutionInfoCompositeExpanded) {
                    ((GridData) compositeExecutionInformation.getLayoutData()).exclude = true;
                    compositeExecution.setSize(compositeExecution.getSize().x, compositeExecution.getSize().y
                            - testSuiteTableComposite.getSize().y - toolbarComposite.getSize().y);
                } else {
                    ((GridData) compositeExecutionInformation.getLayoutData()).exclude = false;
                }
                compositeExecution.layout(true, true);
                compositeExecution.getParent().layout();
                redrawBtnExpandGeneralInfo();
            }
        });
    }

    private void redrawBtnExpandGeneralInfo() {
        btnExpandExecutionInformation.getParent().setRedraw(false);
        btnExpandExecutionInformation.setImage(isExecutionInfoCompositeExpanded ? ImageConstants.IMG_16_ARROW_DOWN
                : ImageConstants.IMG_16_ARROW_RIGHT);
        btnExpandExecutionInformation.getParent().setRedraw(true);
    }

    private void createToolbarComposite() {
        toolbarComposite = new Composite(parent, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(1, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        selectionListener = new ToolbarItemListener(this);
        createToolItemWithSelectionListener(toolBar, StringConstants.ADD, ImageConstants.IMG_16_ADD, selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.REMOVE, ImageConstants.IMG_16_REMOVE,
                selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.UP, ImageConstants.IMG_16_UP, selectionListener);
        createToolItemWithSelectionListener(toolBar, StringConstants.DOWN, ImageConstants.IMG_16_DOWN,
                selectionListener);
        toolItemExecute = createToolItemWithSelectionListener(toolBar,
                StringConstants.PA_ACTION_EXECUTE_TEST_SUITE_COLLECTION, ImageConstants.IMG_16_EXECUTE,
                selectionListener);
    }

    private ToolItem createToolItemWithSelectionListener(ToolBar toolbar, String name, Image image,
            SelectionAdapter selectionListener) {
        ToolItem toolItem = new ToolItem(toolbar, SWT.NONE);
        toolItem.setText(name);
        toolItem.setImage(image);
        toolItem.addSelectionListener(selectionListener);
        return toolItem;
    }

    private void createTableComposite() {
        testSuiteTableComposite = new Composite(parent, SWT.NONE);
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
        tbvcNo.setLabelProvider(
                new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.NO_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnNo, new ColumnWeightData(1, 50));

        TableViewerColumn tbvcId = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnId = tbvcId.getColumn();
        tblclmnId.setText(StringConstants.ID);
        tbvcId.setEditingSupport(new TestSuiteIdEditingSupport(this));
        tbvcId.setLabelProvider(
                new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.ID_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnId, new ColumnWeightData(50, 300));

        TableViewerColumn tbvcRunWith = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnEnviroment = tbvcRunWith.getColumn();
        tblclmnEnviroment.setText(StringConstants.PA_TABLE_COLUMN_RUN_WITH);
        tbvcRunWith.setEditingSupport(new RunConfigurationChooserEditingSupport(this));
        tbvcRunWith.setLabelProvider(
                new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.RUN_WITH_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnEnviroment, new ColumnWeightData(20, 70));

        TableViewerColumn tbvcRunWithData = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnRunWithData = tbvcRunWithData.getColumn();
        tblclmnRunWithData.setText(ComposerTestsuiteCollectionMessageConstants.PA_TABLE_COLUMN_RUN_CONFIGURATION_DATA);
        tbvcRunWithData.setEditingSupport(new RunConfigurationDataEditingSupport(this));
        tbvcRunWithData.setLabelProvider(
                new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.RUN_WITH_DATA_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnRunWithData, new ColumnWeightData(40, 200));

        TableViewerColumn tbvcProfile = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnProfile = tbvcProfile.getColumn();
        tblclmnProfile.setText(ComposerTestsuiteCollectionMessageConstants.PA_TABLE_COLUMN_PROFILE);
        tbvcProfile.setLabelProvider(
                new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.PROFILE_COLUMN_IDX));
        tbvcProfile.setEditingSupport(new ExecutionProfileEditingSupport(this));
        tableLayout.setColumnData(tblclmnProfile, new ColumnWeightData(20, 100));

        TableViewerColumn tbvcRun = new TableViewerColumn(tableViewer, SWT.NONE);
        tblclmnRun = tbvcRun.getColumn();
        tblclmnRun.setText(StringConstants.RUN);
        tbvcRun.setEditingSupport(new RunEnabledEditingSupport(this));
        tbvcRun.setLabelProvider(
                new TestSuiteRunConfigLabelProvider(this, TestSuiteRunConfigLabelProvider.RUN_COLUMN_IDX));
        tableLayout.setColumnData(tblclmnRun, new ColumnWeightData(10, 70));

        tableViewer.setContentProvider(new ArrayContentProvider());
        DefaultTableColumnViewerEditor.create(tableViewer);

        tableViewer.enableTooltipSupport();

        createTableMenu(tableViewer.getTable());

        setTableViewerSelection(tableViewer);
        ColumnViewerUtil.setTableActivation(tableViewer);
        hookDropTestSuiteEvent();
        hookDragTestSuiteEvent();

        // KAT-3580: hide the "Run With" and "Run Configuration" columns in test
        // suite collection view
        // for API projects
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (project.getType() == ProjectType.WEBSERVICE) {
            tableLayout.setColumnData(tblclmnEnviroment, new ColumnWeightData(0, 0));
            tableLayout.setColumnData(tblclmnRunWithData, new ColumnWeightData(0, 0));
        }
    }

    private void hookDragTestSuiteEvent() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY;

        DragSource dragSource = new DragSource(tableViewer.getTable(), operations);
        dragSource.setTransfer(new Transfer[] { new TestSuiteRunConfigurationTransfer() });
        dragSource.addDragListener(new TestSuiteTableDragListener(this));

    }

    private void hookDropTestSuiteEvent() {
        DropTarget dt = new DropTarget(tableViewer.getTable(), DND.DROP_MOVE | DND.DROP_COPY);
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        treeEntityTransfers.add(new TestSuiteRunConfigurationTransfer());
        dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dt.addDropListener(new TestSuiteTableDropListener(this));
    }

    /**
     * Create menu for the given <code>table</code> like this
     * 
     * <pre>
     * --------------------------------------
     * Add          Ctrl/Command + N
     * Remove       Delete
     * --------------------------------------
     * Move Up      Ctrl/Command + Arrow Up
     * Move Down    Ctrl/Command + Arrow Down
     * --------------------------------------
     * Execute      Ctrl/Command + E
     * --------------------------------------
     * </pre>
     */
    private void createTableMenu(Table table) {
        menu = new CMenu(table, selectionListener);
        table.setMenu(menu);

        enableWhenItemSelected = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !tableViewer.getSelection().isEmpty();
            }
        };
        menu.createMenuItem(StringConstants.ADD, HK_NEW);
        menu.createMenuItem(StringConstants.REMOVE, HK_DEL, enableWhenItemSelected);

        new MenuItem(menu, SWT.SEPARATOR);

        menu.createMenuItem(StringConstants.UP, HK_MOVE_ITEMS_UP, enableWhenItemSelected);
        menu.createMenuItem(StringConstants.DOWN, HK_MOVE_ITEMS_DOWN, enableWhenItemSelected);

        new MenuItem(menu, SWT.SEPARATOR);

        menu.createMenuItem(StringConstants.PA_ACTION_EXECUTE_TEST_SUITE_COLLECTION, HK_EXECUTE,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return cloneTestSuite.isAnyRunEnabled();
                    }
                }, SWT.PUSH);
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

    public void setDirty(boolean dirty) {
        mpart.setDirty(dirty);
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
                tableViewer.refresh();
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
        partService.hidePart(mpart, true);
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
        TestSuiteCollectionEntity backup = (TestSuiteCollectionEntity) SerializationUtils.clone(originalTestSuite);
        backup.reuseWrappers(originalTestSuite);
        originalTestSuite.reuseWrappers(cloneTestSuite);
        try {
            TestSuiteCollectionController.getInstance().updateTestSuiteCollection(originalTestSuite);
            updateTestSuiteCollections(originalTestSuite);
            refreshTreeEntity();
            mpart.setDirty(false);
        } catch (DALException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_MSG_UNABLE_TO_UPDATE_TEST_SUITE_COLLECTION,
                    e.getMessage());
            originalTestSuite.reuseWrappers(backup);
        }
    }

    private void refreshTreeEntity() {
        try {
            TestSuiteCollectionTreeEntity tsCollectionTreeEntity = TreeEntityUtil.getTestSuiteCollectionTreeEntity(
                    originalTestSuite, ProjectController.getInstance().getCurrentProject());
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, tsCollectionTreeEntity);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public void executeTestRun() {
        if (mpart.isDirty()) {
            MessageDialog.openInformation(null, StringConstants.INFO,
                    ComposerTestsuiteCollectionMessageConstants.INFO_MESSAGE_SAVE_BEFORE_EXECUTE);
            return;
        }
        if (originalTestSuite.isEmpty()) {
            if (MessageDialog.openConfirm(null, StringConstants.DIA_TITLE_INFORMATION,
                    StringConstants.JOB_MSG_EMPTY_TEST_SUITE_COLLECTION)) {
                selectionListener.executeAction(ActionId.ADD.getId());
            }
            return;
        }

        AbstractExecutionHandler.openConsoleLog();
        toolItemExecute.setEnabled(false);

        TestSuiteCollectionBuilderJob job = new TestSuiteCollectionBuilderJob(originalTestSuite);
        job.schedule();
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        toolItemExecute.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    public TestSuiteCollectionEntity getTestSuiteCollection() {
        return originalTestSuite;
    }

    private void setTableViewerSelection(final CTableViewer tableViewer) {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                createDynamicGotoSubMenu();
            }
        });
    }

    private void createDynamicGotoSubMenu() {
        ControlUtils.removeOldOpenMenuItem(menu);
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        List<TestSuiteEntity> testSuiteEntities = getListTestSuiteFromSelection(selection);
        SelectionAdapter openSubMenuSelection = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object menu = e.getSource();
                if (!(menu instanceof MenuItem)) {
                    return;
                }
                TestSuiteEntity testSuiteEntity = getTestSuiteFromMenuItem((MenuItem) menu);
                if (testSuiteEntity != null) {
                    eventBroker.send(EventConstants.TEST_SUITE_OPEN, testSuiteEntity);
                }
            }
        };
        if (testSuiteEntities.size() == 1) {
            ControlUtils.createOpenMenuWhenSelectOnlyOne(menu, testSuiteEntities.get(0), enableWhenItemSelected,
                    openSubMenuSelection);
            return;
        }
        MenuUtils.createOpenTestArtifactsMenu(
                getMapFileEntityToSelectionAdapter(testSuiteEntities, openSubMenuSelection), menu);
    }

    private TestSuiteEntity getTestSuiteFromMenuItem(MenuItem selectedMenuItem) {
        if (selectedMenuItem.getData() instanceof TestSuiteEntity) {
            return (TestSuiteEntity) selectedMenuItem.getData();
        }
        return null;
    }

    private List<TestSuiteEntity> getListTestSuiteFromSelection(IStructuredSelection selection) {
        List<TestSuiteEntity> testSuiteEntities = new ArrayList<TestSuiteEntity>();
        for (Object object : selection.toList()) {
            if (!(object instanceof TestSuiteRunConfiguration)) {
                continue;
            }
            TestSuiteEntity testSuiteEntity = ((TestSuiteRunConfiguration) object).getTestSuiteEntity();
            if (testSuiteEntities.contains(testSuiteEntity)) {
                continue;
            }
            testSuiteEntities.add(testSuiteEntity);
        }
        return testSuiteEntities;
    }

    private HashMap<FileEntity, SelectionAdapter> getMapFileEntityToSelectionAdapter(
            List<? extends FileEntity> fileEntities, SelectionAdapter openTestSuite) {
        HashMap<FileEntity, SelectionAdapter> map = new HashMap<>();
        for (FileEntity fileEntity : fileEntities) {
            if (fileEntity instanceof TestSuiteEntity) {
                map.put(fileEntity, openTestSuite);
            }
        }
        return map;
    }

    @Override
    public String getEntityId() {
        return originalTestSuite.getIdForDisplay();
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
        MPart part = EventUtil.getPart(event);
        if (part == null || !StringUtils.equals(part.getElementId(), mpart.getElementId())) {
            return;
        }
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, originalTestSuite);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
        Object eventData = EventUtil.getData(event);
        if (!(eventData instanceof TestSuiteCollectionEntity)) {
            return;
        }

        TestSuiteCollectionEntity updatedEntity = (TestSuiteCollectionEntity) eventData;
        if (!StringUtils.equals(updatedEntity.getIdForDisplay(), getEntityId())) {
            return;
        }
        originalTestSuite.setTag(updatedEntity.getTag());
        originalTestSuite.setDescription(updatedEntity.getDescription());
    }

    @Override
    @PreDestroy
    public void onClose() {
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
    }

    public MPart getMPart() {
        return mpart;
    }

}
