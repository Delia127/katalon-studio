package com.kms.katalon.composer.mobile.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public class DeviceSelectionComposite extends Composite {
    private Combo cbbDevices;
    private Map<String, String> devicesList;
    private List<SelectionListener> selectionListenerList;

    public DeviceSelectionComposite(Composite parent, int style, MobileDriverType platForm) {
        super(parent, style);
        devicesList = new HashMap<String, String>();
        selectionListenerList = new ArrayList<SelectionListener>();
        setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        setLayout(glContainer);

        // Load devices name
        if (platForm == MobileDriverType.ANDROID_DRIVER) {
            try {
                devicesList = MobileExecutionUtil.getAndroidDevices();
            } catch (IOException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
        } else if (platForm == MobileDriverType.IOS_DRIVER) {
            try {
                devicesList = MobileExecutionUtil.getIosDevices();
            } catch (IOException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
        }

        Label theLabel = new Label(this, SWT.NONE);
        theLabel.setText(StringConstants.DIA_DEVICE_NAME);

        cbbDevices = new Combo(this, SWT.DROP_DOWN);
        cbbDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbbDevices.setItems(devicesList.values().toArray(new String[] {}));
        cbbDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (SelectionListener selectionListener : selectionListenerList) {
                    selectionListener.widgetSelected(e);
                }
            }
        });
    }

    public void setSelectionIndex(int index) {
        if (devicesList.size() > 0 && index <= devicesList.size()) {
            cbbDevices.select(index);
        }

    }

    public void setDeviceName(String deviceUUID) {
        if (deviceUUID == null || deviceUUID.isEmpty()) {
            return;
        }
        String deviceName = null;
        for (Entry<String, String> device : devicesList.entrySet()) {
            if (device.getKey().equals(deviceUUID)) {
                deviceName = device.getValue();
                break;
            }
        }
        if (cbbDevices.getItemCount() == 0 || cbbDevices.indexOf(deviceName) == -1) {
            cbbDevices.setText(deviceUUID);
        } else {
            cbbDevices.select(cbbDevices.indexOf(deviceName));
        }
    }

    public String getDeviceUUID() {
        if (cbbDevices.getSelectionIndex() < 0) {
            return null;
        }
        String selectedDevice = cbbDevices.getText();
        for (Entry<String, String> device : devicesList.entrySet()) {
            if (device.getValue().equals(selectedDevice)) {
                return device.getKey();
            }
        }
        return null;
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        selectionListenerList.add(selectionListener);
    }
}
