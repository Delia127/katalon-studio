package com.kms.katalon.composer.windows.dialog;

import static com.kms.katalon.composer.mobile.objectspy.dialog.MobileDeviceDialog.safeRoundDouble;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.openqa.selenium.NoSuchWindowException;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.windows.action.WindowsAction;
import com.kms.katalon.composer.windows.action.WindowsActionButtonWrapper;
import com.kms.katalon.composer.windows.action.WindowsActionHandler;
import com.kms.katalon.composer.windows.action.WindowsActionMapping;
import com.kms.katalon.composer.windows.element.BasicWindowsElement;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.SnapshotWindowsElement;
import com.kms.katalon.composer.windows.element.TreeWindowsElement;
import com.kms.katalon.composer.windows.exception.WindowsComposerException;
import com.kms.katalon.composer.windows.record.RecordedWindowsElementLabelProvider;
import com.kms.katalon.composer.windows.record.RecordedWindowsElementTableViewer;
import com.kms.katalon.composer.windows.spy.HighlightElementComposite;
import com.kms.katalon.composer.windows.spy.WindowsElementLabelProvider;
import com.kms.katalon.composer.windows.spy.WindowsElementPropertiesComposite;
import com.kms.katalon.composer.windows.spy.WindowsElementTreeContentProvider;
import com.kms.katalon.composer.windows.spy.WindowsInspectorController;
import com.kms.katalon.composer.windows.spy.WindowsRecordedStepsView;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.windows.driver.WindowsSession;
import com.kms.katalon.core.windows.keyword.helper.WindowsActionHelper;
import com.kms.katalon.tracking.service.Trackings;

public class WindowsRecorderDialog extends AbstractDialog implements WindowsObjectDialog {

    private FolderTreeEntity targetFolderEntity;

    private List<WindowsActionButtonWrapper> actionButtons = new ArrayList<>();

    private ToolItem btnStart, btnCapture, btnStop;

    private TreeViewer allElementTreeViewer;

    private WindowsScreenView screenComposite;

    private TreeWindowsElement appRootElement;

    private WindowsInspectorController inspectorController = new WindowsInspectorController();

    public WindowsInspectorController getInspectorController() {
        return inspectorController;
    }

    private Composite container;

    private WindowsElementPropertiesComposite propertiesComposite;

    private HighlightElementComposite highlightElementComposite;

    private Composite appsComposite;

    private WindowsAppComposite mobileComposite;

    private RecordedWindowsElementTableViewer capturedObjectsTableViewer;

    private WindowsRecordedStepsView stepView;

    private RecordActionResult recordActionResult;

    public WindowsRecorderDialog(Shell parentShell, WindowsAppComposite appComposite) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
        this.mobileComposite = appComposite;
    }

    @Override
    public boolean close() {
        stopObjectInspectorAction();
        boolean result = super.close();
        Trackings.trackCloseRecord("windows", "cancel", 0);
        return result;
    }

    @Override
    public void create() {
        super.create();
        initializeData();
        validateToEnableStartButton();
        targetElementChanged(null);
        updateActionButtonsVisibility(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(1200, 800);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle);
        setBlockOnOpen(true);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Windows Action Recorder");
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(createNoMarginGridLayout());
        container.setBackground(ColorUtil.getCompositeBackgroundColorForDialog());

        SashForm sashForm = createMainSashForm(container);
        sashForm.setBackground(ColorUtil.getCompositeBackgroundColorForSashform());
        populateSashForm(sashForm);
        sashForm.setWeights(getSashFormChildsWeights());

        return container;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        Control[] children = ((Composite) buttonBar).getChildren();
        for (Control child : children) {
            if (child instanceof HelpCompositeForDialog) {
                Composite helpComposite = (Composite) child;
                helpComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
                GridLayout layout = (GridLayout) helpComposite.getLayout();
                layout.marginBottom = 0;
                layout.marginRight = 0;
                helpComposite.getParent().layout(true, true);
                break;
            }
        }

        return buttonBar;
    }

    @Override
    protected void registerControlModifyListeners() {
        // do nothing here
    }

    @Override
    protected void setInput() {
        stepView.setCapturedElementsTableViewer(capturedObjectsTableViewer);
    }

    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return "";
    }

    @Override
    protected void okPressed() {
        recordActionResult = new RecordActionResult(stepView.getWrapper(),
                capturedObjectsTableViewer.getCapturedElements());

        int recordedActionCount = stepView.getNodes().size();

        super.okPressed();

        Trackings.trackCloseRecord("windows", "ok", recordedActionCount);
    }

    public FolderTreeEntity getTargetFolderEntity() {
        return targetFolderEntity;
    }

    protected GridLayout createNoMarginGridLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }

    protected int[] getSashFormChildsWeights() {
        return new int[] { 5, 4, 6 };
    }

    /**
     * Create the main sash form
     * 
     * @return
     */
    protected SashForm createMainSashForm(Composite container) {
        SashForm sashForm = new SashForm(container, SWT.NONE);
        sashForm.setSashWidth(3);
        sashForm.setLayout(new FillLayout());
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return sashForm;
    }

    /**
     * Populate the main sash form
     * 
     * @param sashForm
     */
    protected void populateSashForm(SashForm sashForm) {
        createContentComposite(sashForm);
        createMiddlePaneComposite(sashForm);

        screenComposite = new WindowsScreenView(this);
        screenComposite.createControls(sashForm);
    }

    private void createMiddlePaneComposite(SashForm sashForm) {
        Composite middlePane = new Composite(sashForm, SWT.NONE);
        middlePane.setLayout(createNoMarginGridLayout());

        SashForm hSashForm = new SashForm(middlePane, SWT.VERTICAL);
        hSashForm.setSashWidth(3);
        hSashForm.setBackground(ColorUtil.getCompositeBackgroundColorForSashform());
        hSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createActionListComposite(hSashForm);

        createMiddleBottomComposite(hSashForm);

        hSashForm.setWeights(new int[] { 3, 7 });
    }

    private void createMiddleBottomComposite(Composite parent) {
        createAllObjectsComposite(parent);
    }

    private Control createPropertiesComposite(Composite parent) {
        propertiesComposite = new WindowsElementPropertiesComposite(this);
        Control control = propertiesComposite.createObjectPropertiesComposite(parent);
        return control;
    }

    private Control createHighlightElementComposite(Composite parent) {
        highlightElementComposite = new HighlightElementComposite(this);
        Control control = highlightElementComposite.createComposite(parent);
        return control;
    }

    private Composite createCapturedObjectsComposite(Composite parent) {
        Composite capturedObjectsComposite = new Composite(parent, SWT.NONE);
        capturedObjectsComposite.setLayout(new GridLayout());

        Label lblRecordedActions = new Label(capturedObjectsComposite, SWT.NONE);
        lblRecordedActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText("CAPTURED OBJECTS");

        Composite capturedObjectTableComposite = new Composite(capturedObjectsComposite, SWT.NONE);
        capturedObjectTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout tbclCapturedObjects = new TableColumnLayout();
        capturedObjectTableComposite.setLayout(tbclCapturedObjects);

        capturedObjectsTableViewer = new RecordedWindowsElementTableViewer(capturedObjectTableComposite,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        Table capturedObjectsTable = capturedObjectsTableViewer.getTable();
        capturedObjectsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        capturedObjectsTable.setHeaderVisible(true);
        capturedObjectsTable.setLinesVisible(ControlUtils.shouldLineVisble(capturedObjectsTable.getDisplay()));

        TableViewerColumn tableViewerColumnCapturedObjects = new TableViewerColumn(capturedObjectsTableViewer,
                SWT.NONE);
        TableColumn tblclmnCapturedObjects = tableViewerColumnCapturedObjects.getColumn();
        tblclmnCapturedObjects.setText(StringConstants.NAME);
        tableViewerColumnCapturedObjects.setLabelProvider(new RecordedWindowsElementLabelProvider());
        tbclCapturedObjects.setColumnData(tblclmnCapturedObjects, new ColumnWeightData(98, 100));

        capturedObjectsTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        capturedObjectsTableViewer.setCaptureElements(new ArrayList<>());

        capturedObjectsTable.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(capturedObjectsTableViewer);

        capturedObjectsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                CapturedWindowsElement firstElement = (CapturedWindowsElement) selection.getFirstElement();
                propertiesComposite.setEditingElement(firstElement);
                highlightElementComposite.setEditingElement(firstElement);
                highlightObject(firstElement);
            }
        });

        return capturedObjectsComposite;
    }

    private Composite createRecordedActionComposite(Composite parent) {
        stepView = new WindowsRecordedStepsView();
        Composite compositeStepView = stepView.createContent(parent);

        return compositeStepView;
    }

    private void createActionListComposite(SashForm sashForm) {
        Composite actionListComposite = new Composite(sashForm, SWT.NONE);
        actionListComposite.setLayout(new GridLayout());

        Label lblRecordedActions = new Label(actionListComposite, SWT.NONE);
        lblRecordedActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText("POSSIBLE ACTIONS");

        Composite buttonsComposite = new Composite(actionListComposite, SWT.NONE);
        RowLayout layout = new RowLayout();
        buttonsComposite.setLayout(layout);
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        actionButtons.addAll(Arrays.asList(WindowsAction.values())
                .stream()
                .filter(action -> action.isUserInputAction())
                .map(action -> {
                    return new WindowsActionButtonWrapper(buttonsComposite, action, (event) -> {
                        try {
                            SnapshotWindowsElement element = !allElementTreeViewer.getStructuredSelection().isEmpty()
                                    ? (SnapshotWindowsElement) allElementTreeViewer.getStructuredSelection()
                                            .getFirstElement()
                                    : null;

                            WindowsActionMapping actionMapping = performAction(action, element);
                            if (actionMapping == null || actionMapping.getAction().isCanceled()) {
                                return;
                            }
                            if (actionMapping.getTargetElement() != null) {
                                CapturedWindowsElement targetElement = capturedObjectsTableViewer
                                        .addCapturedObject(actionMapping.getTargetElement());
                                actionMapping.setTargetElement(targetElement);
                            }
                            stepView.addNode(actionMapping);
                            targetElementChanged(null);
                            allElementTreeViewer.setSelection(StructuredSelection.EMPTY);

                            if (isApplicationOpened()) {
                                captureObjectAction();
                            } else {
                                MessageDialog.openInformation(getShell(), "Information", "Application closed");
                                stopObjectInspectorAction();
                            }
                        } catch (StepFailedException | WindowsComposerException | ClassNotFoundException e) {
                            MultiStatusErrorDialog.showErrorDialog(
                                    "Unable to perform action: " + action.getReadableName(), e.getMessage(),
                                    ExceptionsUtil.getStackTraceForThrowable(e));
                        }
                    });
                })
                .collect(Collectors.toList()));
    }

    private boolean isApplicationOpened() {
        try {
            inspectorController.getDriver().getWindowHandle();
            return true;
        } catch (NoSuchWindowException e) {
            return false;
        }
    }

    private WindowsActionMapping performAction(WindowsAction action, SnapshotWindowsElement element)
            throws WindowsComposerException {
        WindowsActionHandler actionHandler = new WindowsActionHandler(inspectorController.getWindowsSession(), action);
        return actionHandler.perform(element, getShell());
    }

    private void targetElementChanged(CapturedWindowsElement basicWindowsElement) {
        // propertiesComposite.setEditingElement(basicWindowsElement);
        updateActionButtonsVisibility(basicWindowsElement);
    }

    public void updateActionButtonsVisibility(BasicWindowsElement basicWindowsElement) {
        UISynchronizeService.syncExec(() -> {
            actionButtons.stream().forEach(actionButton -> {
                WindowsAction action = actionButton.getWindowsAction();
                actionButton.setEnabledButton(inspectorController.getDriver() != null
                        && (!action.hasElement() || basicWindowsElement != null));
            });
        });
    }

    private void createContentComposite(SashForm sashForm) {
        Composite contentComposite = new Composite(sashForm, SWT.NONE);
        contentComposite.setLayout(createNoMarginGridLayout());

        addStartStopToolbar(contentComposite);

        createSettingComposite(contentComposite);

        CTabFolder leftBottomTabFolder = new CTabFolder(contentComposite, SWT.NONE);
        leftBottomTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        CTabItem recordedActionTabItem = new CTabItem(leftBottomTabFolder, SWT.NONE);
        recordedActionTabItem.setText("Recorded Actions");
        Control recordedActionControl = createRecordedActionComposite(leftBottomTabFolder);
        recordedActionTabItem.setControl(recordedActionControl);

        CTabItem capturedObjectsTabItem = new CTabItem(leftBottomTabFolder, SWT.NONE);
        SashForm hSashForm = new SashForm(leftBottomTabFolder, SWT.VERTICAL);
        hSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        createCapturedObjectsComposite(hSashForm);
        createPropertiesComposite(hSashForm);
        createHighlightElementComposite(hSashForm);
        hSashForm.setWeights(new int[] { 4, 6, 1 });

        capturedObjectsTabItem.setControl(hSashForm);
        capturedObjectsTabItem.setText("Captured Objects");

        leftBottomTabFolder.setSelection(recordedActionTabItem);
    }

    private void createSettingComposite(Composite parent) {
        Composite settingComposite = new Composite(parent, SWT.NONE);
        settingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glSettingComposite = new GridLayout(2, false);
        glSettingComposite.horizontalSpacing = 10;
        settingComposite.setLayout(glSettingComposite);

        Label lblConfiguration = new Label(settingComposite, SWT.NONE);
        lblConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        lblConfiguration.setFont(getFontBold(lblConfiguration));
        lblConfiguration.setText("CONFIGURATIONS");

        appsComposite = new Composite(settingComposite, SWT.NONE);
        appsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        appsComposite.setLayout(new FillLayout());

        mobileComposite.createComposite(appsComposite, SWT.NONE, this);
    }

    @Override
    public void refreshButtonsState() {
        validateToEnableStartButton();
    }

    public void initializeData() {
        UISynchronizeService.asyncExec(() -> {
            try {
                mobileComposite.setInput();
            } catch (InvocationTargetException exception) {
                Throwable targetException = exception.getTargetException();
                LoggerSingleton.logError(targetException);
                MultiStatusErrorDialog.showErrorDialog(targetException, "Error",
                        targetException.getClass().getSimpleName());
            } catch (InterruptedException ignored) {
                // ignore this
            } finally {
                validateToEnableStartButton();
            }
        });
    }

    private void startObjectInspectorAction() {
        // Temporary disable Start button while launching app
        btnStart.setEnabled(false);
        final AppiumMonitorDialog progressDlg = new AppiumMonitorDialog(getShell()) {
            @Override
            public void cancelPressed() {
                super.cancelPressed();
                finishedRun();
                getProgressMonitor().done();
                setStartStopButtonsState(true);
            }
        };
        inspectorController.setStreamHandler(progressDlg);
        try {
            WindowsActionMapping actionMapping = mobileComposite.startApp(inspectorController, progressDlg);
            try {
                if (!progressDlg.getProgressMonitor().isCanceled()) {
                    captureObjectAction();

                    setStartStopButtonsState(false);
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                    stepView.refreshTree();
                    
                    stepView.addNode(actionMapping);
                } else {
                    stopObjectInspectorAction();
                    setStartStopButtonsState(true);
                }
            } catch (ClassNotFoundException e) {
                throw new InvocationTargetException(e);
            }
            targetElementChanged(null);

            // send event for tracking
            Trackings.trackRecord("windows");
        } catch (Exception ex) {
            // If user intentionally cancel the progress, don't need to show error message
            if (ex instanceof InvocationTargetException) {
                LoggerSingleton.logError(ex);
                Throwable targetException = ((InvocationTargetException) ex).getTargetException();
                String message = (targetException instanceof java.util.concurrent.ExecutionException)
                        ? targetException.getCause().getMessage() : targetException.getMessage();
                UISynchronizeService.syncExec(() -> {
                    MultiStatusErrorDialog.showErrorDialog("Unable to start application", message,
                            ExceptionsUtil.getStackTraceForThrowable(targetException));
                });
            }

            // Enable start button and show error dialog if application cannot start
            setStartStopButtonsState(true);
        } finally {
            inspectorController.setStreamHandler(null);
        }
    }
    
    private void setStartStopButtonsState(boolean isReadyToStart) {
        if (isReadyToStart) {
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnCapture.setEnabled(false);
        } else {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnCapture.setEnabled(true);
        }
    }

    public String getAppName() {
        return mobileComposite.getAppName();
    }

    private void captureObjectAction() {
        final ProgressMonitorDialogWithThread dialog = new ProgressMonitorDialogWithThread(getShell());

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Capturing Windows objects", IProgressMonitor.UNKNOWN);

                TreeWindowsElement newAppRootElement = (TreeWindowsElement) dialog.runAndWait(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        TreeWindowsElement appRootElement = inspectorController.getWindowsObjectRoot();
                        return appRootElement;
                    }
                });

                checkMonitorCanceled(monitor);

                refreshTreeElements(dialog, newAppRootElement);

                updateActionButtonsVisibility(null);

                String imgPath = captureImage();

                checkMonitorCanceled(monitor);

                refreshDeviceView(imgPath, newAppRootElement);

                appRootElement = newAppRootElement;

                monitor.done();
            }

            private void refreshTreeElements(final ProgressMonitorDialogWithThread dialog,
                    final TreeWindowsElement newAppRootElement) {
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        allElementTreeViewer.setInput(new Object[] { newAppRootElement });
                        allElementTreeViewer.refresh();
                        allElementTreeViewer.expandAll();
                    }
                });
            }

            private void refreshDeviceView(String imgPath, final TreeWindowsElement newAppRootElement) {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    screenComposite.refreshDialog(imgFile);
                }
            }

            private String captureImage() throws InvocationTargetException {
                // Render Screenshot
                try {
                    return inspectorController.captureScreenshot();
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }
        };

        try {
            btnCapture.setEnabled(false);
            dialog.run(true, false, runnable);
        } catch (InterruptedException ignored) {
            // User canceled
        } catch (InvocationTargetException e) {
            if (!isApplicationOpened()) {
                stopObjectInspectorAction();
                MessageDialog.openInformation(getShell(), "Information", "Application closed");
            } else {
                LoggerSingleton.logError(e);
                Throwable exception = e.getTargetException();
                MultiStatusErrorDialog.showErrorDialog(exception, "Could not capture Windows element",
                        exception.getClass().getSimpleName());
            }
        } finally {
            validateToEnableStartButton();
        }
    }

    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException("Operation has been canceled");
        }
    }

    private void stopObjectInspectorAction() {
        // Close application
        try {
            WindowsSession appSession = inspectorController.getWindowsSession();
            if (appSession != null) {
                WindowsActionHelper actionHelper = new WindowsActionHelper(appSession);
                actionHelper.closeApp();
            }
        } catch (NoSuchWindowException exception) {
            // The application is already closed
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Quit Driver
                if (inspectorController.getDriver() != null) {
                    inspectorController.getDriver().quit();
                }
            }
        });
        thread.start();
        final Shell shell = getShell();
        if (shell != null && !shell.isDisposed()) {
            inspectorController.resetDriver();
            // Update UI
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnCapture.setEnabled(false);

            allElementTreeViewer.setInput(new Object[] {});
            allElementTreeViewer.refresh();
            targetElementChanged(null);
            try {
                stepView.refreshTree();
            } catch (InvocationTargetException | InterruptedException e) {}
            screenComposite.refreshDialog(null);
        }
    }

    private void validateToEnableStartButton() {
        boolean ableToStart = mobileComposite.isAbleToStart();
        btnStart.setEnabled(ableToStart && inspectorController.getDriver() == null);
        btnStop.setEnabled(inspectorController.getDriver() != null);
        btnCapture.setEnabled(inspectorController.getDriver() != null);
    }

    private void addStartStopToolbar(Composite contentComposite) {
        Composite toolbarComposite = new Composite(contentComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar contentToolbar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        contentToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        contentToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        btnCapture = new ToolItem(contentToolbar, SWT.NONE);
        btnCapture.setImage(ImageManager.getImage(IImageKeys.REFRESH_24));
        btnCapture.setDisabledImage(ImageManager.getImage(IImageKeys.REFRESH_DISABLED_24));
        btnCapture.setText("Refresh Screen");
        btnCapture.setToolTipText("Refresh Screen");
        btnCapture.setEnabled(false);
        btnCapture.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                captureObjectAction();
            }
        });

        btnStart = new ToolItem(contentToolbar, SWT.NONE);
        btnStart.setImage(ImageManager.getImage(IImageKeys.PLAY_24));
        btnStart.setDisabledImage(ImageManager.getImage(IImageKeys.PLAY_DISABLED_24));
        btnStart.setText("Start");
        btnStart.setToolTipText("Start");
        btnStart.setEnabled(false);
        btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Validate all required informations are filled
                if (validateAppSetting()) {
                    startObjectInspectorAction();
                    // updateActionButtonsVisibility(propertiesComposite.getEditingElement());
                }
            }
        });

        btnStop = new ToolItem(contentToolbar, SWT.NONE);
        btnStop.setImage(ImageManager.getImage(IImageKeys.STOP_24));
        btnStop.setDisabledImage(ImageManager.getImage(IImageKeys.STOP_DISABLED_24));
        btnStop.setText(GlobalStringConstants.STOP);
        btnStop.setToolTipText(GlobalStringConstants.STOP);
        btnStop.setEnabled(false);
        btnStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stopObjectInspectorAction();
            }
        });
    }

    private boolean validateAppSetting() {
        return mobileComposite.validateSetting();
    }

    private Composite createAllObjectsComposite(Composite parentComposite) {
        Composite allObjectsComposite = new Composite(parentComposite, SWT.NONE);
        allObjectsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        allObjectsComposite.setLayout(new GridLayout());

        Label lblRecordedActions = new Label(allObjectsComposite, SWT.NONE);
        lblRecordedActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText("SCREEN OBJECTS");

        Composite allObjectsTreeComposite = new Composite(allObjectsComposite, SWT.NONE);
        allObjectsTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        allObjectsTreeComposite.setLayout(new FillLayout());

        allElementTreeViewer = new CTreeViewer(allObjectsTreeComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        allElementTreeViewer.setLabelProvider(new WindowsElementLabelProvider());
        allElementTreeViewer.setContentProvider(new WindowsElementTreeContentProvider());

        allElementTreeViewer.getTree().setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(allElementTreeViewer, ToolTip.NO_RECREATE);

        allElementTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (allElementTreeViewer.getStructuredSelection().isEmpty()) {
                    updateActionButtonsVisibility(null);
                } else {
                    BasicWindowsElement element = (BasicWindowsElement) allElementTreeViewer.getStructuredSelection()
                            .getFirstElement();
                    highlightObject(element);
                    updateActionButtonsVisibility(element);
                }
            }
        });

        Tree tree = (Tree) allElementTreeViewer.getControl();

        Listener listener = new Listener() {

            @Override
            public void handleEvent(Event event) {
                TreeItem treeItem = (TreeItem) event.item;
                final TreeColumn[] treeColumns = treeItem.getParent().getColumns();
                UISynchronizeService.syncExec(new Runnable() {

                    @Override
                    public void run() {
                        for (TreeColumn treeColumn : treeColumns) {
                            treeColumn.pack();
                        }
                    }
                });
            }
        };

        tree.addListener(SWT.Expand, listener);

        return allObjectsComposite;
    }

    // Highlight Selected object on captured screenshot
    private void highlightObject(BasicWindowsElement selectedElement) {
        if (selectedElement == null || screenComposite == null || screenComposite.isDisposed()) {
            return;
        }

        screenComposite.highlightElement(selectedElement);
    }

    @Override
    public void highlightElementRects(List<Rectangle> rects) {
        screenComposite.highlightRects(rects);
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    @Override
    public void setSelectedElementByLocation(int x, int y) {
        if (appRootElement == null) {
            return;
        }
        final TreeWindowsElement foundElement = recursivelyFindElementByLocation(appRootElement, x, y);
        if (foundElement == null) {
            return;
        }
        highlightObject(foundElement);
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                getShell().setFocus();
                allElementTreeViewer.getTree().setFocus();
                allElementTreeViewer.setSelection(new StructuredSelection(foundElement));
            }
        });
    }

    /**
     * Recursively find element that is positioned at [x, y]. This is by assumed that mobile elements don't overlap each
     * other.
     * 
     * @param currentElement
     * @param x
     * @param y
     * @return element that were found
     */
    private TreeWindowsElement recursivelyFindElementByLocation(TreeWindowsElement currentElement, int x, int y) {
        if (!screenComposite.isElementOnScreen((double) x, (double) y, (double) 1, (double) 1)) {
            return null;
        }
        for (TreeWindowsElement childElement : currentElement.getChildren()) {
            Map<String, String> attributes = childElement.getProperties();
            Double elementX = Double.parseDouble(attributes.get(GUIObject.X));
            Double elementY = Double.parseDouble(attributes.get(GUIObject.Y));
            Double elementWidth = Double.parseDouble(attributes.get(GUIObject.WIDTH));
            Double elementHeight = Double.parseDouble(attributes.get(GUIObject.HEIGHT));
            Rectangle rectangle = new Rectangle(safeRoundDouble(elementX), safeRoundDouble(elementY),
                    safeRoundDouble(elementWidth), safeRoundDouble(elementHeight));
            if (rectangle.contains(x, y)) {
                return recursivelyFindElementByLocation(childElement, x, y);
            }
        }
        return currentElement;
    }

    public void updateSelectedElement(CapturedWindowsElement selectedElement) {
        capturedObjectsTableViewer.refresh(selectedElement);
        try {
            stepView.refreshTree();
        } catch (InvocationTargetException | InterruptedException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public RecordActionResult getRecordActionResult() {
        return recordActionResult;
    }

    public class RecordActionResult {
        private final List<CapturedWindowsElement> windowsElements;

        private final ScriptNodeWrapper script;

        public RecordActionResult(ScriptNodeWrapper script, List<CapturedWindowsElement> windowsElements) {
            this.script = script;
            this.windowsElements = windowsElements;
        }

        public List<CapturedWindowsElement> getWindowsElements() {
            return windowsElements;
        }

        public ScriptNodeWrapper getScript() {
            return script;
        }
    }
}