package com.kms.katalon.composer.execution.part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
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
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.internal.console.ConsoleView;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.dialog.LogPropertyDialog;
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
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.PreferenceConstants.ExecutionPreferenceConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XMLLoggerParser;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.XmlLogRecordException;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.launcher.AbstractLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.logging.LogExceptionFilter;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class LogViewerPart implements EventHandler {

    private static final int INCREMENT = 10;

    @Inject
    private UISynchronize sync;

    @Inject
    private IEventBroker eventBroker;

    private Table table;

    private LogTableViewer tableViewer;

    private ProgressBar progressBar;

    private int maxValue;

    private Label lblNumTestcases, lblNumFailures, lblNumPasses, lblNumErrors;

    private Composite parentComposite;

    private AbstractLauncher launcherWatched;

    private boolean isBusy;

    private boolean stopAdding;

    private LogRecordTreeViewer treeViewer;

    private StyledText txtStartTime, txtEndTime, txtEslapedTime, txtMessage;

    private ToolItem btnShowAllLogs, btnShowInfoLogs, btnShowPassedLogs, btnShowFailedLogs, btnShowErrorLogs;

    private List<XmlLogRecord> currentRecords;

    private Composite compositeTreeContainer;

    private LogExceptionNavigator logNavigator;

    private MPart fMPart;

    private IPreferenceStore preferenceStore;

    private void updateToolItemsStatus(MPart mpart) {
        for (MToolBarElement toolbarElement : mpart.getToolbar().getChildren()) {
            MDirectToolItem toolItem = (MDirectToolItem) toolbarElement;
            switch (toolItem.getElementId()) {
                case IdConstants.LOG_VIEWER_TOOL_ITEM_TREE_ID:
                    toolItem.setSelected(preferenceStore
                            .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE));
                    break;
                case IdConstants.LOG_VIEWER_TOOL_ITEM_PIN_ID:
                    toolItem.setSelected(preferenceStore
                            .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_PIN_LOG));
                    break;
            }
        }
    }

    private void updateMenuStatus(MPart mpart) {
        boolean isShowLogAsTree = preferenceStore
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE);
        for (MMenu menu : mpart.getMenus()) {
            if (!IdConstants.LOG_VIEWER_MENU_TREEVIEW.equals(menu.getElementId())) {
                continue;
            }
            if (isShowLogAsTree) {
                menu.setVisible(true);
                for (MMenuElement childElement : menu.getChildren()) {
                    //
                    if (childElement instanceof MDirectMenuItem
                            && IdConstants.LOG_VIEWER_MENU_ITEM_WORD_WRAP.equals(childElement.getElementId())) {
                        MDirectMenuItem wordWrapElement = (MDirectMenuItem) childElement;
                        wordWrapElement.setSelected(preferenceStore
                                .getBoolean(ExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP));
                    }
                }
            } else {
                menu.setVisible(false);
            }
        }
    }

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        preferenceStore = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        fMPart = mpart;
        logNavigator = new LogExceptionNavigator();
        launcherWatched = null;
        isBusy = false;
        stopAdding = false;
        currentRecords = new ArrayList<XmlLogRecord>();
        parentComposite = parent;
        updateToolItemsStatus(mpart);
        createControls(parent);
        createLogViewerControl(parent);
        registerListeners();
    }

    private void createLogViewerControl(Composite parent) {
        isBusy = true;
        while (parent.getChildren().length > 1) {
            parent.getChildren()[1].dispose();
        }

        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);

        boolean showLogsAsTree = store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE);

        if (showLogsAsTree) {
            createTreeCompositeContainer(parent);
        } else {
            createTableComposite(parent);
        }
        parent.layout(true);
        updateMenuStatus(fMPart);
    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_RESET, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_ADD_ITEMS, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_REFRESH, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_CHANGE_VIEW_TYPE, this);
        eventBroker.subscribe(EventConstants.EXPLORER_RELOAD_INPUT, this);
        eventBroker.subscribe(EventConstants.CONSOLE_LOG_WORD_WRAP, this);

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].addPostSelectionListener(
                IConsoleConstants.ID_CONSOLE_VIEW, new ISelectionListener() {

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
        tltmCollapseAll.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ELCL_COLLAPSEALL));
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
                if (launcherWatched != null) {
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
                if (launcherWatched != null) {
                    isBusy = true;
                    treeViewer.selectNextFailure();
                    isBusy = false;
                }
            }
        });
    }

    private void createTreeCompositeDetails(SashForm sashForm) {
        Composite compositeTreeDetails = new Composite(sashForm, SWT.NONE);
        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTreeDetails.setLayout(treeColumnLayout);

        treeViewer = new LogRecordTreeViewer(compositeTreeDetails, SWT.BORDER, eventBroker);
        treeViewer.setContentProvider(new LogRecordTreeViewerContentProvider());
        treeViewer.getTree().setToolTipText("");
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
                    LoggerSingleton.logError(e);
                }
            }
        });

        if (launcherWatched != null) {
            treeViewer.reset(new ArrayList<XmlLogRecord>());
            Job job = new Job(StringConstants.PA_LOADING_LOGS) {

                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    final int logSize = currentRecords.size();
                    monitor.beginTask(StringConstants.PA_LOADING_LOGS + "...", logSize + 1);
                    sync.syncExec(new Runnable() {

                        @Override
                        public void run() {
                            compositeTreeContainer.setRedraw(false);
                        }

                    });
                    for (int index = 0; index < logSize; index++) {
                        final XmlLogRecord record = currentRecords.get(index);
                        sync.syncExec(new Runnable() {
                            public void run() {
                                try {
                                    treeViewer.addRecord(record);
                                    Thread.sleep(0);
                                } catch (InterruptedException e) {
                                    LoggerSingleton.logError(e);
                                    monitor.setCanceled(true);
                                }
                            }
                        });

                        if (record.getMessage() != null && !record.getMessage().isEmpty()) {
                            monitor.subTask(StringConstants.PA_LOADING_LOG + " " + Integer.toString(index + 1) + " of "
                                    + logSize + "...");
                        }

                        monitor.worked(1);
                    }
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            compositeTreeContainer.setRedraw(true);
                        }
                    });

                    stopAdding = false;
                    isBusy = false;
                    monitor.worked(1);
                    return Status.OK_STATUS;
                }
            };
            job.setUser(true);
            job.schedule();
        } else {
            treeViewer.reset(new ArrayList<XmlLogRecord>());
            stopAdding = false;
            isBusy = false;
        }
    }

    private void showFailureTreeLogMessage(ILogParentTreeNode logParentTreeNode) throws Exception {
        if (logParentTreeNode.getResult() != null) {
            XmlLogRecord result = logParentTreeNode.getResult();
            LogLevel resultLevel = (LogLevel) logParentTreeNode.getResult().getLevel();
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
                                    exceptionLogString = exceptionLogEntry.toString().replace(
                                            exceptionLogEntry.getClassName(), testCaseId);
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
                        txtMessage
                                .removeListener(
                                        SWT.MouseDown,
                                        txtMessage.getListeners(SWT.MouseDown)[txtMessage.getListeners(SWT.MouseDown).length - 1]);
                    }

                    txtMessage.addListener(SWT.MouseDown, mouseDownListener);
                }
            } else {
                txtMessage.setText(result.getMessage());
            }
        } else {
            txtMessage.setText("");
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

    private void showTreeLogProperties() throws Exception {
        StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
        if (selection == null) return;

        if (selection.getFirstElement() instanceof ILogTreeNode) {
            ILogTreeNode logTreeNode = (ILogTreeNode) selection.getFirstElement();
            if (logTreeNode instanceof ILogParentTreeNode) {
                ILogParentTreeNode logParentTreeNode = (ILogParentTreeNode) logTreeNode;

                txtStartTime.setText(logParentTreeNode.getRecordStart().toString());
                if (logParentTreeNode.getRecordEnd() != null) {
                    txtEndTime.setText(logParentTreeNode.getRecordEnd().toString());
                } else {
                    txtEndTime.setText("");
                }

                txtEslapedTime.setText(logParentTreeNode.getFullElapsedTime());
                StyledString styledString = new StyledString(txtEslapedTime.getText(), StyledString.COUNTER_STYLER);
                txtEslapedTime.setStyleRanges(styledString.getStyleRanges());

                showFailureTreeLogMessage(logParentTreeNode);

            } else {
                txtStartTime.setText("");
                txtEndTime.setText("");
                txtEslapedTime.setText("");
                txtMessage.setText(logTreeNode.getMessage());
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
        Composite compositeTreeNodeProperties = new Composite(sashForm, SWT.BORDER);
        compositeTreeNodeProperties.setLayout(new GridLayout(2, false));
        compositeTreeNodeProperties.setBackground(ColorUtil.getWhiteBackgroundColor());

        Label lblLogStart = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblLogStart.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblLogStart.setText(StringConstants.PA_LBL_START);

        txtStartTime = new StyledText(compositeTreeNodeProperties, SWT.BORDER);
        txtStartTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtStartTime.setEditable(false);

        Label lblLogEnd = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblLogEnd.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblLogEnd.setText(StringConstants.PA_LBL_END);

        txtEndTime = new StyledText(compositeTreeNodeProperties, SWT.BORDER);
        txtEndTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtEndTime.setEditable(false);

        Label lblLogRunTime = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblLogRunTime.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblLogRunTime.setText(StringConstants.PA_LBL_ELAPSED_TIME);

        txtEslapedTime = new StyledText(compositeTreeNodeProperties, SWT.BORDER);
        txtEslapedTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtEslapedTime.setEditable(false);

        Label lblMessage = new Label(compositeTreeNodeProperties, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblMessage.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblMessage.setText(StringConstants.PA_LBL_MESSAGE);

        txtMessage = new StyledText(compositeTreeNodeProperties, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        txtMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtMessage.setEditable(false);
        setWrapTxtMessage();
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
        btnShowAllLogs.setText(StringConstants.PA_TIP_ALL);
        btnShowAllLogs.setToolTipText(StringConstants.PA_TIP_ALL);
        btnShowAllLogs.setImage(ImageConstants.IMG_16_LOGVIEW_ALL);

        btnShowInfoLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowInfoLogs.setText(StringConstants.PA_TIP_INFO);
        btnShowInfoLogs.setToolTipText(StringConstants.PA_TIP_INFO);
        btnShowInfoLogs.setImage(ImageConstants.IMG_16_LOGVIEW_INFO);

        btnShowPassedLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowPassedLogs.setText(StringConstants.PA_TIP_PASSED);
        btnShowPassedLogs.setToolTipText(StringConstants.PA_TIP_PASSED);
        btnShowPassedLogs.setImage(ImageConstants.IMG_16_LOGVIEW_PASSED);

        btnShowFailedLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowFailedLogs.setText(StringConstants.PA_TIP_FAILED);
        btnShowFailedLogs.setToolTipText(StringConstants.PA_TIP_FAILED);
        btnShowFailedLogs.setImage(ImageConstants.IMG_16_LOGVIEW_FAILED);

        btnShowErrorLogs = new ToolItem(toolBar, SWT.CHECK);
        btnShowErrorLogs.setText(StringConstants.PA_TIP_ERROR);
        btnShowErrorLogs.setToolTipText(StringConstants.PA_TIP_ERROR);
        btnShowErrorLogs.setImage(ImageConstants.IMG_16_LOGVIEW_ERROR);

        btnShowAllLogs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performFilterTableLogs((ToolItem) e.getSource());
            }
        });

        btnShowInfoLogs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performFilterTableLogs((ToolItem) e.getSource());
            }
        });

        btnShowPassedLogs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performFilterTableLogs((ToolItem) e.getSource());
            }
        });

        btnShowFailedLogs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performFilterTableLogs((ToolItem) e.getSource());
            }
        });

        btnShowErrorLogs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performFilterTableLogs((ToolItem) e.getSource());
            }
        });

        updateTableButtons();
    }

    private void performFilterTableLogs(ToolItem button) {
        try {
            ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
                    PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
            if (button == btnShowAllLogs) {
                store.setValue(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS,
                        button.getSelection());
            } else if (button == btnShowInfoLogs) {
                store.setValue(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS,
                        button.getSelection());
            } else if (button == btnShowPassedLogs) {
                store.setValue(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS,
                        button.getSelection());
            } else if (button == btnShowFailedLogs) {
                store.setValue(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS,
                        button.getSelection());
            } else if (button == btnShowErrorLogs) {
                store.setValue(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS,
                        button.getSelection());
            }

            store.save();
            tableViewer.refresh();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void updateTableButtons() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);
        btnShowAllLogs.setSelection(store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS));
        btnShowInfoLogs.setSelection(store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS));
        btnShowPassedLogs.setSelection(store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS));
        btnShowFailedLogs.setSelection(store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS));
        btnShowErrorLogs.setSelection(store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS));
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
        tblclmnColumnLevel.setText("Level");

        TableViewerColumn tbViewerColumnTime = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnTime = tbViewerColumnTime.getColumn();
        tblclmnColumnTime.setText("Time");

        TableViewerColumn tbViewerColumnMessage = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnMessage = tbViewerColumnMessage.getColumn();
        tblclmnColumnMessage.setText("Messages");

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
                return "";
            }
        });

        tbViewerColumnLevel.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof XmlLogRecord) {
                    return ((XmlLogRecord) element).getLevel().getName();
                }
                return "";
            }
        });

        tbViewerColumnMessage.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof XmlLogRecord) {
                    return ((XmlLogRecord) element).getMessage();
                }
                return "";
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
        if (launcherWatched != null) {
            for (XmlLogRecord record : launcherWatched.getAllRecords()) {
                tableViewer.add(record);
            }
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
        stopAdding = false;
        isBusy = false;
    }

    private void showRecordProperties() {
        int index = table.getSelectionIndex();

        if (index == -1) {
            return; // no row selected
        }

        TableItem selectedItem = table.getItem(index);
        XmlLogRecord selectedRecord = (XmlLogRecord) selectedItem.getData();
        LogPropertyDialog dialog = new LogPropertyDialog(table.getDisplay().getActiveShell(), selectedRecord);
        dialog.open();
    }

    private void createControls(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        parent.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
        createStatusComposite(parent);
    }

    private void updateProgressBar(final XmlLogRecord testCaseResult) {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while (progress < INCREMENT) {
                    progressBar.setSelection(progressBar.getSelection() + 1);
                    progress++;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        LoggerSingleton.logError(e);
                    }
                }
                lblNumTestcases.setText(Integer.toString(progressBar.getSelection() / INCREMENT) + "/"
                        + Integer.toString(progressBar.getMaximum() / INCREMENT));
                // update
                if (testCaseResult.getLevel().equals(LogLevel.PASSED)) {
                    lblNumPasses.setText(Integer.toString(Integer.valueOf(lblNumPasses.getText()) + 1));
                } else if (testCaseResult.getLevel().equals(LogLevel.FAILED)) {
                    lblNumFailures.setText(Integer.toString(Integer.valueOf(lblNumFailures.getText()) + 1));

                    lblNumFailures.setForeground(ColorUtil.getFailedLogBackgroundColor());
                } else if (testCaseResult.getLevel().equals(LogLevel.ERROR)) {
                    lblNumErrors.setText(Integer.toString(Integer.valueOf(lblNumErrors.getText()) + 1));

                    lblNumErrors.setForeground(ColorUtil.getFailedLogBackgroundColor());
                }
                lblNumTestcases.getParent().getParent().layout();
            }
        });
    }

    private void resetProgressBar() {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                progressBar.setSelection(0);
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

    private void addRecords(List<XmlLogRecord> records) throws InterruptedException {
        while (isBusy) {
            if (stopAdding) return;
            // wait for refreshing records completely.
            Thread.sleep(100);
        }

        isBusy = true;
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstants.QUALIFIER);

        boolean showLogsAsTree = store
                .getBoolean(PreferenceConstants.ExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE);

        if (showLogsAsTree) {
            treeViewer.addRecords(records);
        } else {
            for (XmlLogRecord record : records) {
                tableViewer.add(record);
            }
        }
        currentRecords.addAll(records);
        isBusy = false;
    }

    private synchronized void changeObservedLauncher(Event event) throws Exception {
        if (stopAdding) return;
        stopAdding = true;

        while (isBusy) {
            Thread.sleep(100);
        }

        if (launcherWatched != null) {
            launcherWatched.setObserved(false);
            launcherWatched = null;
            currentRecords.clear();
        }

        Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        if (object != null && object instanceof String) {
            String launcherId = (String) object;
            for (AbstractLauncher launcher : LauncherManager.getInstance().getIDELaunchers()) {
                if (launcher.getId().equals(launcherId)) {
                    launcherWatched = launcher;
                    break;
                }
            }
            maxValue = launcherWatched.getTotalTestCase();
        }
        resetProgressBar();
        if (launcherWatched != null) {
            currentRecords.addAll(launcherWatched.getAllRecords());
            createLogViewerControl(parentComposite);
            launcherWatched.setObserved(true);
            setSelectedConsoleView();
        } else {
            createLogViewerControl(parentComposite);
        }
    }

    @Override
    public void handleEvent(Event event) {
        try {
            String topic = event.getTopic();
            switch (topic) {
                case EventConstants.CONSOLE_LOG_RESET: {
                    changeObservedLauncher(event);
                    break;
                }
                case EventConstants.CONSOLE_LOG_ADD_ITEMS: {
                    if (!stopAdding && launcherWatched != null) {
                        List<XmlLogRecord> logRecords = launcherWatched.getAllRecords();
                        if (currentRecords.size() < logRecords.size()) {
                            addRecords(logRecords.subList(currentRecords.size(), logRecords.size()));
                        }
                    }
                    break;
                }
                case EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR: {
                    Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                    if (object != null && object instanceof XmlLogRecord) {
                        updateProgressBar((XmlLogRecord) object);
                    }
                    break;
                }
                case EventConstants.CONSOLE_LOG_CHANGE_VIEW_TYPE: {
                    if (stopAdding) {
                        return;
                    }
                    stopAdding = true;
                    resetProgressBar();
                    createLogViewerControl(parentComposite);
                    break;
                }
                case EventConstants.CONSOLE_LOG_WORD_WRAP: {
                    setWrapTxtMessage();
                    break;
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void setWrapTxtMessage() {
        boolean wrap = preferenceStore.getBoolean(ExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP);
        if (txtMessage.getListeners(SWT.Modify).length == 0) {
            txtMessage.addListener(SWT.Modify, ControlUtils.getAutoHideStyledTextScrollbarListener);
            txtMessage.addListener(SWT.Resize, ControlUtils.getAutoHideStyledTextScrollbarListener);
        }
        txtMessage.setWordWrap(wrap);
        txtMessage.layout();
    }

    private void setSelectedConsoleView() {
        if (launcherWatched == null || launcherWatched.getLaunch() == null) {
            return;
        }

        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0];
        ConsoleView consoleView = (ConsoleView) workbenchPage.findView(IConsoleConstants.ID_CONSOLE_VIEW);

        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        if (manager.getConsoles() == null) return;

        for (IConsole console : manager.getConsoles()) {
            if (!(console instanceof ProcessConsole)) continue;
            if (launcherWatched.getLaunch() == ((ProcessConsole) console).getProcess().getLaunch()) {
                if (workbenchPage.isPartVisible(consoleView)) {
                    manager.showConsoleView(console);
                }
                return;
            }
        }
    }

}
