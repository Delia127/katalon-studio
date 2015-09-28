 
package com.kms.katalon.controller.addon;

import javax.annotation.PostConstruct;

import com.kms.katalon.controller.EntityController;
import com.kms.katalon.dal.setting.IDataProviderSetting;

public class ControllerAddonInjectManager {
	
	@PostConstruct
	void initControllers(IDataProviderSetting dataProviderSetting) {
		EntityController.setDataProviderSetting(dataProviderSetting);
	}
}