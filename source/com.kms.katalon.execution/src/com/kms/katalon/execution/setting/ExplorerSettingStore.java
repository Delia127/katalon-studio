package com.kms.katalon.execution.setting;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;

public class ExplorerSettingStore extends BundleSettingStore {

    private static final String ITEM_PREFIX = "show-";

    public ExplorerSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(), FrameworkUtil.getBundle(ExplorerSettingStore.class).getSymbolicName(),
                false);
    }

    public void setItemShow(String itemName, boolean isShow) throws IOException, GeneralSecurityException {
        setProperty(ITEM_PREFIX + itemName, isShow);
    }

    public boolean isItemShow(String itemName) throws IOException {
        return getBoolean(ITEM_PREFIX + itemName, true);
    }
}
