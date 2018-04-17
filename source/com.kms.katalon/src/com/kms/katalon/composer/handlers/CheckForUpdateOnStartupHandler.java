package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;

public class CheckForUpdateOnStartupHandler extends CheckForUpdatesHandler {
    
    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventListeners() {
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, new EventServiceAdapter() {
            
            @Override
            public void handleEvent(Event event) {
                handleCheckForUpdateAutomatically();
            }
        });
    }

    private void handleCheckForUpdateAutomatically() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        boolean checkNewVersion = prefStore.contains(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION)
                ? prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION) : true;
        if (!checkNewVersion) {
            return;
        }
        checkForUpdate(true);
    }
}
