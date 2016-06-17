package com.kms.katalon.execution.setting;

import java.io.IOException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.ExecutionSettingConstants;

public class ExecutionSettingStore extends BundleSettingStore {

    public ExecutionSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(),
                FrameworkUtil.getBundle(ExecutionSettingStore.class).getSymbolicName(), false);
    }

    public boolean getScreenCaptureOption() throws IOException {
        return getBoolean(ExecutionSettingConstants.REPORT_TAKE_SCREENSHOT_OPTION, true);
    }

    public void setScreenCaptureOption(boolean screenCaptureEnabled) throws IOException {
        setProperty(ExecutionSettingConstants.REPORT_TAKE_SCREENSHOT_OPTION, screenCaptureEnabled);
    }
}
