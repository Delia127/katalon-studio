package com.kms.katalon.composer.windows.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.project.handlers.SettingHandler;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.windows.action.WindowsAction;
import com.kms.katalon.composer.windows.action.WindowsActionMapping;
import com.kms.katalon.composer.windows.spy.WindowsInspectorController;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.execution.windows.WindowsDriverConnector;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WindowsAppComposite {

    private static final String PREF_LAST_STARTED_APP = "lastStartedApp";
    
    private static final String PREF_LAST_STARTED_WINDOW_TITLE = "lastStartedWindowTitle";

    private static final String[] FILTER_FILE_NAMES = new String[] { "Windows Executable Files (*.exe)",
            "All Files (*.*)" };

    private static final String[] FILTER_EXTENSIONS = new String[] { "*.exe", "*.*" };

    private Text txtAppFile;

    private WindowsObjectDialog parentDialog;

    private Button btnBrowse;

    private Label lblDriverConnector;

    private ScopedPreferenceStore store;

    private Text txtApplicationTitle;

    public Composite createComposite(Composite parent, int type, WindowsObjectDialog parentDialog) {
        this.parentDialog = parentDialog;
        Composite composite = new Composite(parent, SWT.NONE);
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
                try {
                    updateRunConfigurationDetails();
                    parentDialog.refreshButtonsState();
                } catch (IOException ex) {
                    MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.ERROR,
                            "Unable to reload Windows desired capabilities");
                    LoggerSingleton.logError(ex);
                }
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
                parentDialog.refreshButtonsState();
            }
        });

        btnBrowse = new Button(appFileChooserComposite, SWT.PUSH);
        final GridData btnBrowserGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        btnBrowse.setLayoutData(btnBrowserGridData);
        btnBrowse.setText(StringConstants.DIA_BTN_BROWSE);
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

        return composite;
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

    public boolean validateSetting() {
        return true;
    }

    public WindowsActionMapping startApp(WindowsInspectorController controller, AppiumMonitorDialog progressDlg)
            throws InvocationTargetException, InterruptedException {
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        final WindowsDriverConnector driverConnector = getDriverConnector(projectDir);
        String appFile = txtAppFile.getText();
        String appTitle = txtApplicationTitle.getText();
        IRunnableWithProgress processToRun = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_LBL_STATUS_APP_STARTING, IProgressMonitor.UNKNOWN);

                progressDlg.runAndWait(new Callable<Object>() {

                    public Object call() throws Exception {
                        controller.startApplication(driverConnector, appFile, appTitle);
                        store.setValue(PREF_LAST_STARTED_APP, appFile);
                        store.setValue(PREF_LAST_STARTED_WINDOW_TITLE, appTitle);
                        return null;
                    }
                });

                monitor.done();
            }
        };

        progressDlg.run(true, true, processToRun);
        WindowsActionMapping actionMapping = new WindowsActionMapping(WindowsAction.StartApplicationWithTitle, null);
        actionMapping.getData()[0].setValue(new ConstantExpressionWrapper(appFile));
        actionMapping.getData()[1].setValue(new ConstantExpressionWrapper(appTitle));
        return actionMapping;
    }

    private WindowsDriverConnector getDriverConnector(String projectDir) throws InvocationTargetException {
        try {
            return WindowsDriverConnector.getInstance(projectDir);
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }
    }

    public void setInput() throws InvocationTargetException, InterruptedException {
        try {
            store = PreferenceStoreManager.getPreferenceStore(WindowsAppComposite.class);
            String appPath = store.getString(PREF_LAST_STARTED_APP);
            txtAppFile.setText(StringUtils.defaultString(appPath));
            
            String windowTitle = store.getString(PREF_LAST_STARTED_WINDOW_TITLE);
            txtApplicationTitle.setText(StringUtils.defaultString(windowTitle));
            updateRunConfigurationDetails();
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }
    }

    public String getAppFile() {
        return txtAppFile.getText();
    }

    public String getAppName() {
        return FilenameUtils.getBaseName(txtAppFile.getText());
    }

    public boolean isAbleToStart() {
        return StringUtils.isNotEmpty(txtAppFile.getText());
    }

    public WindowsObjectDialog getParentDialog() {
        return parentDialog;
    }
}
