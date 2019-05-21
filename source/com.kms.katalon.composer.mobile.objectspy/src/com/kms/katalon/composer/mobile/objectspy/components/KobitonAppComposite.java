package com.kms.katalon.composer.mobile.objectspy.components;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileAction;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.constant.ComposerMobileObjectspyMessageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileAppDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileInspectorController;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.integration.kobiton.entity.KobitonApplication;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.exceptions.KobitonApiException;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;

public class KobitonAppComposite implements MobileAppComposite {
    private Combo cbbKobitonDevices, cbbKobitonApps;

    private MobileAppDialog parentDialog;

    private Button btnRefreshKobitonDevice;

    private List<KobitonDevice> kobitonDevices = new ArrayList<>();

    private List<KobitonApplication> kobitonApps = new ArrayList<>();
    
    private KobitonDevice selectDevice = null;

    private Link linkLabel;
    
    private Composite composite;

    public boolean isAbleToStart() {
        return isNotBlank(cbbKobitonApps.getText()) && cbbKobitonDevices.getSelectionIndex() >= 0;
    }

    public List<String> getAllKobitonDevicesName() {
        List<String> devicesNameList = new ArrayList<String>();
        for (KobitonDevice kobitonDevice : kobitonDevices) {
            devicesNameList.add(kobitonDevice.getDisplayString());
        }
        return devicesNameList;
    }

    public void updateKobitonDeviceList()
            throws ClientProtocolException, URISyntaxException, IOException, KobitonApiException {
        kobitonDevices.clear();
        kobitonDevices
                .addAll(KobitonApiProvider.getKobitonFavoriteDevices(KobitonPreferencesProvider.getKobitonToken()));
    }

    public List<String> getAllKobitonAppsName() {
        List<String> devicesNameList = new ArrayList<String>();
        for (KobitonApplication kobitonApp : kobitonApps) {
            devicesNameList.add(kobitonApp.getName() + ", appId=" + kobitonApp.buildAutomationKey());
        }
        return devicesNameList;
    }

    public void updateKobitonAppList()
            throws ClientProtocolException, URISyntaxException, IOException, KobitonApiException {
        kobitonApps.clear();
        kobitonApps.addAll(KobitonApiProvider.getKobitionApplications(KobitonPreferencesProvider.getKobitonToken()));
    }

    public boolean validateKobitonAppSetting() {
        if (cbbKobitonDevices.getSelectionIndex() < 0) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    ComposerMobileObjectspyMessageConstants.DIA_ERROR_MSG_NO_KOBITON_DEVICES);
            return false;
        }

        if (cbbKobitonApps.getSelectionIndex() < 0) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    ComposerMobileObjectspyMessageConstants.DIA_ERROR_MSG_NO_KOBITON_APPS);
            return false;
        }
        return true;
    }

    private Shell getShell() {
        return composite.getShell();
    }

    public KobitonDevice getSelectedKobitonDevice() {
        if (cbbKobitonDevices == null || cbbKobitonDevices.isDisposed()) {
            return selectDevice;
        }
        int selectedDeviceIndex = cbbKobitonDevices.getSelectionIndex();
        if (selectedDeviceIndex < 0 || selectedDeviceIndex >= kobitonDevices.size()) {
            selectDevice = null;
            return selectDevice;
        }
        selectDevice = kobitonDevices.get(selectedDeviceIndex);
        return selectDevice;
    }

    public KobitonApplication getSelectedKobitonApplication() {
        int selectedAppIndex = cbbKobitonApps.getSelectionIndex();
        if (selectedAppIndex < 0 || selectedAppIndex >= kobitonApps.size()) {
            return null;
        }
        return kobitonApps.get(selectedAppIndex);
    }

    public boolean startKobitonApp(MobileInspectorController inspectorController,
            ProgressMonitorDialogWithThread progressDlg) throws InvocationTargetException, InterruptedException {
        final KobitonDevice selectDevice = getSelectedKobitonDevice();
        if (selectDevice == null) {
            return false;
        }
        final KobitonApplication selectedApplication = getSelectedKobitonApplication();

        IRunnableWithProgress processToRun = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_LBL_STATUS_APP_STARTING, IProgressMonitor.UNKNOWN);

                progressDlg.runAndWait(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        // Start application using KobitonDriver
                        inspectorController.startMobileApp(selectDevice, selectedApplication);
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

    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException(StringConstants.DIA_ERROR_MSG_OPERATION_CANCELED);
        }
    }
    
    public String getAppName() {
        return cbbKobitonApps.getText();
    }

    public void updateKobitonDevices() throws InvocationTargetException, InterruptedException {
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_JOB_TASK_LOADING_DEVICES, IProgressMonitor.UNKNOWN);

                try {
                    updateKobitonDeviceList();
                } catch (URISyntaxException | IOException | KobitonApiException e) {
                    throw new InvocationTargetException(e);
                }
                final List<String> devices = getAllKobitonDevicesName();

                checkMonitorCanceled(monitor);

                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (!devices.isEmpty()) {
                            cbbKobitonDevices.setItems(devices.toArray(new String[] {}));
                            cbbKobitonDevices.select(Math.max(0, devices.indexOf(cbbKobitonDevices.getText())));
                        }
                        setLinkLabelVisible(devices.isEmpty());
                    }
                });

                monitor.done();
            }
        };
        new ProgressMonitorDialogWithThread(Display.getDefault().getActiveShell()).run(true, true, runnable);
    }
    
    private void setLinkLabelVisible(boolean visible) {
        linkLabel.setVisible(visible);
        ((GridData) linkLabel.getLayoutData()).exclude = !visible;
        linkLabel.pack();
        linkLabel.getParent().layout(true, true);
    }

    @Override
    public Composite createComposite(Composite parent, int type, MobileAppDialog parentDialog) {
        composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        
        Composite deviceNameCompposite = new Composite(composite, SWT.NONE);
        final GridLayout deviceNameComppositeLayout = new GridLayout(3, false);
        deviceNameComppositeLayout.marginHeight = 0;
        deviceNameComppositeLayout.marginWidth = 0;
        deviceNameCompposite.setLayout(deviceNameComppositeLayout);
        deviceNameCompposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDeviceName = new Label(deviceNameCompposite, SWT.NONE);
        lblDeviceName.setText(StringConstants.DIA_LBL_DEVICE_NAME);

        cbbKobitonDevices = new Combo(deviceNameCompposite, SWT.READ_ONLY);
        cbbKobitonDevices.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbKobitonDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                parentDialog.refreshButtonsState();
            }
        });

        btnRefreshKobitonDevice = new Button(deviceNameCompposite, SWT.PUSH);
        btnRefreshKobitonDevice.setText(StringConstants.REFRESH);
        btnRefreshKobitonDevice.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                parentDialog.updateDeviceNames();
            }
        });
        
        
        linkLabel = new Link(composite, SWT.NONE);
        linkLabel.setText(
                StringConstants.MSG_NO_DEVICES + " <a href=\"" + StringConstants.NO_DEVICES_TROUBLESHOOTING_GUIDE_LINK
                        + "\">" + StringConstants.MSG_WRAPPED_NO_DEVICES_TROUBLESHOOTING_GUIDE + "</a>");

        linkLabel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });

        Composite appFileChooserComposite = new Composite(composite, SWT.NONE);
        appFileChooserComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glAppFileChooserComposite = new GridLayout(2, false);
        glAppFileChooserComposite.marginHeight = 0;
        glAppFileChooserComposite.marginWidth = 0;
        appFileChooserComposite.setLayout(glAppFileChooserComposite);

        Label appFileLabel = new Label(appFileChooserComposite, SWT.NONE);
        appFileLabel.setText(StringConstants.DIA_LBL_APP_FILE);

        cbbKobitonApps = new Combo(appFileChooserComposite, SWT.READ_ONLY);
        cbbKobitonApps.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cbbKobitonApps.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                parentDialog.refreshButtonsState();
            }
        });
        return composite;
    }

    @Override
    public boolean validateSetting() {
        return validateKobitonAppSetting();
    }

    @Override
    public MobileDriverType getSelectedDriverType() {
        return MobileInspectorController.getMobileDriverType(selectDevice);
    }

    @Override
    public boolean startApp(MobileInspectorController controller, AppiumMonitorDialog progressDialog)
            throws InvocationTargetException, InterruptedException {
        return startKobitonApp(controller, progressDialog);
    }

    @Override
    public void setInput() throws InvocationTargetException, InterruptedException {
        loadDevices();
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(ComposerMobileObjectspyMessageConstants.DIA_JOB_TASK_LOADING_KOBITON_APPS,
                        IProgressMonitor.UNKNOWN);

                try {
                    updateKobitonAppList();
                } catch (URISyntaxException | IOException | KobitonApiException e) {
                    throw new InvocationTargetException(e);
                }
                final List<String> apps = getAllKobitonAppsName();

                checkMonitorCanceled(monitor);

                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (apps.isEmpty()) {
                            return;
                        }
                        cbbKobitonApps.setItems(apps.toArray(new String[] {}));
                        cbbKobitonApps.select(Math.max(0, apps.indexOf(cbbKobitonApps.getText())));
                    }
                });

                monitor.done();
            }
        };
        new ProgressMonitorDialogWithThread(Display.getDefault().getActiveShell()).run(true, true, runnable);
    }

    @Override
    public String getAppFile() {
        return kobitonApps.get(cbbKobitonApps.getSelectionIndex()).getName();
    }

    @Override
    public void loadDevices() throws InvocationTargetException, InterruptedException {
        updateKobitonDevices();
    }

    @Override
    public MobileActionMapping buildStartAppActionMapping() {
        MobileActionMapping startAppAction = new MobileActionMapping(MobileAction.StartApplication, null);
        String appValue = kobitonApps.get(cbbKobitonApps.getSelectionIndex()).buildAutomationKey();
        startAppAction.getData()[0].setValue(new ConstantExpressionWrapper(appValue));
        return startAppAction;
    }
}
