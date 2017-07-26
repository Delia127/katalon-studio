package com.kms.katalon.composer.mobile.objectspy.components;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.execution.util.MobileDeviceUIProvider;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileAppDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileInspectorController;
import com.kms.katalon.composer.mobile.objectspy.preferences.MobileObjectSpyPreferencesHelper;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public class LocalAppComposite extends Composite {
    private String ANDROID_FILTER_NAMES = "Android Application (*.apk)";

    private String ANDROID_FILTER_EXTS = "*.apk";

    private String IOS_FILTER_NAMES = "iOS Application (*.app, *.ipa)";

    private String IOS_FILTER_EXTS = "*.app;*.ipa";

    private MobileAppDialog parentDialog;

    private MobileObjectSpyPreferencesHelper preferencesHelper;

    private Combo cbbDevices;

    private Button btnBrowse, btnRefreshDevice;

    private Text txtAppFile;

    private List<MobileDeviceInfo> deviceInfos = new ArrayList<>();
    
    private MobileDeviceInfo selectedDevice = null;

    public LocalAppComposite(Composite parent, MobileAppDialog parentDialog,
            MobileObjectSpyPreferencesHelper preferencesHelper, int style) {
        super(parent, style);
        this.parentDialog = parentDialog;
        this.preferencesHelper = preferencesHelper;
        initComponent();
    }

    private void initComponent() {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);

        Composite deviceNameCompposite = new Composite(this, SWT.NONE);
        final GridLayout deviceNameComppositeLayout = new GridLayout(3, false);
        deviceNameComppositeLayout.marginHeight = 0;
        deviceNameComppositeLayout.marginWidth = 0;
        deviceNameCompposite.setLayout(deviceNameComppositeLayout);
        deviceNameCompposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        // Device Name
        Label lblDeviceName = new Label(deviceNameCompposite, SWT.NONE);
        lblDeviceName.setText(StringConstants.DIA_LBL_DEVICE_NAME);

        cbbDevices = new Combo(deviceNameCompposite, SWT.READ_ONLY);
        cbbDevices.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                parentDialog.refreshButtonsState();
            }
        });

        btnRefreshDevice = new Button(deviceNameCompposite, SWT.FLAT);
        btnRefreshDevice.setText(StringConstants.REFRESH);
        btnRefreshDevice.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                parentDialog.updateDeviceNames();
            }
        });

        Composite appFileChooserComposite = new Composite(this, SWT.NONE);
        appFileChooserComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glAppFileChooserComposite = new GridLayout(3, false);
        glAppFileChooserComposite.marginHeight = 0;
        glAppFileChooserComposite.marginWidth = 0;
        appFileChooserComposite.setLayout(glAppFileChooserComposite);

        // Application File location
        Label appFileLabel = new Label(appFileChooserComposite, SWT.NONE);
        appFileLabel.setText(StringConstants.DIA_LBL_APP_FILE);

        txtAppFile = new Text(appFileChooserComposite, SWT.READ_ONLY | SWT.BORDER);
        txtAppFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtAppFile.setText(preferencesHelper.getLastAppFile());
        txtAppFile.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                parentDialog.refreshButtonsState();
            }
        });

        btnBrowse = new Button(appFileChooserComposite, SWT.PUSH);
        final GridData btnBrowserGridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        btnBrowse.setLayoutData(btnBrowserGridData);
        btnBrowse.setText(StringConstants.DIA_BTN_BROWSE);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(getFilterNames());
                dialog.setFilterExtensions(getFilterExtensions());
                String absolutePath = dialog.open();
                if (StringUtils.isEmpty(absolutePath)) {
                    return;
                }
                preferencesHelper.setLastAppFile(absolutePath);
                txtAppFile.setText(absolutePath);
            }
        });
    }

    private String[] getFilterNames() {
        if (StringUtils.equals(Platform.getOS(), Platform.OS_MACOSX)) {
            return new String[] { ANDROID_FILTER_NAMES, IOS_FILTER_NAMES };
        }
        return new String[] { ANDROID_FILTER_NAMES };
    }

    private String[] getFilterExtensions() {
        if (StringUtils.equals(Platform.getOS(), Platform.OS_MACOSX)) {
            return new String[] { ANDROID_FILTER_EXTS, IOS_FILTER_EXTS };
        }
        return new String[] { ANDROID_FILTER_EXTS };
    }

    public boolean isAbleToStart() {
        return isNotBlank(getAppFile()) && cbbDevices.getSelectionIndex() >= 0;
    }

    public void updateLocalDevices() throws InvocationTargetException, InterruptedException {
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_JOB_TASK_LOADING_DEVICES, IProgressMonitor.UNKNOWN);

                updateDeviceList();
                final List<String> devices = getAllDevicesName();

                checkMonitorCanceled(monitor);

                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (!devices.isEmpty()) {
                            cbbDevices.setItems(devices.toArray(new String[] {}));
                            cbbDevices.select(Math.max(0, devices.indexOf(cbbDevices.getText())));
                        }
                    }
                });

                monitor.done();
            }
        };
        new ProgressMonitorDialogWithThread(Display.getDefault().getActiveShell()).run(true, true, runnable);
    }

    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException(StringConstants.DIA_ERROR_MSG_OPERATION_CANCELED);
        }
    }

    private List<String> getAllDevicesName() {
        List<String> devicesNameList = new ArrayList<String>();
        for (MobileDeviceInfo deviceInfo : deviceInfos) {
            devicesNameList.add(deviceInfo.getDisplayName());
        }
        return devicesNameList;
    }

    private void updateDeviceList() {
        deviceInfos.clear();
        deviceInfos.addAll(MobileDeviceUIProvider.getAllDevices());
    }
    
    public String getAppName() {
        return FilenameUtils.getName(getAppFile());
    }

    public String getAppFile() {
        return txtAppFile.getText();
    }

    public MobileDeviceInfo getSelectedMobileDeviceInfo() {
        if (cbbDevices == null || cbbDevices.isDisposed()) {
            return selectedDevice;
        }
        int selectedMobileDeviceIndex = cbbDevices.getSelectionIndex();
        if (selectedMobileDeviceIndex < 0 || selectedMobileDeviceIndex >= deviceInfos.size()) {
            selectedDevice = null;
            return selectedDevice;
        }
        selectedDevice = deviceInfos.get(selectedMobileDeviceIndex);
        return selectedDevice;
    }

    public boolean startLocalApp(MobileInspectorController inspectorController,
            ProgressMonitorDialogWithThread progressDlg) throws InvocationTargetException, InterruptedException {
        final MobileDeviceInfo selectDeviceInfo = getSelectedMobileDeviceInfo();
        if (selectDeviceInfo == null) {
            return false;
        }
        final String appFile = getAppFile();

        IRunnableWithProgress processToRun = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_LBL_STATUS_APP_STARTING, IProgressMonitor.UNKNOWN);

                progressDlg.runAndWait(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        // Start application using MobileDriver
                        inspectorController.startMobileApp(selectDeviceInfo, appFile, false);
                        return null;
                    }
                });
                checkMonitorCanceled(monitor);

                monitor.done();
            }
        };

        progressDlg.run(true, true, processToRun);
        return true;
    }
    
    public boolean validateLocalAppSetting() {
        if (cbbDevices.getSelectionIndex() < 0) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_PLS_CONNECT_AND_SELECT_DEVICE);
            return false;
        }

        String appFilePath = getAppFile().trim();

        if (appFilePath.equals("")) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_PLS_SELECT_APP_FILE);
            return false;
        }
        File appFile = new File(appFilePath);

        if (!appFile.exists()) {
            MessageDialog.openWarning(getShell(), StringConstants.ERROR_TITLE,
                    StringConstants.DIA_ERROR_MSG_APP_FILE_NOT_EXIST);
            return false;
        }
        return true;
    }
}
