package com.kms.katalon.composer.mobile.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.driver.MobileDevice;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class DeviceSelectionComposite extends Composite {
    private Combo cbbDevices;
    private List<MobileDevice> devicesList;
    private ArrayList<SelectionListener> selectionListenerList;

    public DeviceSelectionComposite(Composite parent, int style, MobileDriverType platform) {
        super(parent, style);
        devicesList = new ArrayList<MobileDevice>();
        selectionListenerList = new ArrayList<SelectionListener>();
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
            fullNames[i] = devicesList.get(i).getFullName();
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
            if (devicesList.get(i).getId().equals(deviceId)) {
                cbbDevices.select(i);
                return;
            }
        }

    }

    private void loadDeviceList(MobileDriverType platForm) {
        try {
            if (platForm == MobileDriverType.ANDROID_DRIVER) {
                devicesList.addAll(MobileExecutionUtil.getAndroidDevices().values());
            } else if (platForm == MobileDriverType.IOS_DRIVER) {
                devicesList.addAll(MobileExecutionUtil.getIosDevices().values());
            }
        } catch (IOException | InterruptedException e) {
            LoggerSingleton.logError(e);
        }
    }

    private boolean isNoDeviceSelected() {
        return devicesList.isEmpty() || cbbDevices.getSelectionIndex() < 0;
    }

    public MobileDevice getSelectedDevice() {
        if (isNoDeviceSelected()) {
            return null;
        }

        return devicesList.get(cbbDevices.getSelectionIndex());
    }

    public String getSelectedDeviceId() {
        if (isNoDeviceSelected()) {
            return "";
        }

        return devicesList.get(cbbDevices.getSelectionIndex()).getId();
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        selectionListenerList.add(selectionListener);
    }
}
