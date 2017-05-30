package com.kms.katalon.composer.execution.part;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.LogPropertyDialog;
import com.kms.katalon.composer.execution.launcher.IDEConsoleManager;
import com.kms.katalon.composer.execution.launcher.IDEObservableLauncher;
import com.kms.katalon.composer.execution.launcher.IDEObservableParentLauncher;
import com.kms.katalon.composer.execution.provider.LogRecordTreeViewer;
import com.kms.katalon.composer.execution.provider.LogRecordTreeViewerContentProvider;
import com.kms.katalon.composer.execution.provider.LogRecordTreeViewerLabelProvider;
import com.kms.katalon.composer.execution.provider.LogTableViewer;
import com.kms.katalon.composer.execution.provider.LogTableViewerFilter;
import com.kms.katalon.composer.execution.trace.LogExceptionNavigator;
import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XMLLoggerParser;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.XmlLogRecordException;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.logging.LogExceptionFilter;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class LogViewerPart implements EventHandler, LauncherListener {

    private static final int AFTER_STATUS_MENU_INDEX = 1;

    private static final int INCREMENT = 10;

    @Inject
    private UISynchronize sync;

    @Inject
    private IEventBroker eventBroker;

    private Table table;

    private LogTableViewer tableViewer;

    private ProgressBar progressBar;

    private Label lblNumTestcases, lblNumFailures, lblNumPasses, lblNumErrors;

    private Composite parentComposite;

    private IDEObservableLauncher rootLauncherWatched;

    private List<IDEObservableLauncher> launchersWatched;

    private int selectedLauncherWatchedIndex;

    private boolean isBusy;

    private boolean loadingLogCanceled;

    private LogRecordTreeViewer treeViewer;

    private StyledText txtStartTime, txtName, txtEslapedTime, txtMessage;

    private ToolItem btnShowAllLogs, btnShowInfoLogs, btnShowPassedLogs, btnShowFailedLogs, btnShowErrorLogs,
            btnShowWarningLogs, btnShowNotRunLogs;

    private List<XmlLogRecord> currentRecords;

    private Composite compositeTreeContainer;

    private LogExceptionNavigator logNavigator;

    private MPart fMPart;

    private ScopedPreferenceStore preferenceStore;

    private LogLoadingJob loadingJob;

    private void initToolItemsStatus(MPart mpart) {
        for (MToolBarElement toolbarElement : mpart.getToolbar().getChildren()) {
            if (!(toolbarElement instanceof MDirectToolItem)) {
                continue;
            }
            MDirectToolItem toolItem = (MDirectToolItem) toolbarElement;
            switch (toolItem.getElementId()) {
                case IdConstants.LOG_VIEWER_TOOL_ITEM_TREE_ID:
                    toolItem.setSelected(preferenceStore
                            .getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE));
                    break;
                case IdConstants.LOG_VIEWER_TOOL_ITEM_PIN_ID:
                    toolItem.setSelected(
                            preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_PIN_LOG));
                    break;
            }
        }
    }

    private void updateMenuStatus(MPart mpart) {
        boolean isShowLogAsTree = preferenceStore
                .getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE);
        for (MMenu menu : mpart.getMenus()) {
            if (!IdConstants.LOG_VIEWER_MENU_TREEVIEW.equals(menu.getElementId())) {
                menu.setVisible(true);
                continue;
            }

            if (isShowLogAsTree) {
                menu.setVisible(true);
                for (MMenuElement childElement : menu.getChildren()) {
                    if (childElement instanceof MDirectMenuItem
                            && IdConstants.LOG_VIEWER_MENU_ITEM_WORD_WRAP.equals(childElement.getElementId())) {
                        MDirectMenuItem wordWrapElement = (MDirectMenuItem) childElement;
                        wordWrapElement.setSelected(preferenceStore
                                .getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP));
                    }
                }
            } else {
                menu.setVisible(false);
            }
        }
    }

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        preferenceStore = getPreferenceStore(LogViewerPart.class);
        fMPart = mpart;
        logNavigator = new LogExceptionNavigator();
        launchersWatched = new ArrayList<>();
        selectedLauncherWatchedIndex = -1;
        isBusy = false;
        currentRecords = new ArrayList<XmlLogRecord>();
        parentComposite = parent;
        initToolItemsStatus(mpart);
        createControls(parent);
        createLogViewerControl(parent);
        registerEventListeners();
        getChangingViewToolItem().setEnabled(true);
    }

    private void createLogViewerControl(Composite parent) {
        disposeChildrenFromIndex(parent, AFTER_STATUS_MENU_INDEX);

        boolean showLogsAsTree = preferenceStore
                .getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE);

        if (showLogsAsTree) {
            createTreeCompositeContainer(parent);
        } else {
            createTableComposite(parent);
        }
        parent.layout(true);
        updateMenuStatus(fMPart);
    }

    private void disposeChildrenFromIndex(Composite parent, int start) {
        parent.setRedraw(false);
        while (parent.getChildren().length > start) {
            parent.getChildren()[start].dispose();
        }
        parent.setRedraw(true);
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_RESET, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_REFRESH, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_CHANGE_VIEW_TYPE, this);
        eventBroker.subscribe(EventConstants.EXPLORER_RELOAD_INPUT, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_WORD_WRAP, this);

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
                .addPostSelectionListener(IConsoleConstants.ID_CONSOLE_VIEW, new ISelectionListener() {

                    @Override
                    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                        setSelectedConsoleView();
                    }
                });

    }

    private void createTreeCompositeToolbar(Composite compositeTreeContainer) {
        ToolBar toolBar = new ToolBar(compositeTreeContainer, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
        toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        ToolItem tltmCollapseAll = new ToolItem(toolBar, SWT.NONE);
        tltmCollapseAll.setToolTipText(StringConstants.PA_COLLAPSE_ALL);
        tltmCollapseAll.setImage(ImageConstants.IMG_16_COLLAPSE_ALL);
        tltmCollapseAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.collapseAll();
            }
        });

        ToolItem tltmExpandAll = new ToolItem(toolBar, SWT.NONE);
        tltmExpandAll.setToolTipText(StringConstants.PA_EXPAND_ALL);
        tltmExpandAll.setImage(ImageConstants.IMG_16_EXPAND_ALL);
        tltmExpandAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.expandAll();
            }
        });

        ToolItem tltmShowPreviousFailure = new ToolItem(toolBar, SWT.NONE);
        tltmShowPreviousFailure.setImage(ImageConstants.IMG_16_PREVIOUS_FAILURE);
        tltmShowPreviousFailure.setToolTipText(StringConstants.PA_PREV_FAILURE);
        tltmShowPreviousFailure.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isLaunchersWatchedValid()) {
                    isBusy = true;
                    treeViewer.selectPreviousFailure();
                    isBusy = false;
                }
            }
        });

        ToolItem tltmShowNextFailure = new ToolItem(toolBar, SWT.NONE);
        tltmShowNextFailure.setImage(ImageConstants.IMG_16_NEXT_FAILURE);
        tltmShowNextFailure.setToolTipText(StringConstants.PA_NEXT_FAILURE);
        tltmShowNextFailure.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (isLaunchersWatchedValid()) {
                    isBusy = true;
                    treeViewer.selectNextFailure();
                    isBusy = false;
                }
            }
        });
    }

    private boolean isLaunchersWatchedValid() {
        return launchersWatched != null && !launchersWatched.isEmpty() && selectedLauncherWatchedIndex > -1
                && selectedLauncherWatchedIndex < launchersWatched.size();
    }

    public IDEObservableLauncher getLauncherWatched() {
        if (isLaunchersWatchedValid()) {
            return launchersWatched.get(selectedLauncherWatchedIndex);
        }
        return null;
    }

    private void createTreeCompositeDetails(SashForm sashForm) {
        Composite compositeTreeDetails = new Composite(sashForm, SWT.NONE);
        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTreeDetails.setLayout(treeColumnLayout);

        treeViewer = new LogRecordTreeViewer(compositeTreeDetails, SWT.BORDER);
        treeViewer.setContentProvider(new LogRecordTreeViewerContentProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn treeColumn = treeViewerColumn.getColumn();
        treeColumn.setWidth(400);
        treeViewerColumn.setLabelProvider(new LogRecordTreeViewerLabelProvider());

        treeColumnLayout.setColumnData(treeColumn, new ColumnWeightData(95, treeColumn.getWidth()));
    }

    private void createTreeCompositeContainer(Composite parent) {
        compositeTreeContainer = new Composite(parent, SWT.NONE);
        compositeTreeContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.horizontalSpacing = 0;
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        compositeTreeContainer.setLayout(glComposite);

        createTreeCompositeToolbar(compositeTreeContainer);

        SashForm sashForm = new SashForm(compositeTreeContainer, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createTreeCompositeDetails(sashForm);

        createTreeNodePropertiesComposite(sashForm);
        sashForm.setWeights(new int[] { 1, 1 });

        treeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                try {
                    showTreeLogProperties();
                } catch (Exception e) {
                    logError(e);
                }
            }
        });

        treeViewer.reset(new ArrayList<XmlLogRecord>());
        if (isLaunchersWatchedValid()) {
            loadingJob = new LogLoadingJob();
            loadingJob.setUser(true);
            loadingJob.schedule();
        }
    }

    private class LogLoadingJob extends Job {

        public LogLoadingJob() {
            super(StringConstants.PA_LOADING_LOGS);
        }

        @Override
        public IStatus run(IProgressMonitor monitor) {
            try {
                isBusy = true;
                final int logSize = currentRecords.size();
                monitor.beginTask(StringConstants.PA_LOADING_LOGS + "...", logSize + 1);
                sync.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        compositeTreeContainer.setRedraw(false);
                        getChangingViewToolItem().setEnabled(false);
                    }
                });
                for (int index = 0; index < logSize; index++) {
                    if (monitor.isCanceled()) {
                        loadingLogCanceled = true;
                        return Status.CANCEL_STATUS;
                    }
                    final XmlLogRecord record = currentRecords.get(index);
                    sync.syncExec(new Runnable() {
                        public void run() {
                            treeViewer.addRecord(record);
                        }
                    });

                    if (record.getMessage() != null && !record.getMessage().isEmpty()) {
                        monitor.subTask(StringConstants.PA_LOADING_LOG + " " + Integer.toString(index + 1) + " of "
                                + logSize + "...");
                    }

                    monitor.worked(1);
                }

                return Status.OK_STATUS;
            } finally {
                sync.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        compositeTreeContainer.setRedraw(true);
                        getChangingViewToolItem().setEnabled(true);
                    }
                });
                monitor.done();
                isBusy = false;
            }
        }

    }

    private void showFailureTreeLogMessage(ILogParentTreeNode logParentTreeNode) throws Exception {
        if (logParentTreeNode.getResult() != null) {
            XmlLogRecord result = logParentTreeNode.getResult();
            LogLevel resultLevel = LogLevel.valueOf(logParentTreeNode.getResult().getLevel());
            if (resultLevel == LogLevel.FAILED || resultLevel == LogLevel.ERROR) {
                StringBuilder messageBuilder = new StringBuilder(result.getMessage());

                List<StyleRange> styleRanges = new ArrayList<StyleRange>();
                if (result.getExceptions() != null) {
                    messageBuilder.append("\n");
                    for (XmlLogRecordException exceptionLogEntry : result.getExceptions()) {
                        if (LogExceptionFilter.isTraceableException(exceptionLogEntry)) {
                            messageBuilder.append("\n");

                            StyleRange range = new StyleRange();
                            range.start = messageBuilder.length();

                            String exceptionLogString = exceptionLogEntry.toString();
                            if (LogExceptionFilter.isTestCaseScript(exceptionLogEntry.getClassName())) {
                                TestCaseEntity testCase = LogExceptionFilter
                                        .getTestCaseByLogException(exceptionLogEntry);
                                if (testCase != null) {
                                    String testCaseId = testCase.getIdForDisplay();
                                    exceptionLogString = exceptionLogEntry.toString()
                                            .replace(exceptionLogEntry.getClassName(), testCaseId);
                                }
                            }

                            messageBuilder.append(exceptionLogString);

                            range.length = exceptionLogString.length();
                            range.underline = true;
                            range.data = exceptionLogEntry;
                            range.underlineStyle = SWT.UNDERLINE_LINK;

                            styleRanges.add(range);
                        }
                    }
                }

                txtMessage.setText(messageBuilder.toString());
                if (styleRanges.size() > 0) {
                    txtMessage.setStyleRanges(styleRanges.toArray(new StyleRange[0]));

                    while (txtMessage.getListeners(SWT.MouseDown).length > 1) {
                        txtMessage.removeListener(SWT.MouseDown, txtMessage
                                .getListeners(SWT.MouseDown)[txtMessage.getListeners(SWT.MouseDown).length - 1]);
                    }

                    txtMessage.addListener(SWT.MouseDown, mouseDownListener);
                }
            } else {
                txtMessage.setText(result.getMessage());
            }
        } else {
            txtMessage.setText(StringConstants.EMPTY);
        }
    }

    // Handle mouse down event on txtMessage
    private Listener mouseDownListener = new Listener() {
        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            try {
                int offset = txtMessage.getOffsetAtLocation(new Point(event.x, event.y));
                StyleRange style = txtMessage.getStyleRangeAtOffset(offset);
                if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
                    XmlLogRecordException logException = (XmlLogRecordException) style.data;
                    navigateScriptByLogExpcetion(logException);
                }
            } catch (IllegalArgumentException e) {
                // no character under event.x, event.y
            }
        }
    };

    private LogPropertyDialog dialog;

    private void showTreeLogProperties() throws Exception {
        StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
        if (selection == null) {
            return;
        }

        if (selection.getFirstElement() instanceof ILogTreeNode) {
            ILogTreeNode logTreeNode = (ILogTreeNode) selection.getFirstElement();
            if (logTreeNode instanceof ILogParentTreeNode) {
                ILogParentTreeNode logParentTreeNode = (ILogParentTreeNode) logTreeNode;

                txtStartTime.setText(logParentTreeNode.getRecordStart().getLogTimeString());
                txtName.setText(logParentTreeNode.getMessage());
                txtEslapedTime.setText(logParentTreeNode.getFullElapsedTime());
                StyledString styledString = new StyledString(txtEslapedTime.getText(), StyledString.COUNTER_STYLER);
                txtEslapedTime.setStyleRanges(styledString.getStyleRanges());

                showFailureTreeLogMessage(logParentTreeNode);

            } else {
                txtStartTime.setText(StringConstants.EMPTY);
                txtEslapedTime.setText(StringConstants.EMPTY);
                txtMessage.setText(logTreeNode.getMessage());
                txtName.setText(StringConstants.EMPTY);
            }
        }
    }

    private void navigateScriptByLogExpcetion(XmlLogRecordException logException) {
        if (LogExceptionFilter.isTestCaseScript(logException.getClassName())) {
            logNavigator.openTestCaseByLogException(logException);
        } else if (LogExceptionFilter.isCustomKeywordScript(logException.getClassName())) {
            logNavigator.openKeywordByLogException(logException);
        }
    }

    private void createTreeNodePropertiesComposite(SashForm sashForm) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        final Color whiteBackgroundColor = ColorUtil.getWhiteBackgroundColor();
        scrolledComposite.setBackground(whiteBackgroundColor);

        Composite compositeTreeNodeProperties = new Composite(scrolledComposite, SWT.BORDER);
        compositeTreeNodeProperties.setLayout(new GridLayout(4, false));
        compositeTreeNodeProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        compositeTreeNodeProperties.setBackground(whiteBackgroundColor);
        scrolledComposite.setContent(compositeTreeNodeProperties);

        Label lblName = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblName.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblName.setText(ComposerExecutionMessageConstants.PA_LBL_NAME);
        lblName.setBackground(whiteBackgroundColor);

        txtName = new StyledText(compositeTreeNodeProperties, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        txtName.setEditable(false);

        Label lblLogStart = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblLogStart.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblLogStart.setText(StringConstants.PA_LBL_START);
        lblLogStart.setBackground(whiteBackgroundColor);

        txtStartTime = new StyledText(compositeTreeNodeProperties, SWT.BORDER);
        final GridData layoutDataStartTime = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutDataStartTime.minimumWidth = 200;
        layoutDataStartTime.widthHint = 200;
        txtStartTime.setLayoutData(layoutDataStartTime);
        txtStartTime.setEditable(false);

        Label lblLogRunTime = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblLogRunTime.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblLogRunTime.setText(StringConstants.PA_LBL_ELAPSED_TIME);
        lblLogRunTime.setBackground(whiteBackgroundColor);

        txtEslapedTime = new StyledText(compositeTreeNodeProperties, SWT.BORDER);
        final GridData layoutDataElapsedTime = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutDataElapsedTime.minimumWidth = 200;
        layoutDataElapsedTime.widthHint = 200;
        txtEslapedTime.setLayoutData(layoutDataElapsedTime);
        txtEslapedTime.setEditable(false);

        Label lblMessage = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblMessage.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblMessage.setText(StringConstants.PA_LBL_MESSAGE);
        lblMessage.setBackground(whiteBackgroundColor);

        txtMessage = new StyledText(compositeTreeNodeProperties, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        txtMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        txtMessage.setEditable(false);
        setWrapTxtMessage();

        scrolledComposite.setMinSize(compositeTreeNodeProperties.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void createStatusComposite(Composite container) {
        Composite compositeStatus = new Composite(container, SWT.NONE);
        compositeStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeStatus = new GridLayout(2, true);
        glCompositeStatus.marginWidth = 0;
        compositeStatus.setLayout(glCompositeStatus);

        Composite composite = new Composite(compositeStatus, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        composite.setLayout(new GridLayout(4, true));

        Composite compositeRuns = new Composite(composite, SWT.NONE);
        compositeRuns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeRuns.setLayout(new GridLayout(2, false));

        Label lblRuns = new Label(compositeRuns, SWT.NONE);
        lblRuns.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        lblRuns.setText(StringConstants.PA_LBL_RUNS);

        lblNumTestcases = new Label(compositeRuns, SWT.NONE);
        lblNumTestcases.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositePasses = new Composite(composite, SWT.NONE);
        compositePasses.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositePasses.setLayout(new GridLayout(2, false));

        Label lblPasses = new Label(compositePasses, SWT.NONE);
        lblPasses.setText(StringConstants.PA_LBL_PASSES);
        lblPasses.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

        lblNumPasses = new Label(compositePasses, SWT.NONE);
        lblNumPasses.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeFailures = new Composite(composite, SWT.NONE);
        compositeFailures.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeFailures.setLayout(new GridLayout(2, false));

        Label lblFailures = new Label(compositeFailures, SWT.NONE);
        lblFailures.setText(StringConstants.PA_LBL_FAILURES);
        lblFailures.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

        lblNumFailures = new Label(compositeFailures, SWT.NONE);
        lblNumFailures.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeIncompletes = new Composite(composite, SWT.NONE);
        compositeIncompletes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeIncompletes.setLayout(new GridLayout(2, false));

        Label lblErrors = new Label(compositeIncompletes, SWT.NONE);
        lblErrors.setText(StringConstants.PA_LBL_ERRORS);
        lblErrors.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

        lblNumErrors = new Label(compositeIncompletes, SWT.NONE);
        lblNumErrors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        progressBar = new ProgressBar(compositeStatus, SWT.SMOOTH);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    private void createTableButtonComposite(Composite container) {
        ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
        toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        btnShowAllLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowAllLogs.setData(StringConstants.ID, ComposerExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS);
        btnShowAllLogs.setText(StringConstants.PA_TIP_ALL);
        btnShowAllLogs.setToolTipText(StringConstants.PA_TIP_ALL);
        btnShowAllLogs.setImage(ImageConstants.IMG_16_LOGVIEW_ALL);

        btnShowInfoLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowInfoLogs.setData(StringConstants.ID, ComposerExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS);
        btnShowInfoLogs.setText(StringConstants.PA_TIP_INFO);
        btnShowInfoLogs.setToolTipText(StringConstants.PA_TIP_INFO);
        btnShowInfoLogs.setImage(ImageConstants.IMG_16_LOGVIEW_INFO);

        btnShowPassedLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowPassedLogs.setData(StringConstants.ID, ComposerExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS);
        btnShowPassedLogs.setText(StringConstants.PA_TIP_PASSED);
        btnShowPassedLogs.setToolTipText(StringConstants.PA_TIP_PASSED);
        btnShowPassedLogs.setImage(ImageConstants.IMG_16_LOGVIEW_PASSED);

        btnShowFailedLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowFailedLogs.setData(StringConstants.ID, ComposerExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS);
        btnShowFailedLogs.setText(StringConstants.PA_TIP_FAILED);
        btnShowFailedLogs.setToolTipText(StringConstants.PA_TIP_FAILED);
        btnShowFailedLogs.setImage(ImageConstants.IMG_16_LOGVIEW_FAILED);

        btnShowErrorLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowErrorLogs.setData(StringConstants.ID, ComposerExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS);
        btnShowErrorLogs.setText(StringConstants.PA_TIP_ERROR);
        btnShowErrorLogs.setToolTipText(StringConstants.PA_TIP_ERROR);
        btnShowErrorLogs.setImage(ImageConstants.IMG_16_LOGVIEW_ERROR);

        btnShowWarningLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowWarningLogs.setData(StringConstants.ID,
                ComposerExecutionPreferenceConstants.EXECUTION_SHOW_WARNING_LOGS);
        btnShowWarningLogs.setText(StringConstants.PA_TIP_WARNING);
        btnShowWarningLogs.setToolTipText(StringConstants.PA_TIP_WARNING);
        btnShowWarningLogs.setImage(ImageConstants.IMG_16_LOGVIEW_WARNING);

        btnShowNotRunLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowNotRunLogs.setData(StringConstants.ID, ComposerExecutionPreferenceConstants.EXECUTION_SHOW_NOT_RUN_LOGS);
        btnShowNotRunLogs.setText(StringConstants.PA_TIP_NOT_RUN);
        btnShowNotRunLogs.setToolTipText(StringConstants.PA_TIP_NOT_RUN);
        btnShowNotRunLogs.setImage(ImageConstants.IMG_16_LOGVIEW_NOT_RUN);

        SelectionAdapter logFilterSelectionAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performFilterTableLogs((ToolItem) e.getSource());
            }
        };

        btnShowAllLogs.addSelectionListener(logFilterSelectionAdapter);
        btnShowInfoLogs.addSelectionListener(logFilterSelectionAdapter);
        btnShowPassedLogs.addSelectionListener(logFilterSelectionAdapter);
        btnShowFailedLogs.addSelectionListener(logFilterSelectionAdapter);
        btnShowErrorLogs.addSelectionListener(logFilterSelectionAdapter);
        btnShowWarningLogs.addSelectionListener(logFilterSelectionAdapter);
        btnShowNotRunLogs.addSelectionListener(logFilterSelectionAdapter);

        updateTableButtons();
    }

    private void performFilterTableLogs(ToolItem button) {
        String prefId = (String) button.getData(StringConstants.ID);
        if (isBlank(prefId)) {
            return;
        }

        try {
            preferenceStore.setValue(prefId, button.getSelection());
            preferenceStore.save();
            tableViewer.refresh();
        } catch (IOException e) {
            logError(e);
        }
    }

    private void updateTableButtons() {
        btnShowAllLogs
                .setSelection(preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS));
        btnShowInfoLogs.setSelection(
                preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS));
        btnShowPassedLogs.setSelection(
                preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS));
        btnShowFailedLogs.setSelection(
                preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS));
        btnShowErrorLogs.setSelection(
                preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS));
        btnShowWarningLogs.setSelection(
                preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_WARNING_LOGS));
        btnShowNotRunLogs.setSelection(
                preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_NOT_RUN_LOGS));
    }

    private void createTableCompositeDetails(Composite container) {
        Composite compositeTable = new Composite(container, SWT.BORDER);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new LogTableViewer(compositeTable, SWT.FULL_SELECTION, eventBroker);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn tbViewerColumnLevel = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnLevel = tbViewerColumnLevel.getColumn();
        tblclmnColumnLevel.setText(StringConstants.PA_COL_LEVEL);

        TableViewerColumn tbViewerColumnTime = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnTime = tbViewerColumnTime.getColumn();
        tblclmnColumnTime.setText(StringConstants.TIME);

        TableViewerColumn tbViewerColumnMessage = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnMessage = tbViewerColumnMessage.getColumn();
        tblclmnColumnMessage.setText(StringConstants.MESSAGE);

        TableColumnLayout tableComColumnLayout = new TableColumnLayout();
        compositeTable.setLayout(tableComColumnLayout);

        tableComColumnLayout.setColumnData(tblclmnColumnTime, new ColumnWeightData(0, 250));
        tableComColumnLayout.setColumnData(tblclmnColumnLevel, new ColumnWeightData(0, 150));
        tableComColumnLayout.setColumnData(tblclmnColumnMessage, new ColumnWeightData(99, 500));

        tbViewerColumnTime.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof XmlLogRecord) {
                    return XMLLoggerParser.getRecordDate((LogRecord) element);
                }
                return StringConstants.EMPTY;
            }
        });

        tbViewerColumnLevel.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof XmlLogRecord) {
                    return ((XmlLogRecord) element).getLevel().getName();
                }
                return StringConstants.EMPTY;
            }
        });

        tbViewerColumnMessage.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof XmlLogRecord) {
                    return ((XmlLogRecord) element).getMessage();
                }
                return StringConstants.EMPTY;
            }
        });
        tableViewer.addFilter(new LogTableViewerFilter());

        table.addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                GC gc = event.gc;
                event.detail &= ~SWT.SELECTED;
                gc.fillRectangle(event.x, event.y, event.width, event.height);
            }
        });

        Menu popupMenu = new Menu(table);
        MenuItem newItem = new MenuItem(popupMenu, SWT.CASCADE);
        newItem.setText(StringConstants.PA_LOG_CONTEXT_MENU_PROPERTIES);
        newItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showRecordProperties();
            }
        });

        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                showRecordProperties();
            }
        });

        table.setMenu(popupMenu);
        tableViewer.setInput(new ArrayList<XmlLogRecord>());
        if (isLaunchersWatchedValid()) {
            isBusy = true;
            for (XmlLogRecord record : currentRecords) {
                tableViewer.add(record);
            }
            isBusy = false;
        }
    }

    private void createTableComposite(Composite parent) {
        Composite tableContainer = new Composite(parent, SWT.NONE);
        tableContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        tableContainer.setLayout(glComposite);

        createTableButtonComposite(tableContainer);
        createTableCompositeDetails(tableContainer);
    }

    private void showRecordProperties() {
        if (dialog != null && dialog.isOpen()) {
            return;
        }
        int index = table.getSelectionIndex();

        if (index == -1) {
            return; // no row selected
        }

        TableItem selectedItem = table.getItem(index);
        XmlLogRecord selectedRecord = (XmlLogRecord) selectedItem.getData();
        dialog = new LogPropertyDialog(table.getDisplay().getActiveShell(), selectedRecord);
        dialog.open();
    }

    private void createControls(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        parent.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
        createStatusComposite(parent);
    }

    private void updateProgressBar() {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!isLaunchersWatchedValid()) {
                    return;
                }

                ILauncherResult result = getLauncherWatched().getResult();
                if (result == null) {
                    return;
                }

                final int numExecuted = result.getExecutedTestCases();
                progressBar.setSelection(numExecuted * INCREMENT);
                lblNumTestcases
                        .setText(Integer.toString(numExecuted) + "/" + Integer.toString(result.getTotalTestCases()));
                // update
                lblNumPasses.setText(Integer.toString(result.getNumPasses()));
                lblNumFailures.setText(Integer.toString(result.getNumFailures()));
                lblNumErrors.setText(Integer.toString(result.getNumErrors()));

                lblNumTestcases.getParent().getParent().layout();
            }
        });
    }

    private void resetProgressBar() {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                progressBar.setSelection(0);
                final IDEObservableLauncher launcherWatched = getLauncherWatched();
                int maxValue = launcherWatched != null ? launcherWatched.getResult().getTotalTestCases() : 1;
                progressBar.setMaximum(maxValue * INCREMENT);
                lblNumTestcases.setText(Integer.toString(progressBar.getSelection()) + "/"
                        + Integer.toString(progressBar.getMaximum() / INCREMENT));
                lblNumPasses.setText(Integer.toString(0));
                lblNumFailures.setText(Integer.toString(0));
                lblNumFailures.setForeground(ColorUtil.getDefaultTextColor());
                lblNumErrors.setText(Integer.toString(0));
                lblNumErrors.setForeground(ColorUtil.getDefaultTextColor());

                lblNumTestcases.getParent().getParent().layout();
            }
        });
    }

    private void addRecords(final List<XmlLogRecord> records) throws InterruptedException {
        if (loadingJob != null && loadingJob.getState() == Job.RUNNING) {
            return;
        }
        waitForNotBusy();

        isBusy = true;

        try {
            boolean showLogsAsTree = preferenceStore
                    .getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE);

            if (showLogsAsTree) {
                treeViewer.addRecords(records);
            } else {
                for (XmlLogRecord record : records) {
                    tableViewer.add(record);
                }
            }
            currentRecords.addAll(records);

        } finally {
            isBusy = false;
        }
    }

    private synchronized void changeObservedLauncher(final Event event) throws Exception {
        final LauncherListener launcherListener = this;
        new Thread(new Runnable() {
            private void getWatchedLauncherFromEvent() {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof String)) {
                    return;
                }

                String launcherId = (String) object;
                for (ILauncher launcher : LauncherManager.getInstance().getAllLaunchers()) {
                    if (!launcher.getId().equals(launcherId)) {
                        continue;
                    }
                    if (launcher instanceof IDEObservableParentLauncher) {
                        rootLauncherWatched = (IDEObservableParentLauncher) launcher;
                        launchersWatched.addAll(((IDEObservableParentLauncher) rootLauncherWatched).getSubLaunchers());
                        return;
                    }
                    if (launcher instanceof IDEObservableLauncher) {
                        rootLauncherWatched = (IDEObservableLauncher) launcher;
                        launchersWatched.add(rootLauncherWatched);
                    }
                }
            }

            @Override
            public void run() {
                if (parentComposite == null || parentComposite.isDisposed()) {
                    return;
                }
                waitForNotBusy();

                currentRecords.clear();
                clearWatchedLaunchers(launcherListener);

                loadingLogCanceled = false;
                getWatchedLauncherFromEvent();
                if (launchersWatched != null && !launchersWatched.isEmpty()) {
                    selectedLauncherWatchedIndex = 0;
                }
                refreshPart(launcherListener);
            }

            private void clearWatchedLaunchers(final LauncherListener launcherListener) {
                selectedLauncherWatchedIndex = -1;
                if (launchersWatched == null || launchersWatched.isEmpty()) {
                    return;
                }
                for (IDEObservableLauncher launcherWatched : launchersWatched) {
                    rootLauncherWatched.setObserved(false);
                    launcherWatched.removeListener(launcherListener);
                }
                launchersWatched.clear();
            }
        }).start();
    }

    private void refreshPart(final LauncherListener launcherListener) {
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                resetProgressBar();

                if (isLaunchersWatchedValid()) {
                    final IDEObservableLauncher launcherWatched = getLauncherWatched();
                    currentRecords.addAll(launcherWatched.getLogRecords());
                    rootLauncherWatched.setObserved(true);
                    launcherWatched.addListener(launcherListener);
                    setSelectedConsoleView();
                    updateProgressBar();
                }
                createLogViewerControl(parentComposite);
                eventBroker.send(EventConstants.JOB_REFRESH, null);
            }
        });
    }

    @Override
    public void handleEvent(final Event event) {
        try {
            String topic = event.getTopic();
            switch (topic) {
                case EventConstants.CONSOLE_LOG_RESET: {
                    changeObservedLauncher(event);
                    break;
                }
                case EventConstants.CONSOLE_LOG_CHANGE_VIEW_TYPE: {
                    startChangingLogViewerThread();
                    break;
                }
                case EventConstants.CONSOLE_LOG_WORD_WRAP: {
                    setWrapTxtMessage();
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception e) {
            logError(e);
        }
    }

    private synchronized void startChangingLogViewerThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (parentComposite == null || parentComposite.isDisposed()) {
                    return;
                }
                waitForNotBusy();

                sync.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        resetProgressBar();
                        createLogViewerControl(parentComposite);
                        updateProgressBar();
                        parentComposite.layout();
                    }
                });
            }
        }).start();
    }

    private MDirectToolItem getChangingViewToolItem() {
        for (MToolBarElement element : fMPart.getToolbar().getChildren()) {
            if (IdConstants.LOG_VIEWER_TOOL_ITEM_TREE_ID.equals(element.getElementId())) {
                return (MDirectToolItem) element;
            }
        }
        return null;
    }

    private void waitForNotBusy() {
        while (isBusy || (loadingJob != null && loadingJob.getState() == Job.RUNNING)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignored
            }
        }
    }

    private void setWrapTxtMessage() {
        boolean wrap = preferenceStore.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP);
        if (txtMessage.getListeners(SWT.Modify).length == 0) {
            txtMessage.addListener(SWT.Modify, ControlUtils.getAutoHideStyledTextScrollbarListener);
            txtMessage.addListener(SWT.Resize, ControlUtils.getAutoHideStyledTextScrollbarListener);
        }
        txtMessage.setWordWrap(wrap);
        txtMessage.layout();
    }

    private void setSelectedConsoleView() {
        final IDEObservableLauncher launcherWatched = getLauncherWatched();
        if (launcherWatched == null || launcherWatched.getLaunch() == null) {
            return;
        }

        IDEConsoleManager.openLaunchConsole(launcherWatched.getLaunch());
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject notifiedObject) {
        final IDEObservableLauncher launcherWatched = getLauncherWatched();
        switch (event) {
            case UPDATE_RECORD:
                if (launcherWatched == null || loadingLogCanceled) {
                    return;
                }
                UISynchronizeService.syncExec(new Runnable() {

                    @Override
                    public void run() {
                        List<XmlLogRecord> logRecords = launcherWatched.getLogRecords();
                        if (currentRecords.size() >= logRecords.size()) {
                            return;
                        }

                        try {
                            addRecords(logRecords.subList(currentRecords.size(), logRecords.size()));
                        } catch (InterruptedException e) {
                            logError(e);
                        }
                    }
                });
                break;
            case UPDATE_RESULT:
                if (launcherWatched != null && StringUtils.defaultIfEmpty(launcherWatched.getId(), "")
                        .equals(notifiedObject.getLauncherId())) {
                    updateProgressBar();
                }
                break;
            default:
                break;
        }
    }

    public List<IDEObservableLauncher> getLaunchersWatched() {
        return launchersWatched;
    }

    public void changeSelectedLaucherWatchedById(String launcherId) {
        if (StringUtils.isEmpty(launcherId)) {
            return;
        }
        for (int index = 0; index < launchersWatched.size(); index++) {
            IDEObservableLauncher launcherWatched = launchersWatched.get(index);
            if (launcherId.equals(launcherWatched.getId()) && index != selectedLauncherWatchedIndex) {
                selectedLauncherWatchedIndex = index;
                currentRecords.clear();
                refreshPart(this);
                return;
            }
        }
    }
}
