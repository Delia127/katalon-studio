package com.kms.katalon.execution.webui.driver;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.IDriverConnector;

public abstract class RemoteDebugDriverConnector extends WebUiDriverConnector {
	
	protected String debugPort, debugHost;

	public RemoteDebugDriverConnector(String configurationFolderPath) throws IOException {
        super(configurationFolderPath);
        if(debugHost == null || debugHost.isEmpty()){
        	debugHost = DriverFactory.DEFAULT_DEBUG_HOST;	
        }
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

	public String getDebugHost() {
		return debugHost;
	}

	public void setDebugHost(String debugHost) {
		this.debugHost = debugHost;
	}

    @Override
    protected void loadDriverProperties() throws IOException {
        super.loadDriverProperties();
        debugPort = driverProperties.get(DriverFactory.DEBUG_PORT) == null ? "" : String.valueOf(driverProperties.get(DriverFactory.DEBUG_PORT));
        debugHost = driverProperties.get(DriverFactory.DEBUG_HOST) == null ? "" : String.valueOf(driverProperties.get(DriverFactory.DEBUG_HOST));
        driverProperties.remove(DriverFactory.DEBUG_PORT);
        driverProperties.remove(DriverFactory.DEBUG_HOST);
    }
    
    @Override
    public void saveUserConfigProperties() throws IOException {
        driverProperties.put(DriverFactory.DEBUG_PORT, debugPort);
        driverProperties.put(DriverFactory.DEBUG_HOST, debugHost);
        super.saveUserConfigProperties();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IDriverConnector clone() {
    	RemoteDebugDriverConnector driverConnector = createDriver();
    	if(driverConnector != null){
    	driverConnector.setDebugPort(getDebugPort());
    	driverConnector.setDebugHost(getDebugHost());
    	driverConnector.driverProperties = (Map<String, Object>) cloneDriverPropertyValue(getUserConfigProperties());
    	}
        return driverConnector;
    }
    
    @Override
    public Map<String, Object> getSystemProperties() {    	
    	Map<String, Object> propertyMap = super.getSystemProperties();
        propertyMap.put(DriverFactory.DEBUG_HOST, debugHost);
        propertyMap.put(DriverFactory.DEBUG_PORT, debugPort);
        return propertyMap;	
    }
    
    protected abstract RemoteDebugDriverConnector createDriver();
}
