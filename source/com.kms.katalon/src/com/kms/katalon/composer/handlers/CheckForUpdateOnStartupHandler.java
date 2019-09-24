package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

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
        IPreferenceStore prefStore = PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
        boolean checkNewVersion = prefStore.contains(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION)
                ? prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_CHECK_NEW_VERSION) : true;

        boolean checkAllowUsage = prefStore.contains(PreferenceConstants.GENERAL_AUTO_CHECK_ALLOW_USAGE_TRACKING)
                ? prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_CHECK_ALLOW_USAGE_TRACKING) : true;
        if (!checkNewVersion) {
            return;
        }
        else{
            checkForUpdate(true);
        }
        if(!checkAllowUsage) {
            return;
        }
        else{
            trackingAllowUsage();
        }

    }

    private void trackingAllowUsage() {
        Trackings.trackOpenProject(ProjectController.getInstance().getCurrentProject());
        
    }
}
