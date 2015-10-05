package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class IEDriverConnector extends WebUiDriverConnector {
	private String ieDriverPath;
	private int waitForHang;
	
	public IEDriverConnector(String projectDir) throws IOException {
        super(projectDir);
        setIeDriverPath(SeleniumWebDriverProvider.getIEDriverPath());
        setWaitForHang(WebUIExecutionUtil.getWaitForIEHanging());
    }

    public IEDriverConnector(String projectDir, String customProfileName) throws IOException {
        super(projectDir, customProfileName);
        setIeDriverPath(SeleniumWebDriverProvider.getIEDriverPath());
        setWaitForHang(WebUIExecutionUtil.getWaitForIEHanging());
    }

	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.IE_DRIVER;
	}

	@Override
	public Map<String, Object> getExecutionSettingPropertyMap() {
		Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
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

}
