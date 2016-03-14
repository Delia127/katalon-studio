package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class IEDriverConnector extends WebUiDriverConnector {
	private String ieDriverPath;
	private int waitForHang;
	
	public IEDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        setIeDriverPath(SeleniumWebDriverProvider.getIEDriverPath());
        setWaitForHang(WebUIExecutionUtil.getWaitForIEHanging());
    }
	
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.IE_DRIVER;
	}

	@Override
	public Map<String, Object> getSystemProperties() {
		Map<String, Object> propertyMap = super.getSystemProperties();
		propertyMap.put(StringConstants.CONF_PROPERTY_IE_DRIVER_PATH, ieDriverPath);
		propertyMap.put(DriverFactory.WAIT_FOR_IE_HANGING_PROPERTY, String.valueOf(waitForHang));
		return propertyMap;
	}

	public String getIeDriverPath() {
		return ieDriverPath;
	}

	public void setIeDriverPath(String ieDriverPath) {
		this.ieDriverPath = ieDriverPath;
	}

	public int getWaitForHang() {
		return waitForHang;
	}

	public void setWaitForHang(int waitForHang) {
		this.waitForHang = waitForHang;
	}

	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            IEDriverConnector ieDriverConnector = new IEDriverConnector(getParentFolderPath());
            ieDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
            return ieDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
