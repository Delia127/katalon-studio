package com.kms.katalon.core.webui.setting;

import com.kms.katalon.core.driver.DriverType;

public class DriverProperty {
	private String name;
	private Object value;
	private DriverType driverType;
	
	public DriverProperty(String name, Object value, DriverType driverType) {
		this.name = name;
		this.value = value;
		this.setDriverType(driverType);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {		
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public DriverType getDriverType() {
		return driverType;
	}

	public void setDriverType(DriverType driverType) {
		this.driverType = driverType;
	}
	
	public String getRawName() {
		return DriverPropertyStore.getParentKey(driverType) + "." + getName();
	}
	
	public String getRawValue() {
		return WebUiSettingStore.getRawValue(getValue());
	}
}
