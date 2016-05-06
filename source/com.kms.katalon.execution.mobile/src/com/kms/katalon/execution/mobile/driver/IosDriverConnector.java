package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;

public class IosDriverConnector extends MobileDriverConnector {

    public IosDriverConnector(String configurationFolderPath, IosDeviceInfo device) throws IOException {
        super(configurationFolderPath, device);
    }
    
    public IosDriverConnector(String configurationFolderPath) throws IOException {
        this(configurationFolderPath, null);
    }
    
	@Override
	public DriverType getDriverType() {
		return MobileDriverType.IOS_DRIVER;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            IosDriverConnector iosDriverConnector = new IosDriverConnector(getParentFolderPath(), (IosDeviceInfo) getDevice());
            iosDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
            return iosDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
