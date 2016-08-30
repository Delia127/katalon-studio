package com.kms.katalon.composer.execution.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;

import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class WrapMessageLogHandler {
    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute(@Optional MDirectMenuItem item) {
        try {
            ScopedPreferenceStore store = getPreferenceStore(WrapMessageLogHandler.class);
            switch (item.getElementId()) {
                case IdConstants.LOG_VIEWER_MENU_ITEM_WORD_WRAP: {
                    store.setValue(ComposerExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP, item.isSelected());
                    break;
                }
                default:
                    break;
            }
            store.save();
            eventBroker.post(EventConstants.CONSOLE_LOG_WORD_WRAP, null);
        } catch (IOException e) {
            logError(e);
        }
    }
}
