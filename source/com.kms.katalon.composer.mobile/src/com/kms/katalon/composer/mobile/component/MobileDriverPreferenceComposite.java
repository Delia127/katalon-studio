package com.kms.katalon.composer.mobile.component;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.core.appium.constants.AppiumStringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public class MobileDriverPreferenceComposite extends DriverPreferenceComposite {
    protected DeviceSelectionComposite deviceSelectionComposite;

    public MobileDriverPreferenceComposite(Composite parent, int style, MobileDriverConnector driverConnector) {
        super(parent, style, driverConnector);
    }

    @Override
    protected void createContents(final IDriverConnector driverConnector) {
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite deviceSelectionCompositeContainer = new Composite(this, SWT.NONE);
        deviceSelectionCompositeContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        deviceSelectionCompositeContainer.setLayout(new GridLayout());
        deviceSelectionComposite = new DeviceSelectionComposite(deviceSelectionCompositeContainer, SWT.NONE,
                (MobileDriverType) driverConnector.getDriverType());
        
        driverPropertyMapComposite = new DriverPropertyMapComposite(this);
        
        if (driverConnector != null) {
            String deviceId = ((MobileDriverConnector) driverConnector).getDefaultDeviceId();
            deviceSelectionComposite.setDeviceId(deviceId);
        }
        
        deviceSelectionComposite.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (driverConnector != null && deviceSelectionComposite != null) {
                    MobileDeviceInfo deviceInfo = deviceSelectionComposite.getSelectedDevice();
                    ((MobileDriverConnector) driverConnector).setDevice(deviceInfo);
                    if (deviceInfo != null) {
                        ((MobileDriverConnector) driverConnector).updateDefaultDeviceId();
                    }
                }
            }
        });
        
        deviceSelectionComposite.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                Map<String, Object> driverPreferenceProperties = driverPropertyMapComposite.getDriverProperties();
                MobileDeviceInfo deviceInfo = deviceSelectionComposite.getSelectedDevice();
                driverPreferenceProperties.put(AppiumStringConstants.CONF_EXECUTED_DEVICE_ID, deviceInfo.getDeviceId());
                driverPropertyMapComposite.setInput(driverPreferenceProperties);
            }
        });
    }

    @Override
    public IDriverConnector getResult() {
        return driverConnector;
    }
}
