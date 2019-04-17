package com.kms.katalon.core.keyword;

import com.kms.katalon.core.setting.BundleSettingStore;

public interface IPluginEventHandler {

	public void handle(IActionProvider actionProvider, BundleSettingStore store);

}
