package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class IosDriverConnector extends MobileDriverConnector {

    public IosDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
	@Override
	public DriverType getDriverType() {
		return MobileDriverType.IOS_DRIVER;
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            IosDriverConnector iosDriverConnector = new IosDriverConnector(getParentFolderPath());
            iosDriverConnector.setDeviceName(getDeviceName());
            iosDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return iosDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
