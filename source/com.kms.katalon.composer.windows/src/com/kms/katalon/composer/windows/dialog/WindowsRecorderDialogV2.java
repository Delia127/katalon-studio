package com.kms.katalon.composer.windows.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.windows.action.WindowsAction;
import com.kms.katalon.composer.windows.action.WindowsActionMapping;
import com.kms.katalon.composer.windows.constant.ComposerWindowsMessage;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.nativerecorder.NativeRecorderDriver;
import com.kms.katalon.composer.windows.record.RecordedWindowsElementLabelProvider;
import com.kms.katalon.composer.windows.record.RecordedWindowsElementTableViewer;
import com.kms.katalon.composer.windows.socket.WindowsServerSocketMessage.ServerMessageType;
import com.kms.katalon.composer.windows.socket.WindowsSocketMessageUtil;
import com.kms.katalon.composer.windows.socket.WindowsSocketServer;
import com.kms.katalon.composer.windows.socket.WindowsStartRecordingPayload;
import com.kms.katalon.composer.windows.socket.WindowsStopRecordingPayload;
import com.kms.katalon.composer.windows.spy.WindowsElementPropertiesComposite;
import com.kms.katalon.composer.windows.spy.WindowsInspectorController;
import com.kms.katalon.composer.windows.spy.WindowsRecordedStepsView;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.tracking.service.Trackings;

public class WindowsRecorderDialogV2 extends AbstractDialog implements WindowsObjectDialog {

    private ToolItem btnStart;

    private ToolItem btnStop;

    private boolean isStarting = false;

    private Composite container;

    private WindowsAppComposite mobileComposite = new WindowsAppComposite();

    private RecordedWindowsElementTableViewer capturedObjectsTableViewer;

    private WindowsRecordedStepsView stepView;

    private WindowsElementPropertiesComposite propertiesComposite;

    private WindowsSocketServer socketServer = new WindowsSocketServer(this);

    private NativeRecorderDriver nativeRecorderDriver = new NativeRecorderDriver();

    private RecordActionResult recordActionResult;

    private boolean okPressed = false;

    public WindowsRecorderDialogV2(Shell parentShell) {
        super(parentShell);
        socketServer.start();
        startNativeRecorderDriver();
    }

    @Override
    public String getDialogTitle() {
        return ComposerWindowsMessage.DLG_TITLE_KATALON_NATIVE_RECORDER;
    }

    @Override
    public int open() {
        Trackings.trackOpenWindowsNativeRecord();
        return super.open();
    }
    
    @Override
    public boolean close() {
        socketServer.close();
        if (okPressed) {
            int recordedActionCount = stepView.getNodes().size();
            Trackings.trackCloseWindowsNativeRecordByOk(recordedActionCount);
        } else {
            Trackings.trackCloseWindowsNativeRecordByCancel();
        }
        return super.close();
    }

    @Override
    protected void okPressed() {
        recordActionResult = new RecordActionResult(stepView.getWrapper(),
                capturedObjectsTableViewer.getCapturedElements());

        okPressed  = true;
        
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 600);
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        Shell parentShell = Display.getCurrent().getActiveShell();
        Rectangle parentSize = parentShell.getBounds();
        int locationX = (parentSize.width - initialSize.x) / 2 + parentSize.x;
        int locationY = (parentSize.height - initialSize.y) / 2 + parentSize.y;
        return new Point(locationX, locationY);
    }

    @Override
    protected int getShellStyle() {
        if (!Platform.OS_LINUX.equals(Platform.getOS())) {
            return SWT.SHELL_TRIM;
        } else {
            return SWT.SHELL_TRIM | SWT.CENTER;
        }
    }

    @Override
    protected void registerControlModifyListeners() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setInput() {
        setButtonStates();
        stepView.setCapturedElementsTableViewer(capturedObjectsTableViewer);

        try {
            mobileComposite.setInput();
        } catch (InvocationTargetException | InterruptedException e) {}
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(createNoMarginGridLayout());
        container.setBackground(ColorUtil.getCompositeBackgroundColorForDialog());

        SashForm sashForm = createMainSashForm(container);
        sashForm.setBackground(ColorUtil.getCompositeBackgroundColorForSashform());
        populateSashForm(sashForm);
        // sashForm.setWeights(new int[] { 4, 6 });

        return container;
    }

    protected void populateSashForm(SashForm sashForm) {
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
        hSashForm.setWeights(new int[] { 4, 6 });

        capturedObjectsTabItem.setControl(hSashForm);
        capturedObjectsTabItem.setText("Captured Objects");

        leftBottomTabFolder.setSelection(recordedActionTabItem);
    }

    private Composite createRecordedActionComposite(Composite parent) {
        stepView = new WindowsRecordedStepsView();
        Composite compositeStepView = stepView.createContent(parent);

        return compositeStepView;
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
            }
        });

        return capturedObjectsComposite;
    }

    private Control createPropertiesComposite(Composite parent) {
        propertiesComposite = new WindowsElementPropertiesComposite(this);
        Control control = propertiesComposite.createObjectPropertiesComposite(parent);
        return control;
    }

    private void addStartStopToolbar(Composite contentComposite) {
        Composite toolbarComposite = new Composite(contentComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar contentToolbar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        contentToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        contentToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        btnStart = new ToolItem(contentToolbar, SWT.NONE);
        btnStart.setImage(ImageManager.getImage(IImageKeys.PLAY_24));
        btnStart.setDisabledImage(ImageManager.getImage(IImageKeys.PLAY_DISABLED_24));
        btnStart.setText("Start");
        btnStart.setToolTipText("Start");
        btnStart.setEnabled(false);
        btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isStarting = true;
                setButtonStates();
                try {
                    mobileComposite.saveSettings();
                    startRecording();
                } catch (Exception ex) {
                    isStarting = false;
                    setButtonStates();
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
                try {
                    isStarting = false;
                    setButtonStates();

                    closeApplication();
                    stopRecordind();
                } catch (Exception ex) {}
            }
        });
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
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

        Composite appsComposite = new Composite(settingComposite, SWT.NONE);
        appsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        appsComposite.setLayout(new FillLayout());

        mobileComposite.setShowConfiguration(false);
        mobileComposite.setShowApplicationTitle(false);
        mobileComposite.createComposite(appsComposite, SWT.NONE, this);
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

    protected GridLayout createNoMarginGridLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }

    private void startRecording() throws Exception {
        Trackings.trackWindowsNativeRecord();
        startNativeRecorderDriver();
        WindowsStartRecordingPayload message = WindowsSocketMessageUtil
                .createStartRecordingPayload(mobileComposite.getAppFile());
        String data = JsonUtil.toJson(message);
        socketServer.sendMessage(WindowsSocketMessageUtil.createServerMessage(ServerMessageType.START_RECORDING, data));
    }

    private void stopRecordind() throws Exception {
        socketServer.sendMessage(WindowsSocketMessageUtil.createServerMessage(ServerMessageType.STOP_RECORDING, ""));
    }

    private void closeApplication() throws IOException, ClassNotFoundException {
        WindowsActionMapping stopAction = new WindowsActionMapping(WindowsAction.CloseApplication, null);
        stepView.addNode(stopAction);

        WindowsStopRecordingPayload message = WindowsSocketMessageUtil
                .createStopRecordingPayload(mobileComposite.getAppFile());
        String data = JsonUtil.toJson(message);
        socketServer
                .sendMessage(WindowsSocketMessageUtil.createServerMessage(ServerMessageType.CLOSE_APPLICATION, data));
    }

    private void startNativeRecorderDriver() {
        try {
            boolean shouldStartANewClient = !socketServer.isClientConnected();
            nativeRecorderDriver.start(shouldStartANewClient);
        } catch (IOException exception) {
            LoggerSingleton.logError(exception);
        }
    }

    private void setButtonStates() {
        btnStart.setEnabled(!isStarting);
        btnStop.setEnabled(isStarting);
    }

    public void refreshButtonsState() {
        setButtonStates();
    }

    @Override
    public void setSelectedElementByLocation(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateSelectedElement(CapturedWindowsElement editingElement) {
        capturedObjectsTableViewer.refresh(editingElement);
        try {
            stepView.refreshTree();
        } catch (InvocationTargetException | InterruptedException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    public WindowsInspectorController getInspectorController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void highlightElementRects(List<Rectangle> rects) {
        // TODO Auto-generated method stub

    }

    public boolean isStarting() {
        return isStarting;
    }

    public void setStarting(boolean isStarting) {
        this.isStarting = isStarting;
    }

    public ToolItem getBtnStart() {
        return btnStart;
    }

    public ToolItem getBtnStop() {
        return btnStop;
    }

    public WindowsAppComposite getMobileComposite() {
        return mobileComposite;
    }

    public WindowsRecordedStepsView getStepView() {
        return stepView;
    }

    public RecordedWindowsElementTableViewer getCapturedObjectsTableViewer() {
        return capturedObjectsTableViewer;
    }

    public WindowsElementPropertiesComposite getPropertiesComposite() {
        return propertiesComposite;
    }

    public void addActionMapping(WindowsActionMapping actionMapping) {
        UISynchronizeService.syncExec(() -> {
            try {
                if (actionMapping.getTargetElement() != null) {
                    CapturedWindowsElement finalCapturedElement = capturedObjectsTableViewer
                            .addCapturedObject(actionMapping.getTargetElement());
                    actionMapping.setTargetElement(finalCapturedElement);
                }
                stepView.refreshTree();
                stepView.addNode(actionMapping);
            } catch (ClassNotFoundException | InvocationTargetException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
        });
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
