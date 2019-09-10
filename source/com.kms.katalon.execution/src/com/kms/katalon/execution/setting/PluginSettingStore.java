package com.kms.katalon.execution.setting;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.PluginOptions;

public class PluginSettingStore extends BundleSettingStore {

    public PluginSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(), FrameworkUtil.getBundle(PluginSettingStore.class).getSymbolicName(),
                false);
    }

    
    public PluginOptions getReloadOption() throws IOException {
		return PluginOptions.valueOf(
			getString(PreferenceConstants.PLUGIN_RELOAD_OPTION, PluginOptions.ONLINE_AND_OFFLINE.name())
		);
    }
    
    public void setReloadOption(PluginOptions option) throws IOException {
		setProperty(PreferenceConstants.PLUGIN_RELOAD_OPTION, option.name());
    }
}
