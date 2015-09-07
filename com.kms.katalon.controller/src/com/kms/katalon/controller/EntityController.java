package com.kms.katalon.controller;

import com.kms.katalon.dal.setting.IDataProviderSetting;

public abstract class EntityController {
	protected static IDataProviderSetting dataProviderSetting;
	
	protected EntityController() {};
	
	protected static IDataProviderSetting getDataProviderSetting() {
		return dataProviderSetting;
	}
	
	public static void setDataProviderSetting(IDataProviderSetting dataProviderSetting) {
		EntityController.dataProviderSetting = dataProviderSetting;
	}
}
