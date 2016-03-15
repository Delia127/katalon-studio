package com.kms.katalon.composer.mobile.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
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
        

        deviceSelectionComposite.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (driverConnector != null && deviceSelectionComposite != null) {
                    ((MobileDriverConnector) driverConnector).setDeviceId(deviceSelectionComposite
                            .getSelectedDeviceId());
                }
            }
        });
    }

    @Override
    public IDriverConnector getResult() {
        return driverConnector;
    }
}
