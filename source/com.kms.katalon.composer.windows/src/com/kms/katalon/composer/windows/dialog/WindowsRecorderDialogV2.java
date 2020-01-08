package com.kms.katalon.composer.windows.dialog;

import java.io.IOException;
import java.net.BindException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.handlers.SettingHandler;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.webui.recorder.dialog.RecordedStepsView;
import com.kms.katalon.composer.windows.record.WindowsActionsCaptureServer;
import com.kms.katalon.composer.windows.websocket.WindowsAddonSocket;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.windows.WindowsDriverConnector;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WindowsRecorderDialogV2 extends AbstractDialog {
    
    private static final String PREF_LAST_STARTED_APP = "lastStartedApp";
    
    private static final String PREF_LAST_STARTED_WINDOW_TITLE = "lastStartedWindowTitle";

    private static final String[] FILTER_FILE_NAMES = new String[] { "Windows Executable Files (*.exe)",
            "All Files (*.*)" };

    private static final String[] FILTER_EXTENSIONS = new String[] { "*.exe", "*.*" };

    private Text txtAppFile;

    private Button btnBrowse;

    private Label lblDriverConnector;

    private ScopedPreferenceStore store;

    private Text txtApplicationTitle;

    private SashForm hSashForm;

    private RecordedStepsView recordStepsView;

    private WindowsActionsCaptureServer server;

    private static final int ANY_PORT_NUMBER = 0;
    
    private AddonSocket currentInstantSocket;

    private ToolItem btnStart;

    private ToolItem btnStop;

    private boolean isStarting = false; 
    
    public WindowsRecorderDialogV2(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }
    
    @Override
    protected int getShellStyle() {
        if (!Platform.OS_LINUX.equals(Platform.getOS())) {
            return SWT.SHELL_TRIM | SWT.ON_TOP | SWT.CENTER;
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
        // TODO Auto-generated method stub
        try {
            store = PreferenceStoreManager.getPreferenceStore(WindowsAppComposite.class);
            String appPath = store.getString(PREF_LAST_STARTED_APP);
            txtAppFile.setText(StringUtils.defaultString(appPath));
            
            String windowTitle = store.getString(PREF_LAST_STARTED_WINDOW_TITLE);
            txtApplicationTitle.setText(StringUtils.defaultString(windowTitle));
            updateRunConfigurationDetails();
            
            setButtonStates();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void updateRunConfigurationDetails() throws IOException {
        WindowsDriverConnector driverConnector = WindowsDriverConnector
                .getInstance(ProjectController.getInstance().getCurrentProject().getFolderLocation());
        String url = driverConnector.getWinAppDriverUrl();
        String desiredCapabilities = JsonUtil.toJson(driverConnector.getDesiredCapabilities(), false);
        String text = String.format("%s, %s", url, desiredCapabilities);
        lblDriverConnector.setText(text);
        
        String toolTipText = String.format("WinAppDriver URL: %s, Capabilities: %s", url, desiredCapabilities);
        lblDriverConnector.setToolTipText(toolTipText);
    }
    
    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setBackground(ColorUtil.getCompositeBackgroundColorForDialog());
        container.setBackgroundMode(SWT.INHERIT_FORCE);

        GridLayout glMain = new GridLayout();
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        container.setLayout(glMain);

        Composite bodyComposite = new Composite(container, SWT.NONE);
        bodyComposite.setLayout(new FillLayout(SWT.VERTICAL));
        bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        hSashForm = new SashForm(bodyComposite, SWT.NONE);
        hSashForm.setSashWidth(0);

        Composite leftPanelComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout glHtmlDomComposite = new GridLayout();
        glHtmlDomComposite.marginBottom = 5;
        glHtmlDomComposite.marginRight = 0;
        glHtmlDomComposite.marginWidth = 0;
        glHtmlDomComposite.marginHeight = 0;
        glHtmlDomComposite.horizontalSpacing = 0;
        leftPanelComposite.setLayout(glHtmlDomComposite);
        
        addStartStopToolbar(leftPanelComposite);
        
        createConfigurationComposite(leftPanelComposite);

        createStepsPanel(leftPanelComposite);

        Composite rightPanelComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout glObjectComposite = new GridLayout();
        glObjectComposite.marginLeft = 5;
        glObjectComposite.marginWidth = 0;
        glObjectComposite.marginBottom = 0;
        glObjectComposite.marginHeight = 0;
        glObjectComposite.verticalSpacing = 0;
        glObjectComposite.horizontalSpacing = 0;
        rightPanelComposite.setLayout(glObjectComposite);

        createObjectsPanel(rightPanelComposite);

        hSashForm.setWeights(new int[] { 10, 0 });

        // txtStartUrl.setFocus();

        initializeInput();

        return container;
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
                try {
                    startServer(ANY_PORT_NUMBER);

                    isStarting = true;
                    setButtonStates();
                } catch (Exception ex) {
                    
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
                    server.stop();

                    isStarting = false;
                    setButtonStates();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
    }

    private void createConfigurationComposite(Composite parent) {
        Composite settingComposite = new Composite(parent, SWT.NONE);
        settingComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glSettingComposite = new GridLayout(2, false);
        glSettingComposite.horizontalSpacing = 10;
        settingComposite.setLayout(glSettingComposite);

        Label lblConfigurationHeader = new Label(settingComposite, SWT.NONE);
        lblConfigurationHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        lblConfigurationHeader.setFont(getFontBold(lblConfigurationHeader));
        lblConfigurationHeader.setText("CONFIGURATIONS");

        Composite appsComposite = new Composite(settingComposite, SWT.NONE);
        appsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        appsComposite.setLayout(new FillLayout());

        Composite composite = new Composite(appsComposite, SWT.NONE);
        GridLayout glComposite = new GridLayout(2, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        glComposite.horizontalSpacing = 10;
        composite.setLayout(glComposite);

        Label lblConfiguration = new Label(composite, SWT.NONE);
        lblConfiguration.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblConfiguration.setText("Configuration");

        Composite configurationComposite = new Composite(composite, SWT.NONE);
        configurationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout glConfigurationComposite = new GridLayout(2, false);
        glConfigurationComposite.marginWidth = 0;
        glConfigurationComposite.marginHeight = 0;
        configurationComposite.setLayout(glConfigurationComposite);

        lblDriverConnector = new Label(configurationComposite, SWT.NONE);
        lblDriverConnector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button btnEditConfiguration = new Button(configurationComposite, SWT.PUSH);
        btnEditConfiguration.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        btnEditConfiguration.setText(GlobalStringConstants.EDIT);
        btnEditConfiguration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SettingHandler settingHander = SettingHandler.getInstance();
                settingHander.openSettingsPage(IdConstants.SETTING_CAPABILITIES_WINDOWS);
//                try {
//                    updateRunConfigurationDetails();
//                    parentDialog.refreshButtonsState();
//                } catch (IOException ex) {
//                    MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.ERROR,
//                            "Unable to reload Windows desired capabilities");
//                    LoggerSingleton.logError(ex);
//                }
            }
        });

        Label lblAppFile = new Label(composite, SWT.NONE);
        lblAppFile.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblAppFile.setText("Application File");

        Composite appFileChooserComposite = new Composite(composite, SWT.NONE);
        appFileChooserComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout glFileChooser = new GridLayout(2, false);
        glFileChooser.marginWidth = 0;
        glFileChooser.marginHeight = 0;
        appFileChooserComposite.setLayout(glFileChooser);

        txtAppFile = new Text(appFileChooserComposite, SWT.BORDER);
        txtAppFile.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        txtAppFile.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                txtAppFile.setToolTipText(txtAppFile.getText());
                //parentDialog.refreshButtonsState();
            }
        });

        btnBrowse = new Button(appFileChooserComposite, SWT.PUSH);
        final GridData btnBrowserGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        btnBrowse.setLayoutData(btnBrowserGridData);
        btnBrowse.setText("Browse...");
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(FILTER_FILE_NAMES);
                dialog.setFilterExtensions(FILTER_EXTENSIONS);
                String absolutePath = dialog.open();
                if (StringUtils.isEmpty(absolutePath)) {
                    return;
                }
                txtAppFile.setText(absolutePath);
            }
        });
        
        txtAppFile.addDisposeListener(new DisposeListener() {
            
            @Override
            public void widgetDisposed(DisposeEvent e) {
                try {
                    store.save();
                } catch (IOException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });

        Label lblWindowTitle = new Label(composite, SWT.NONE);
        lblWindowTitle.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblWindowTitle.setText("Application Title");
        lblWindowTitle.setToolTipText("Title of the main application main window");

        txtApplicationTitle = new Text(composite, SWT.BORDER);
        txtApplicationTitle.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    }

    private void createStepsPanel(Composite parent) {
        Composite labelComposite = new Composite(parent, SWT.NONE);
        GridLayout glLabelComposite = new GridLayout(2, false);
        glLabelComposite.marginWidth = 0;
        glLabelComposite.marginHeight = 0;
        labelComposite.setLayout(glLabelComposite);

        Label lblRecordedActions = new Label(labelComposite, SWT.NONE);
        lblRecordedActions.setFont(getFontBold(lblRecordedActions));
        lblRecordedActions.setText("RECORDED ACTIONS");
        labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // createActionToolbar(labelComposite);

        SashForm compositeSteps = new SashForm(parent, SWT.VERTICAL);
        compositeSteps.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite compositeStepsTab = new Composite(compositeSteps, SWT.NONE);
        compositeStepsTab.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glStepsTab = new GridLayout(1, false);
        glStepsTab.marginWidth = 0;
        glStepsTab.marginHeight = 0;
        glStepsTab.marginBottom = 0;
        compositeStepsTab.setLayout(glStepsTab);

        createStepButtons(compositeStepsTab);

        Composite tableComposite = new Composite(compositeStepsTab, SWT.None);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        tableComposite.setLayout(layout);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        recordStepsView = new RecordedStepsView();
        recordStepsView.createContent(tableComposite);
    }

    private void createStepButtons(Composite compositeSteps) {
        Composite compositeToolbars = new Composite(compositeSteps, SWT.NONE);
        compositeToolbars.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        compositeToolbars.setLayout(layout);

        ToolBar toolbar = new ToolBar(compositeToolbars, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // SelectionListener selectionListener = new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // Object item = e.getSource();
        // if (item instanceof ToolItem) {
        // performToolItemSelected((ToolItem) e.getSource(), e);
        // return;
        // }
        // if (item instanceof MenuItem) {
        // performMenuItemSelected((MenuItem) e.getSource());
        // }
        // }
        // };
        //
        // tltmAddStep = new ToolItem(toolbar, SWT.DROP_DOWN);
        // tltmAddStep.setText(StringConstants.ADD);
        // tltmAddStep.setImage(ImageConstants.IMG_16_ADD);
        // tltmAddStep.addSelectionListener(selectionListener);
        //
        // Menu addMenu = new Menu(tltmAddStep.getParent().getShell());
        // tltmAddStep.setData(addMenu);
        // TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu,
        // new int[] { TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID,
        // TreeTableMenuItemConstants.getBuildInKeywordID("Mobile"),
        // TreeTableMenuItemConstants.getBuildInKeywordID("WS") });
        //
        // tltmRecent = new ToolItem(toolbar, SWT.DROP_DOWN);
        // tltmRecent.setText(ComposerTestcaseMessageConstants.PA_BTN_TIP_RECENT);
        // tltmRecent.setImage(ImageConstants.IMG_16_RECENT);
        // tltmRecent.addSelectionListener(selectionListener);
        // setRecentKeywordItemState();
        //
        // tltmRemoveStep = new ToolItem(toolbar, SWT.NONE);
        // tltmRemoveStep.setText(StringConstants.REMOVE);
        // tltmRemoveStep.setImage(ImageConstants.IMG_16_DELETE);
        // tltmRemoveStep.addSelectionListener(selectionListener);
        //
        // tltmUp = new ToolItem(toolbar, SWT.NONE);
        // tltmUp.setText(StringConstants.DIA_ITEM_MOVE_UP);
        // tltmUp.setImage(ImageConstants.IMG_16_MOVE_UP);
        // tltmUp.addSelectionListener(selectionListener);
        //
        // tltmDown = new ToolItem(toolbar, SWT.NONE);
        // tltmDown.setText(StringConstants.DIA_ITEM_MOVE_DOWN);
        // tltmDown.setImage(ImageConstants.IMG_16_MOVE_DOWN);
        // tltmDown.addSelectionListener(selectionListener);
        //
        // new HelpCompositeForDialog(compositeToolbars, DocumentationMessageConstants.RECORDER_RUN_STEPS) {
        // @Override
        // protected GridData createGridData() {
        // GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, true);
        // gridData.widthHint = 32;
        // return gridData;
        // }
        //
        // @Override
        // protected GridLayout createLayout() {
        // GridLayout layout = new GridLayout();
        // layout.marginHeight = 0;
        // layout.marginBottom = 0;
        // layout.marginWidth = 0;
        // return layout;
        // }
        // };
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private void initializeInput() {
        // txtStartUrl.setText(store.getString(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_URL));
        // txtStartUrl.selectAll();
        //
        // getTreeTableInput().refresh();
    }

    private void createObjectsPanel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(1, false));

    }

    private void closeInstantSession() {
        if (currentInstantSocket != null && currentInstantSocket.isConnected()) {
            currentInstantSocket.close();
        }
    }

    public boolean isCurrentServerPortUsable(int port) {
        return port == ANY_PORT_NUMBER || port == server.getServerPort();
    }

    private void startServer(int port) throws Exception {
        closeInstantSession();
        if (server != null && server.isStarted() && isCurrentServerPortUsable(port)) {
            return;
        }
        stopServer();
        try {
            server = new WindowsActionsCaptureServer(port, this, WindowsAddonSocket.class);
            server.start();
        } catch (BindException e) {
            MessageDialog.openError(getParentShell(), GlobalStringConstants.ERROR,
                    "Port is in use");
            server = null;
        }
    }

    private void stopServer() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }
    
    private void setButtonStates() {
        btnStart.setEnabled(!isStarting);
        btnStop.setEnabled(isStarting);
    }
}
