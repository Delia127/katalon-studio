package com.kms.katalon.execution.setting;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.PluginOptions;
import com.kms.katalon.execution.constants.StringConstants;

public class PluginSettingStore extends BundleSettingStore {

	public PluginSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(), FrameworkUtil.getBundle(PluginSettingStore.class).getSymbolicName(),
                false);
	}
	
	public PluginOptions getdReloadPluginOption() throws IOException {
		return PluginOptions.valueOf(getString(StringConstants.PLUGIN_RELOAD_OPTION, PluginOptions.ONLINE_AND_OFFLINE.name()));
	}
	
	public void setReloadPluginOption(PluginOptions reloadOption) throws IOException {
    	setProperty(StringConstants.PLUGIN_RELOAD_OPTION, reloadOption.name());
	}
}
