package com.kms.katalon.execution.mobile.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.logging.LogUtil;

public class IosDriverConnector extends MobileDriverConnector {

    public IosDriverConnector(String configurationFolderPath, IosDeviceInfo device) throws IOException {
        super(configurationFolderPath, device);
    }
    
    public IosDriverConnector(String configurationFolderPath) throws IOException {
        this(configurationFolderPath, null);
    }
    
    public IosDriverConnector() throws IOException {
        this(null, null);
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
	
	@Override
    public Map<String, Object> getSystemProperties() {
        Map<String, Object> systemProperties = super.getSystemProperties();
        try {
            systemProperties.put(StringConstants.XML_LOG_DEVICE_CONSOLE_PATH_PROPERTY, 
                    IosDeviceInfo.getDeviceConsoleFolder().getAbsolutePath());
        } catch (IOException e) {
            LogUtil.logError(e);
        }
        return systemProperties;
    }
}
