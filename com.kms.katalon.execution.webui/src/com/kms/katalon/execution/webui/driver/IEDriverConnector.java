package com.kms.katalon.execution.webui.driver;

import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.entity.AbstractDriverConnector;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class IEDriverConnector extends AbstractDriverConnector {
	private String ieDriverPath;
	private int waitForHang;

	public IEDriverConnector() {
		setIeDriverPath(SeleniumWebDriverProvider.getIEDriverPath());
		setWaitForHang(WebUIExecutionUtil.getWaitForIEHanging());
	}

	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.IE_DRIVER;
	}

	@Override
	public Map<String, String> getPropertyMap() {
		Map<String, String> propertyMap = super.getPropertyMap();
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
