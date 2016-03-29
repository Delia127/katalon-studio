package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.IDriverConnector;

public abstract class RemoteDebugDriverConnector extends WebUiDriverConnector {
	
	protected String debugPort;
	
	protected RemoteDebugDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
    }
    
	@Override
	public abstract DriverType getDriverType();
	
	@Override
	public Map<String, Object> getUserConfigProperties() {
        return super.getUserConfigProperties();
	}
	
	public String getDebugPort() {
		return debugPort;
	}

	public void setDebugPort(String debugPort) {
		this.debugPort = debugPort;
	}

    @Override
    protected void loadDriverProperties() throws IOException {
        super.loadDriverProperties();
        debugPort = driverProperties.get(DriverFactory.DEBUG_PORT) == null ? "" : String.valueOf(driverProperties.get(DriverFactory.DEBUG_PORT));
        driverProperties.remove(DriverFactory.DEBUG_PORT);
    }

    @Override
    public void saveUserConfigProperties() throws IOException {
        driverProperties.put(DriverFactory.DEBUG_PORT, debugPort);
        super.saveUserConfigProperties();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
    	RemoteDebugDriverConnector driverConnector = createDriver();
    	if(driverConnector != null){
    		driverConnector.setDebugPort(getDebugPort());
        	driverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
    	}
        return driverConnector;
    }
    
    protected abstract RemoteDebugDriverConnector createDriver();
}
