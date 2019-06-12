package com.kms.katalon.composer.mobile.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.util.MobileDeviceUIProvider;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class DeviceSelectionComposite extends Composite {
    private Combo cbbDevices;

    private List<MobileDeviceInfo> devicesList = new ArrayList<>();

    private ArrayList<SelectionListener> selectionListenerList = new ArrayList<>();

    public DeviceSelectionComposite(Composite parent, int style, MobileDriverType platform) {
        super(parent, style);
        setBackground(ColorUtil.getCompositeBackgroundColorForDialog());
        setBackgroundMode(SWT.INHERIT_FORCE);

        setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        setLayout(glContainer);

        // Load devices name
        loadDeviceList(platform);

        Label theLabel = new Label(this, SWT.NONE);
        theLabel.setText(StringConstants.DIA_DEVICE_NAME);

        cbbDevices = new Combo(this, SWT.DROP_DOWN);
        cbbDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Show full names of device list to show them on combo-box
        cbbDevices.setItems(getDeviceFullNames());

        cbbDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (SelectionListener selectionListener : selectionListenerList) {
                    selectionListener.widgetSelected(e);
                }
            }
        });
    }

    private String[] getDeviceFullNames() {
        String[] fullNames = new String[devicesList.size()];
        for (int i = 0; i < devicesList.size(); i++) {
            fullNames[i] = devicesList.get(i).getDisplayName();
        }
        return fullNames;
    }

    public void setSelectionIndex(int index) {
        if (devicesList.size() > 0 && index <= devicesList.size()) {
            cbbDevices.select(index);
        }
    }

    public void setDeviceId(String deviceId) {
        if (StringUtils.isBlank(deviceId)) {
            return;
        }

        for (int i = 0; i < devicesList.size(); i++) {
            if (devicesList.get(i).getDeviceId().equals(deviceId)) {
                cbbDevices.select(i);
                return;
            }
        }

    }

    private void loadDeviceList(MobileDriverType platForm) {
        devicesList.clear();
        switch (platForm) {
            case ANDROID_DRIVER:
                try {
                    if (MobileDeviceUIProvider.checkAndroidSDKExist(getShell())) {
                        devicesList.addAll(MobileDeviceProvider.getAndroidDevices());
                    }
                } catch (IOException | InterruptedException | MobileSetupException e) {
                    logException(e);
                }
                break;
            case IOS_DRIVER:
                try {
                    devicesList.addAll(MobileDeviceProvider.getIosDevices());
                } catch (IOException | InterruptedException e) {
                    logException(e);
                }
                try {
                    devicesList.addAll(MobileDeviceProvider.getIosSimulators());
                } catch (IOException | InterruptedException e) {
                    logException(e);
                }
                break;
        }
    }

    public void logException(Exception e) {
        MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Error",
                e.getClass().getName() + ": " + e.getMessage());
        LoggerSingleton.logError(e);
    }

    private boolean isNoDeviceSelected() {
        return devicesList.isEmpty() || cbbDevices.getSelectionIndex() < 0;
    }

    public MobileDeviceInfo getSelectedDevice() {
        if (isNoDeviceSelected()) {
            return null;
        }

        return devicesList.get(cbbDevices.getSelectionIndex());
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        selectionListenerList.add(selectionListener);
    }
}
