package com.kms.katalon.composer.execution.util;

import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.execution.menu.ExecutionHandledMenuItem;
import com.kms.katalon.composer.execution.preferences.ExecutionPreferenceDefaultValueInitializer;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants.ExecutionPreferenceConstants;

public class ComposerExecutionUtil {

    /**
     * Update default label for Run drop-down item
     */
    public static void updateDefaultLabelForRunDropDownItem() {
        String defaultItemLabel = ((IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                ExecutionPreferenceConstants.QUALIFIER))
                .getString(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
        if (isBlank(defaultItemLabel)) {
            defaultItemLabel = ExecutionPreferenceDefaultValueInitializer.EXECUTION_DEFAULT_RUN_CONFIGURATION;
        }

        updateDefaultLabelForRunDropDownItem(defaultItemLabel);
    }

    /**
     * Update default label for Run drop-down item
     * 
     * @param defaultItemLabel Menu Item label
     */
    public static void updateDefaultLabelForRunDropDownItem(String defaultItemLabel) {
        MToolItem runToolItem = (MToolItem) ModelServiceSingleton.getInstance().getModelService()
                .find(IdConstants.EXECUTION_TOOL_ITEM_ID, ApplicationSingleton.getInstance().getApplication());
        if (runToolItem == null) return;

        final MMenu menu = runToolItem.getMenu();
        if (menu == null || menu.getChildren() == null || menu.getChildren().isEmpty()) return;

        if (isBlank(defaultItemLabel)) return;

        // Set new default label
        for (MMenuElement item : menu.getChildren()) {
            if (item instanceof ExecutionHandledMenuItem) {
                ExecutionHandledMenuItem wrappedItem = (ExecutionHandledMenuItem) item;
                if (contains(item.getLabel(), defaultItemLabel)) {
                    wrappedItem.setDefault(true);
                    continue;
                }
                wrappedItem.setDefault(false);
            }
        }
    }
}
