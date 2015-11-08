package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class EdgeDriverConnector extends WebUiDriverConnector {
	private String edgeDriverPath;
	
	public EdgeDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        setEdgeDriverPath(SeleniumWebDriverProvider.getEdgeDriverPath());
    }
	
	@Override
	public DriverType getDriverType() {
		return WebUIDriverType.EDGE_DRIVER;
	}

	@Override
	public Map<String, Object> getExecutionSettingPropertyMap() {
		Map<String, Object> propertyMap = super.getExecutionSettingPropertyMap();
		propertyMap.put(StringConstants.CONF_PROPERTY_EDGE_DRIVER_PATH, edgeDriverPath);
		return propertyMap;
	}

	public String getEdgeDriverPath() {
		return edgeDriverPath;
	}

	public void setEdgeDriverPath(String edgeDriverPath) {
		this.edgeDriverPath = edgeDriverPath;
	}

	@SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
        try {
            EdgeDriverConnector ieDriverConnector = new EdgeDriverConnector(getParentFolderPath());
            ieDriverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getDriverProperties());
            return ieDriverConnector;
        } catch (IOException e) {
            // do nothing
        }
        return null;
    }
}
