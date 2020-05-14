package com.katalon.plugin.smart_xpath.settings;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.PluginOptions;
import com.kms.katalon.execution.constants.StringConstants;

public class SelfHealingSettingStore extends BundleSettingStore {

	public SelfHealingSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(), FrameworkUtil.getBundle(SelfHealingSettingStore.class).getSymbolicName(),
                false);
	}

	public PluginOptions getdReloadPluginOption() throws IOException {
		return PluginOptions.valueOf(getString("selfhealing.execution", PluginOptions.ONLINE_AND_OFFLINE.name()));
	}

	public void setReloadPluginOption(PluginOptions reloadOption) throws IOException {
    	setProperty("selfhealing.execution", reloadOption.name());
	}
}