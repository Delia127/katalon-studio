package com.kms.katalon.composer.mobile.recorder.components;

import static com.kms.katalon.composer.mobile.objectspy.dialog.MobileDeviceDialog.safeRoundDouble;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileAction;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionParamValueType;
import com.kms.katalon.composer.mobile.objectspy.components.MobileAppComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileCapturedObjectsComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileConfigurationsComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileElementPropertiesComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileHighlightComposite;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileAppDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileDeviceDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileDeviceView;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileElementInspectorDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileInspectorController;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.preferences.MobileObjectSpyPreferencesHelper;
import com.kms.katalon.composer.mobile.objectspy.util.MobileActionHelper;
import com.kms.katalon.composer.mobile.recorder.composites.MobileAllObjectsComposite;
import com.kms.katalon.composer.mobile.recorder.composites.MobileRecordedActionsComposite;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecorderImageConstants;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecorderStringConstants;
import com.kms.katalon.composer.mobile.recorder.exceptions.MobileRecordException;
import com.kms.katalon.composer.testcase.ast.dialogs.ArgumentInputBuilderDialog;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputParameterBuilder;
import com.kms.katalon.composer.testcase.model.InputParameterClass;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;
import com.kms.katalon.core.testobject.MobileTestObject;
import com.kms.katalon.core.testobject.MobileTestObject.MobileLocatorStrategy;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.CryptoUtil;

public class MobileRecorderDialog extends AbstractDialog implements MobileElementInspectorDialog, MobileAppDialog {

    private MobileConfigurationsComposite mobileConfigurationsComposite;

    private MobileRecordedActionsComposite recordedActionsComposite;

    private MobileCapturedObjectsComposite capturedObjectsComposite;

    private MobileHighlightComposite highlightElementComposite;

    private MobileElementPropertiesComposite propertiesComposite;

    private MobileAllObjectsComposite allObjectsComposite;

    private MobileAppComposite mobileComposite;

    private FolderTreeEntity targetFolderEntity;

    private List<MobileActionButtonWrapper> actionButtons = new ArrayList<>();

    private ToolItem btnStart, btnCapture, btnStop;

    private MobileDeviceView deviceView;

    private TreeMobileElement appRootElement;

    private MobileInspectorController inspectorController = new MobileInspectorController();

    private MobileObjectSpyPreferencesHelper preferencesHelper = new MobileObjectSpyPreferencesHelper();

    private Composite container;

    private RecordActionResult recordActionResult;
    
    private boolean okPressed = false;

    public RecordActionResult getRecordActionResult() {
        return recordActionResult;
    }

    public MobileRecorderDialog(Shell parentShell, MobileAppComposite appComposite) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE);
        this.mobileComposite = appComposite;
    }

    @Override
    public boolean close() {
        stopObjectInspectorAction();
        try {
            preferencesHelper.save();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        
        if (okPressed) {
            Trackings.trackCloseMobileRecordByOk(getDeviceTypeString(), getRecordedActions().size());
        } else {
            Trackings.trackCloseMobileRecordByCancel(getDeviceTypeString());
        }
        
        boolean result = super.close();
        
        return result;
    }

    @Override
    public void create() {
        super.create();
        initializeData();
        validateToEnableStartButton();
        updateActionButtonsVisibility(null, getCurrentMobileDriverType());
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(1100, 800);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
        setBlockOnOpen(true);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MobileRecoderMessagesConstants.DLG_TITLE_MOBILE_RECORDER);
    }

    private int calculateDialogStartX(Rectangle displayBounds, Point dialogSize) {
        int dialogsWidth = dialogSize.x + MobileDeviceDialog.DIALOG_WIDTH;
        int startX = (displayBounds.width - dialogsWidth) / 2 + displayBounds.x;
        return Math.max(startX, 0);
    }

    private int calculateDialogStartY(Rectangle displayBounds, Point dialogSize) {
        int startY = displayBounds.height - dialogSize.y;
        return Math.max(startY, 0) / 2;
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        Rectangle displayBounds = getShell().getMonitor().getBounds();
        return new Point(calculateDialogStartX(displayBounds, initialSize),
                calculateDialogStartY(displayBounds, initialSize));
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(createNoMarginGridLayout());
        container.setBackground(ColorUtil.getCompositeBackgroundColorForDialog());

        SashForm sashForm = createMainSashForm(container);
        sashForm.setBackground(ColorUtil.getCompositeBackgroundColorForSashform());
        populateSashForm(sashForm);
        sashForm.setWeights(new int[] { 4, 4, 4 });

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
        // do nothing here
    }

    @Override
    protected boolean hasDocumentation() {
        return true;
    }

    @Override
    protected String getDocumentationUrl() {
        return MobileRecoderMessagesConstants.URL_DOCUMENTATION_MOBILE_RECORD;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        recordActionResult = new RecordActionResult(recordedActionsComposite.getStepView().getWrapper(),
                capturedObjectsComposite.getCapturedElements());
        
        okPressed = true;
        
        super.okPressed();
    }

    private List<AstTreeTableNode> getRecordedActions() {
        return recordedActionsComposite.getRecordedActions();
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
        createLeftPaneComposite(sashForm);
        createMiddlePaneComposite(sashForm);
        
        deviceView = new MobileDeviceView(this);
        deviceView.createControls(sashForm);
    }

    private void createLeftPaneComposite(SashForm sashForm) {
        Composite contentComposite = new Composite(sashForm, SWT.NONE);
        contentComposite.setLayout(createNoMarginGridLayout());

        addStartStopToolbar(contentComposite);
        createSettingComposite(contentComposite);
        createActionsAndCapturedObjectsCompostite(contentComposite);
    }

    private void addStartStopToolbar(Composite contentComposite) {
        Composite toolbarComposite = new Composite(contentComposite, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ToolBar contentToolbar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        contentToolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        contentToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        btnCapture = new ToolItem(contentToolbar, SWT.NONE);
        btnCapture.setImage(MobileRecorderImageConstants.IMG_24_CAPTURE);
        btnCapture.setDisabledImage(MobileRecorderImageConstants.IMG_24_CAPTURE_DISABLED);
        btnCapture.setText(MobileRecoderMessagesConstants.BTN_CAPTURE_OBJECT);
        btnCapture.setToolTipText(MobileRecoderMessagesConstants.BTN_TOOLTIP_CAPTURE_OBJECT);
        btnCapture.setEnabled(false);
        btnCapture.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                captureObjectAction();
            }
        });

        btnStart = new ToolItem(contentToolbar, SWT.NONE);
        btnStart.setImage(MobileRecorderImageConstants.IMG_24_START_DEVICE);
        btnStart.setDisabledImage(MobileRecorderImageConstants.IMG_24_START_DEVICE_DISABLED);
        btnStart.setText(MobileRecoderMessagesConstants.BTN_START);
        btnStart.setToolTipText(MobileRecoderMessagesConstants.BTN_TOOLTIP_START);
        btnStart.setEnabled(false);
        btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Validate all required informations are filled
                if (validateAppSetting()) {
                    startObjectInspectorAction();
                    updateActionButtonsVisibility(propertiesComposite.getEditingElement(),
                            getCurrentMobileDriverType());
                }
            }
        });

        btnStop = new ToolItem(contentToolbar, SWT.NONE);
        btnStop.setImage(MobileRecorderImageConstants.IMG_24_STOP_DEVICE);
        btnStop.setDisabledImage(MobileRecorderImageConstants.IMG_24_STOP_DEVICE_DISABLED);
        btnStop.setText(MobileRecorderStringConstants.STOP);
        btnStop.setToolTipText(MobileRecorderStringConstants.STOP);
        btnStop.setEnabled(false);
        btnStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stopObjectInspectorAction();
            }
        });
    }

    private void createSettingComposite(Composite parent) {
        mobileConfigurationsComposite = new MobileConfigurationsComposite(this, parent, mobileComposite);
    }

    private void createActionsAndCapturedObjectsCompostite(Composite parent) {
        CTabFolder leftBottomTabFolder = new CTabFolder(parent, SWT.NONE);
        leftBottomTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        createRecordedActionComposite(leftBottomTabFolder);
        CTabItem recordedActionTabItem = new CTabItem(leftBottomTabFolder, SWT.NONE);
        recordedActionTabItem.setText("Recorded Actions");
        recordedActionTabItem.setControl(recordedActionsComposite);

        SashForm capturedObjectsSashForm = new SashForm(leftBottomTabFolder, SWT.VERTICAL);
        createCapturedObjectsAndPropertiesComposite(capturedObjectsSashForm);

        capturedObjectsSashForm.setLayout(new FillLayout());
        capturedObjectsSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        capturedObjectsSashForm.setWeights(new int[] { 6, 6, 1 });

        CTabItem capturedObjectsTabItem = new CTabItem(leftBottomTabFolder, SWT.NONE);
        capturedObjectsTabItem.setText("Captured Objects");
        capturedObjectsTabItem.setControl(capturedObjectsSashForm);

        leftBottomTabFolder.setSelection(recordedActionTabItem);

        recordedActionsComposite.getStepView()
            .setCapturedElementsTableViewer(capturedObjectsComposite.getCapturedObjectsTableViewer());
    }

    private void createRecordedActionComposite(Composite parent) {
        recordedActionsComposite = new MobileRecordedActionsComposite(this, parent);
    }

    private void createCapturedObjectsAndPropertiesComposite(Composite parent) {
        createCapturedObjectsComposite(parent);
        createPropertiesComposite(parent);
        createHighlightElementComposite(parent);
    }

    private void createCapturedObjectsComposite(Composite parent) {
        capturedObjectsComposite = new MobileCapturedObjectsComposite(this, parent);
    }

    private void createPropertiesComposite(Composite parent) {
        propertiesComposite = new MobileElementPropertiesComposite(this, parent);
    }

    private void createHighlightElementComposite(Composite parent) {
        highlightElementComposite = new MobileHighlightComposite(this);
        highlightElementComposite.createComposite(parent);
    }

    private void createMiddlePaneComposite(SashForm sashForm) {
        Composite middlePane = new Composite(sashForm, SWT.NONE);
        middlePane.setLayout(createNoMarginGridLayout());

        SashForm hSashForm = new SashForm(middlePane, SWT.VERTICAL);
        hSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createActionListComposite(hSashForm);
        createAllObjectsComposite(hSashForm);

        hSashForm.setWeights(new int[] { 3, 7 });
    }

    private void createActionListComposite(SashForm sashForm) {
        Composite actionListComposite = new Composite(sashForm, SWT.NONE);
        actionListComposite.setLayout(new GridLayout());

        Label lblRecordedActions = new Label(actionListComposite, SWT.NONE);
        lblRecordedActions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText(MobileRecoderMessagesConstants.LBL_POSSIBLE_ACTIONS);

        Composite buttonsComposite = new Composite(actionListComposite, SWT.NONE);
        RowLayout layout = new RowLayout();
        buttonsComposite.setLayout(layout);
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        actionButtons.addAll(Arrays.asList(MobileAction.values())
                .stream()
                .filter(action -> action.isUserInputAction())
                .map(action -> {
                    return new MobileActionButtonWrapper(buttonsComposite, action, (event) -> {
                        try {
                            MobileActionMapping actionMapping = performAction(action,
                                	allObjectsComposite.getSelectedElement());
                            if (actionMapping == null) {
                                return;
                            }

                            recordedActionsComposite.getStepView().addNode(actionMapping);
                            
                            List<CapturedMobileElement> mobileElements = new ArrayList<>();
                            CapturedMobileElement targetElement = actionMapping.getTargetElement();
                            if (targetElement != null) {
                                mobileElements.add(targetElement);
                                capturedObjectsComposite.getCapturedObjectsTableViewer().addMobileElements(mobileElements);
                                capturedObjectsComposite.setSelection(targetElement);
                                propertiesComposite.setEditingElement(targetElement);
                            }
                        } catch (ClassNotFoundException | StepFailedException | MobileRecordException
                                | InvocationTargetException | InterruptedException e) {
                            LoggerSingleton.logError(e);
                            MultiStatusErrorDialog.showErrorDialog(
                                    "Unable to perform action: " + action.getReadableName(), e.getMessage(),
                                    ExceptionsUtil.getStackTraceForThrowable(e));
                        }
                    });
                })
                .collect(Collectors.toList()));
    }

    private void createAllObjectsComposite(Composite parent) {
        allObjectsComposite = new MobileAllObjectsComposite(this, parent);
    }

	private MobileTestObject convertMobileElementToTestObject(CapturedMobileElement targetElement,
			MobileDriverType driverType) {
		if (targetElement == null) {
			return null;
		}
		MobileTestObject testObject = new MobileTestObject(targetElement.getName());
		testObject.setMobileLocator(targetElement.getLocator());
		testObject.setMobileLocatorStrategy(MobileLocatorStrategy.valueOf(targetElement.getLocatorStrategy().name()));
		return testObject;
	}

    private MobileActionMapping performAction(MobileAction action, TreeMobileElement treeElement)
            throws MobileRecordException {
        try {
            MobileActionHelper mobileActionHelper = new MobileActionHelper(inspectorController.getDriver());

            final ProgressMonitorDialogWithThread progressDlg = new ProgressMonitorDialogWithThread(getShell()) {
                @Override
                public void cancelPressed() {
                    super.cancelPressed();
                    finishedRun();
                    getProgressMonitor().done();
                }
            };
            
            final MobileActionMapping mobileActionMapping = MobileActionMapping.newEmpty();

            IRunnableWithProgress processToRun = new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(MobileRecoderMessagesConstants.MSG_TASK_EXECUTING_COMMAND,
                            IProgressMonitor.UNKNOWN);
                    progressDlg.runAndWait(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            CapturedMobileElement targetElement = null;
                            if (action.hasElement()) {
                                targetElement = captureMobileElement(treeElement);
                            }
                            TestObject testObject = convertMobileElementToTestObject(targetElement, getCurrentMobileDriverType());
                            mobileActionMapping.setAction(action);
                            mobileActionMapping.setTargetElement(targetElement);
                            switch (action) {
                                case ClearText:
                                    mobileActionHelper.clearText(testObject);
                                    break;
                                case HideKeyboard:
                                    mobileActionHelper.hideKeyboard();
                                    break;
                                case PressBack:
                                    mobileActionHelper.pressBack();
                                    break;
                                case GetText:
                                    handleGetText(testObject, mobileActionMapping, mobileActionHelper);
                                    break;
                                case SetText:
                                    final StringBuilder stringBuilder = new StringBuilder();
                                    UISynchronizeService.syncExec(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputDialog inputDialog = new InputDialog(getShell(),
                                                    MobileRecoderMessagesConstants.DLG_TITLE_TEXT_INPUT,
                                                    MobileRecoderMessagesConstants.DLG_MSG_TEXT_INPUT, null, null);
                                            if (inputDialog.open() == Window.OK) {
                                                stringBuilder.append(inputDialog.getValue());
                                            }
                                        }
                                    });
                                    String textInput = stringBuilder.toString();
                                    if (textInput.isEmpty()) {
                                        throw new CancellationException();
                                    }
                                    mobileActionMapping.getData()[0].setValue(new ConstantExpressionWrapper(textInput));
                                    mobileActionHelper.setText(testObject, textInput);
                                    break;
                                case SetEncryptedText:
                                    handleSetEncryptedText(mobileActionHelper, mobileActionMapping, testObject);
                                    break;
                                case ScrollToText:
                                    handleScrollToText(mobileActionHelper, mobileActionMapping);
                                    break;
                                case SwitchToLandscape:
                                    mobileActionHelper.switchToLandscape();
                                    break;
                                case SwitchToPortrait:
                                    mobileActionHelper.switchToPortrait();
                                    break;
                                case Tap:
                                    mobileActionHelper.tap(testObject);
                                    break;
                                case TapAndHold:
                                    mobileActionHelper.tapAndHold(testObject);
                                    break;
                                case Swipe:
                                    handleSwipeAction(mobileActionHelper, mobileActionMapping);
                                    break;
                                default:
                                    break;
                            }
                            // wait 0.5s for page source to change
                            Thread.sleep(500);
                            return null;
                        }
                    });
                    checkMonitorCanceled(monitor);

                    monitor.done();
                }
            };
            progressDlg.run(true, false, processToRun);
            captureObjectAction();
            setSelectedElement(null);
            allObjectsComposite.clearAllSelections();
            return mobileActionMapping;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ExecutionException) {
                ExecutionException executionException = (ExecutionException) e.getTargetException();
                Throwable cause = executionException.getCause();
                if (cause instanceof StepFailedException) {
                    throw (StepFailedException) cause;
                }
                if (cause instanceof CancellationException) {
                    return null;
                }
                throw new MobileRecordException(cause);
            }
            throw new MobileRecordException(e.getTargetException());
        } catch (Exception e) {
            throw new MobileRecordException(e);
        }
    }

    private void handleSetEncryptedText(MobileActionHelper mobileActionHelper, MobileActionMapping mobileActionMapping, TestObject testObject) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                InputDialog inputDialog = new InputDialog(getShell(),
                        MobileRecoderMessagesConstants.DLG_TITLE_TEXT_INPUT,
                        MobileRecoderMessagesConstants.DLG_MSG_TEXT_INPUT, null, null) {
                    @Override
                    protected int getInputTextStyle() {
                        return super.getInputTextStyle() | SWT.PASSWORD;
                    }
                };
                if (inputDialog.open() == Window.OK) {
                    stringBuilder.append(inputDialog.getValue());
                }
            }
        });
        String password = stringBuilder.toString();
        String encryptedPassword = CryptoUtil.encode(CryptoUtil.getDefault(password));
        if (password.isEmpty()) {
            throw new CancellationException();
        }
        mobileActionMapping.getData()[0].setValue(new ConstantExpressionWrapper(encryptedPassword));
        mobileActionHelper.setText(testObject, password);
    }

    private void handleScrollToText(MobileActionHelper mobileActionHelper, MobileActionMapping mobileActionMapping ) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                InputDialog inputDialog = new InputDialog(getShell(),
                        MobileRecoderMessagesConstants.DLG_TITLE_TEXT_INPUT,
                        MobileRecoderMessagesConstants.DLG_MSG_TEXT_INPUT, null, null);
                if (inputDialog.open() == Window.OK) {
                    stringBuilder.append(inputDialog.getValue());
                }
            }
        });
        String textInput = stringBuilder.toString();
        if (textInput.isEmpty()) {
            throw new CancellationException();
        }
        mobileActionMapping.getData()[0].setValue(new ConstantExpressionWrapper(textInput));
        mobileActionHelper.scrollToText(textInput);
    }

    private void handleSwipeAction(MobileActionHelper mobileActionHelper, MobileActionMapping mobileActionMapping)
            throws Exception {
        List<InputParameter> parameters = new ArrayList<>();
        InputParameterClass integerParamType = new InputParameterClass(Integer.class);
        ConstantExpressionWrapper defaultValue = new ConstantExpressionWrapper(0);
        parameters.add(new InputParameter("startX", integerParamType, defaultValue));
        parameters.add(new InputParameter("startY", integerParamType, defaultValue));
        parameters.add(new InputParameter("endX", integerParamType, defaultValue));
        parameters.add(new InputParameter("endY", integerParamType, defaultValue));
        InputParameterBuilder parameterBuilder = InputParameterBuilder.createForNestedMethodCall(parameters);

        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                ArgumentInputBuilderDialog inputDialog = new ArgumentInputBuilderDialog(getShell(), parameterBuilder,
                        null);
                inputDialog.open();
            }
        });

        MobileActionParamValueType[] actionParams = mobileActionMapping.getData();
        List<InputParameter> touchCoords = parameterBuilder.getOriginalParameters();
        List<Integer> coords = new ArrayList<Integer>();
        IntStream.range(0, actionParams.length).forEach(index -> {
            coords.add((Integer) (((ConstantExpressionWrapper) touchCoords.get(index).getValue()).getValue()));
            actionParams[index].setValue(touchCoords.get(index).getValue());
        });

        if (coords.stream().allMatch(coord -> coord == 0)) {
            throw new CancellationException();
        }

        mobileActionHelper.swipe(coords.get(0), coords.get(1), coords.get(2), coords.get(3));
    }

    private void handleGetText(
            TestObject testObject,
            MobileActionMapping mobileActionMapping,
            MobileActionHelper mobileActionHelper
    ) throws Exception {
        String elementText = mobileActionHelper.getText(testObject);
        final MutableBoolean isCanceled = new MutableBoolean(false);

        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                GetTextDialog getTextDialog = new GetTextDialog(getShell(), elementText);
                if (getTextDialog.open() != GetTextDialog.OK) {
                    isCanceled.setTrue();
                }
            }
        });
        
        if (isCanceled.isTrue()) {
            throw new CancellationException();
        }
    }

    private class GetTextDialog extends AbstractDialog {

        private Text txtText;

        private String text;

        protected GetTextDialog(Shell parentShell, String text) {
            super(parentShell, false);
            this.text = text;
        }

        @Override
        protected void registerControlModifyListeners() {
        }

        @Override
        protected void setInput() {
            txtText.setText(StringUtils.defaultIfEmpty(text, ""));
        }

        @Override
        protected Control createDialogContainer(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout());

            Label lblText = new Label(composite, SWT.NONE);
            lblText.setText(MobileRecoderMessagesConstants.DLG_GET_TEXT_INPUT_LABEL);

            txtText = new Text(composite, SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
            txtText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            return composite;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, MobileRecoderMessagesConstants.BTN_APPLY_ACTION, false);
            createButton(parent, IDialogConstants.CANCEL_ID, MobileRecoderMessagesConstants.BTN_CANCEL_ACTION, false);
        }

        @Override
        protected Point getInitialSize() {
            return new Point(400, 250);
        }

        @Override
        public String getDialogTitle() {
            return MobileRecoderMessagesConstants.DLG_GET_TEXT_TITLE;
        }
    }

    public MobileDriverType getCurrentMobileDriverType() {
        return mobileComposite.getSelectedDriverType();
    }

    public void updateActionButtonsVisibility(MobileElement mobileElement, MobileDriverType currentMobileDriverType) {
        actionButtons.stream().forEach(actionButton -> {
            MobileAction action = actionButton.getMobileAction();
            actionButton.setEnabledButton(inspectorController.getDriver() != null
                    && (!action.hasElement() || mobileElement != null)
                    && (currentMobileDriverType != null && action.isDriverTypeSupported(currentMobileDriverType)));
        });
    }

    @Override
    public void refreshButtonsState() {
        validateToEnableStartButton();
        updateActionButtonsVisibility(propertiesComposite.getEditingElement(), getCurrentMobileDriverType());
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

    @Override
    public void updateDeviceNames() {
        UISynchronizeService.asyncExec(() -> {
            try {
                mobileComposite.loadDevices();
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
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnCapture.setEnabled(false);
            }
        };
        inspectorController.setStreamHandler(progressDlg);
        try {
            if (!mobileComposite.startApp(inspectorController, progressDlg)) {
                btnStart.setEnabled(true);
                return;
            }
            captureObjectAction();

            // If no exception, application has been successful started, enable more features
            btnCapture.setEnabled(true);
            btnStop.setEnabled(true);
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            setSelectedElement(null);
            recordedActionsComposite.getStepView().addNode(mobileComposite.buildStartAppActionMapping());

            // send event for tracking
            Trackings.trackMobileRecord(getDeviceTypeString());
        } catch (InvocationTargetException | InterruptedException | ClassNotFoundException ex) {
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
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnCapture.setEnabled(false);
        } finally {
            inspectorController.setStreamHandler(null);
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
                monitor.beginTask(MobileRecoderMessagesConstants.MSG_TASK_CAPTURING_OBJECTS, IProgressMonitor.UNKNOWN);

                TreeMobileElement newAppRootElement = (TreeMobileElement) dialog.runAndWait(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        TreeMobileElement appRootElement = inspectorController.getMobileObjectRoot();
                        return appRootElement;
                    }
                });

                checkMonitorCanceled(monitor);

                refreshTreeElements(dialog, newAppRootElement);

                String imgPath = captureImage();

                checkMonitorCanceled(monitor);

                refreshDeviceView(imgPath, newAppRootElement);

                appRootElement = newAppRootElement;

                monitor.done();
            }

            private void refreshTreeElements(final ProgressMonitorDialogWithThread dialog,
                    final TreeMobileElement newAppRootElement) {
                // Root element should be named as .apk file name
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        TreeViewer allElementTreeViewer = allObjectsComposite.getAllElementTreeViewer();
                        if (newAppRootElement != null) {
                            allElementTreeViewer.setInput(new Object[] { newAppRootElement });
                        } else {
                            allElementTreeViewer.setInput(new Object[] { });
                        }
                        allElementTreeViewer.refresh();
                        allElementTreeViewer.expandAll();
                    }
                });
            }

            private void refreshDeviceView(String imgPath, final TreeMobileElement newAppRootElement) {
                File imgFile = new File(imgPath);
                if (imgFile.exists()) {
                    deviceView.refreshDialog(imgFile, newAppRootElement);
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
            LoggerSingleton.logError(e);
            Throwable exception = e.getTargetException();
            MultiStatusErrorDialog.showErrorDialog(exception,
                    MobileRecoderMessagesConstants.MSG_ERR_CANNOT_CAPTURE_OBJECTS,
                    exception.getClass().getSimpleName());
        } finally {
            btnCapture.setEnabled(true);
        }
    }

    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException(MobileRecoderMessagesConstants.MSG_ERR_OPERATION_CANCELLED);
        }
    }

    private void stopObjectInspectorAction() {
        // Close application
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Quit Driver
                if (inspectorController.getDriver() != null) {
                    inspectorController.closeApp();
                }
            }
        });
        thread.start();

        final Shell shell = getShell();
        if (shell != null && !shell.isDisposed()) {
            // Update UI
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnCapture.setEnabled(false);
            updateActionButtonsVisibility(null, getCurrentMobileDriverType());

            allObjectsComposite.clearAllElements();
            try {
                recordedActionsComposite.getStepView().refreshTree();
            } catch (InvocationTargetException | InterruptedException exeception) {
                LoggerSingleton.logError(exeception);
            }
        }

        if (deviceView != null) {
            deviceView.dispose();
        }

        try {
            addAdditionalActions();
        } catch (ClassNotFoundException | InvocationTargetException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                return;
            }
            LoggerSingleton.logError(exception);
            Throwable targetException = ((InvocationTargetException) exception).getTargetException();
            String message = (targetException instanceof java.util.concurrent.ExecutionException)
                    ? targetException.getCause().getMessage() : targetException.getMessage();
            UISynchronizeService.syncExec(() -> {
                MultiStatusErrorDialog.showErrorDialog("Unable to close application", message,
                        ExceptionsUtil.getStackTraceForThrowable(targetException));
            });
        }
    }

    private void addAdditionalActions() throws ClassNotFoundException, InvocationTargetException, InterruptedException {
        int numRecordedActions = recordedActionsComposite.getRecordedActions().size();
        if (numRecordedActions <= 0) {
            return;
        }
        AstTreeTableNode lastRecordAction = recordedActionsComposite.getRecordedActions().get(numRecordedActions - 1);
        ExpressionStatementWrapper lastExpressionWrapper = (ExpressionStatementWrapper) lastRecordAction.getASTObject();
        MethodCallExpressionWrapper lastMethodCallExpression = (MethodCallExpressionWrapper) lastExpressionWrapper.getExpression();
        if (((ConstantExpressionWrapper) lastMethodCallExpression.getMethod()).getValue()
                .equals(MobileAction.CloseApplication.getMappedKeywordMethod())) {
            return;
        }
        recordedActionsComposite.getStepView()
            .addNode(new MobileActionMapping(MobileAction.CloseApplication, null));
    }

    private void validateToEnableStartButton() {
        btnStart.setEnabled(mobileComposite.isAbleToStart());
    }

    private boolean validateAppSetting() {
        return mobileComposite.validateSetting();
    }

    // Highlight Selected object on captured screenshot
    @Override
    public void highlightElement(MobileElement selectedElement) {
        if (selectedElement == null || deviceView == null || deviceView.isDisposed()) {
            return;
        }
        deviceView.highlightElement(selectedElement);
    }

    @Override
    public void highlightElementRects(List<Rectangle> rects) {
        deviceView.highlightRects(rects);
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
        final TreeMobileElement foundElement = findElementByLocation(appRootElement, x, y);
        if (foundElement == null) {
            return;
        }
        highlightElement(foundElement);
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                getShell().setFocus();
                allObjectsComposite.focusToElementsTree();
                allObjectsComposite.setSelection(foundElement);

                setSelectedElement(foundElement);
            }
        });
    }

    private TreeMobileElement findElementByLocation(TreeMobileElement currentElement, int x, int y) {
        List<TreeMobileElement> potentialElements = recursivelyFindListElementByLocation(currentElement, x, y);
        int nearestDistance = Integer.MAX_VALUE;
        TreeMobileElement foundElement = null;
        for (TreeMobileElement element : potentialElements) {
            int elementNearestDistance = findNearestDistance(element, x, y);
            if (elementNearestDistance <= nearestDistance) {
                nearestDistance = elementNearestDistance;
                foundElement = element;
            }
        }
        return foundElement;
    }

    private int findNearestDistance(TreeMobileElement element, int x, int y) {
        Rectangle elementBounds = getElementBounds(element);
        int leftDistance = Math.abs(elementBounds.x - x);
        int rightDistance = Math.abs(elementBounds.x + elementBounds.width - x);
        int topDistance = Math.abs(elementBounds.y - y);
        int bottomDistance = Math.abs(elementBounds.y + elementBounds.height - y);
        return minNumber(Arrays.asList(leftDistance, rightDistance, topDistance, bottomDistance));
    }

    private int minNumber(List<Integer> list) {
        int min = Integer.MAX_VALUE;
        for (int number : list) {
            if (number < min) {
                min = number;
            }
        }
        return min;
    }

    /**
     * Recursively find element that is positioned at [x, y]. This is by assumed that mobile elements don't overlap each
     * other.
     * 
     * @param currentElement
     * @param x
     * @param y
     * @return list of elements that were found
     */
    private List<TreeMobileElement> recursivelyFindListElementByLocation(TreeMobileElement currentElement, int x,
            int y) {
        List<TreeMobileElement> childrenElements = new ArrayList<>();
        for (TreeMobileElement childElement : currentElement.getChildrenElement()) {
            childrenElements.addAll(recursivelyFindListElementByLocation(childElement, x, y));
        }
        if (!childrenElements.isEmpty()) {
            return childrenElements;
        }
        if (isPointInElementBounds(currentElement, x, y)) {
            return Arrays.asList(currentElement);
        }
        return Collections.emptyList();
    }

    private boolean isPointInElementBounds(TreeMobileElement currentElement, int x, int y) {
        if (currentElement.getAttributes() == null) {
            return false;
        }
        Map<String, String> attributes = currentElement.getAttributes();
        if (attributes.containsKey(GUIObject.X) && attributes.containsKey(GUIObject.Y)
                && attributes.containsKey(GUIObject.WIDTH) && attributes.containsKey(GUIObject.HEIGHT)) {
            Rectangle rectangle = getElementBounds(currentElement);
            return rectangle.contains(x, y);
        } else {
            return false;
        }
    }

    private Rectangle getElementBounds(TreeMobileElement currentElement) {
        Map<String, String> attributes = currentElement.getAttributes();
        Double elementX = Double.parseDouble(attributes.get(GUIObject.X));
        Double elementY = Double.parseDouble(attributes.get(GUIObject.Y));
        Double elementWidth = Double.parseDouble(attributes.get(GUIObject.WIDTH));
        Double elementHeight = Double.parseDouble(attributes.get(GUIObject.HEIGHT));
        Rectangle rectangle = new Rectangle(safeRoundDouble(elementX), safeRoundDouble(elementY),
                safeRoundDouble(elementWidth), safeRoundDouble(elementHeight));
        return rectangle;
    }

    @Override
    public MobileObjectSpyPreferencesHelper getPreferencesHelper() {
        return preferencesHelper;
    }

    @Override
    public void setSelectedElement(MobileElement element) {
//        if (element != null) {
//            TreeMobileElement link = element.getLink();
//            if (link != null) {
//                allObjectsComposite.setSelection(link);
//            }
//        } else {
//            allObjectsComposite.setSelection(null);
//        }
//        capturedObjectsComposite.setSelection(element);
//        propertiesComposite.setEditingElement(element);
        highlightElement(element);
        updateActionButtonsVisibility(element, getCurrentMobileDriverType());
    }

    @Override
    public void updateSelectedElement(CapturedMobileElement element) {
        capturedObjectsComposite.refresh(element, true);
        TreeMobileElement treeElement = element.getLink();
        if (treeElement != null) {
            allObjectsComposite.getAllElementTreeViewer().refresh(treeElement);
            allObjectsComposite.getAllElementTreeViewer().setSelection(new StructuredSelection(treeElement));
        }
    }

    @Override
    public void handleCapturedObjectsTableSelectionChange() {
        capturedObjectsComposite.updateCheckAllCheckboxState();
    }

    @Override
    public void setEdittingElement(CapturedMobileElement element) {
        propertiesComposite.setEditingElement(element);
        highlightElementComposite.setEditingElement(element);
    }

    @Override
    public void addCapturedElement(CapturedMobileElement element) {
        capturedObjectsComposite.addElement(element);
        propertiesComposite.focusAndEditCapturedElementName();
    }

    @Override
    public boolean isAddedCapturedElement(CapturedMobileElement element) {
        return capturedObjectsComposite.containsElement(element);
    }

    @Override
    public void removeCapturedElement(CapturedMobileElement element) {
        capturedObjectsComposite.removeElement(element);
        if (element == propertiesComposite.getEditingElement()) {
            propertiesComposite.setEditingElement(null);
            highlightElementComposite.setEditingElement(null);
        }
    }

    @Override
    public void focusAndEditCapturedElementName() {
        propertiesComposite.focusAndEditCapturedElementName();
    }

    @Override
    public void removeSelectedCapturedElements(CapturedMobileElement[] elements) {
        capturedObjectsComposite.removeElements(Arrays.asList(elements));
        propertiesComposite.setEditingElement(null);
        highlightElementComposite.setEditingElement(null);
    }

    @Override
    public void verifyCapturedElementsStates(CapturedMobileElement[] elements) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MobileInspectorController getInspectorController() {
        return inspectorController;
    }

	@Override
	public CapturedMobileElement captureMobileElement(TreeMobileElement treeElement) {
	    if (treeElement == null) {
	        return null;
	    }
	    
		return treeElement.newCapturedElement(inspectorController.getDriver());
	}

    public class RecordActionResult {
        private final List<CapturedMobileElement> mobileElements;

        private final ScriptNodeWrapper script;

        public RecordActionResult(ScriptNodeWrapper script, List<CapturedMobileElement> mobileElements) {
            this.script = script;
            this.mobileElements = mobileElements;
        }

        public List<CapturedMobileElement> getMobileElements() {
            return mobileElements;
        }

        public ScriptNodeWrapper getScript() {
            return script;
        }
    }
    
    private String getDeviceTypeString() {
        return mobileComposite.getSelectedDriverType().toString();
    }

    @Override
    public int open() {
        Trackings.trackOpenMobileRecord(getDeviceTypeString());
        return super.open();
    }
}
