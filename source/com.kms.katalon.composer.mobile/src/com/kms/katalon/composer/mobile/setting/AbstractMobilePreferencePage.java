package com.kms.katalon.composer.mobile.setting;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.composer.mobile.component.DeviceSelectionComposite;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public abstract class AbstractMobilePreferencePage extends DriverPreferencePage {

    private DeviceSelectionComposite deviceSelectionComposite;
    private MobileDriverConnector abstractMobileDriverConnector;

    @Override
    protected void initilize() {
        super.initilize();
        abstractMobileDriverConnector = (MobileDriverConnector) driverConnector;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite deviceSelectionCompositeContainer = new Composite(container, SWT.NONE);
        deviceSelectionCompositeContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        deviceSelectionCompositeContainer.setLayout(new GridLayout());
        deviceSelectionComposite = new DeviceSelectionComposite(deviceSelectionCompositeContainer, SWT.NONE,
                (MobileDriverType) driverConnector.getDriverType());

        driverPreferenceComposite = new DriverPreferenceComposite(container, SWT.NONE, driverConnector);
        updateInput();
        return container;
    }
    
    protected void updateInput() {
        String deviceId = abstractMobileDriverConnector.getDeviceId();
        deviceSelectionComposite.setDeviceId(deviceId);

        
        Map<String, Object> configProp = new LinkedHashMap<String, Object>();
        configProp.putAll(abstractMobileDriverConnector.getUserConfigProperties());
        configProp.remove(StringConstants.CONF_EXECUTED_DEVICE_ID);
        
        driverPreferenceComposite.setInput(configProp);
    }

    @Override
    public boolean performOk() {       
        try {
            if (driverPreferenceComposite == null || driverPreferenceComposite.isDisposed()) {
                return true;
            }
            
            driverConnector = driverPreferenceComposite.getResult();
            
            if (abstractMobileDriverConnector != null && deviceSelectionComposite != null) {
                abstractMobileDriverConnector.setDevice(deviceSelectionComposite.getSelectedDevice());
            }
            driverConnector.saveUserConfigProperties();
            return true;
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }
}
