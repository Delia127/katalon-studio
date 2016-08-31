package com.kms.katalon.composer.execution.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;

import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class PinLogViewHander {

    @Execute
    public void execute(@Optional MDirectToolItem item) {
        try {
            ScopedPreferenceStore store = getPreferenceStore(PinLogViewHander.class);
            switch (item.getElementId()) {
                case IdConstants.LOG_VIEWER_TOOL_ITEM_PIN_ID:
                    store.setValue(ComposerExecutionPreferenceConstants.EXECUTION_PIN_LOG, item.isSelected());
                    break;
                default:
                    break;
            }
            store.save();
        } catch (IOException e) {
            logError(e);
        }
    }
}
