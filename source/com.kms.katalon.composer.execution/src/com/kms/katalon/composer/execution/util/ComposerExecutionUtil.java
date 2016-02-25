package com.kms.katalon.composer.execution.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.execution.preferences.ExecutionPreferenceDefaultValueInitializer;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants.ExecutionPreferenceConstants;

public class ComposerExecutionUtil {

    /**
     * Update default label for Run drop-down item
     * 
     * @param defaultItemLabel Menu Item label. If null, value will be get from execution preference
     */
    public static void updateDefaultLabelForRunDropDownItem(String defaultItemLabel) {
        MToolItem runToolItem = (MToolItem) ModelServiceSingleton.getInstance().getModelService()
                .find(IdConstants.EXECUTION_TOOL_ITEM_ID, ApplicationSingleton.getInstance().getApplication());
        if (runToolItem == null) return;

        MMenu menu = runToolItem.getMenu();
        if (menu == null || menu.getChildren() == null || menu.getChildren().isEmpty()) return;

        String defaultLabel = " (default)";

        if (defaultItemLabel == null) {
            defaultItemLabel = ((IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                    ExecutionPreferenceConstants.QUALIFIER))
                    .getString(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION);
            if (defaultItemLabel.isEmpty()) {
                defaultItemLabel = ExecutionPreferenceDefaultValueInitializer.EXECUTION_DEFAULT_RUN_CONFIGURATION;
            }
        }

        // Remove previous default label
        for (MMenuElement item : menu.getChildren()) {
            if (StringUtils.contains(item.getLabel(), defaultLabel)) {
                item.setLabel(StringUtils.removeEnd(item.getLabel(), defaultLabel));
                break;
            }
        }

        // Set new default label
        for (MMenuElement item : menu.getChildren()) {
            if (StringUtils.equals(item.getLabel(), defaultItemLabel)) {
                item.setLabel(item.getLabel() + defaultLabel);
                break;
            }
        }
    }
}
