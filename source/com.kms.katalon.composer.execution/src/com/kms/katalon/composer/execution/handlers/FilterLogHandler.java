package com.kms.katalon.composer.execution.handlers;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;

public class FilterLogHandler {

    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(@Optional MDirectToolItem item) {
        try {
            ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
                    PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
            switch (item.getElementId()) {
            case IdConstants.LOG_VIEWER_TOOL_ITEM_ALL_ID:
                store.setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_ALL_LOGS,
                        item.isSelected());
                break;
            case IdConstants.LOG_VIEWER_TOOL_ITEM_INFO_ID:
                store.setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_INFO_LOGS,
                        item.isSelected());
                break;
            case IdConstants.LOG_VIEWER_TOOL_ITEM_PASSED_ID:
                store.setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_PASSED_LOGS,
                        item.isSelected());
                break;
            case IdConstants.LOG_VIEWER_TOOL_ITEM_FAILED_ID:
                store.setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_FAILED_LOGS,
                        item.isSelected());
                break;
            case IdConstants.LOG_VIEWER_TOOL_ITEM_ERROR_ID:
                store.setValue(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_ERROR_LOGS,
                        item.isSelected());
                break;
            }
            store.save();
            eventBroker.post(EventConstants.CONSOLE_LOG_REFRESH, null);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

    }
}
