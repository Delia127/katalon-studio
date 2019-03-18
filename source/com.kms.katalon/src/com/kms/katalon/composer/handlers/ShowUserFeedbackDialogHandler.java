package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.intro.UserFeedbackDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;

@Deprecated
public class ShowUserFeedbackDialogHandler {

    private static final int APP_CLOSES_LIMIT = 4;

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventListeners() {
        eventBroker.subscribe(EventConstants.WORKSPACE_CLOSED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                ScopedPreferenceStore prefStore = PreferenceStoreManager
                        .getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
                int numberOfAppCloses = prefStore.getInt(PreferenceConstants.GENERAL_NUMBER_OF_APP_CLOSES);
                boolean shouldShowFeedbackDialog = prefStore
                        .getBoolean(PreferenceConstants.GENERAL_SHOW_USER_FEEDBACK_DIALOG_ON_APP_CLOSE);
                numberOfAppCloses++;
                if (numberOfAppCloses >= APP_CLOSES_LIMIT && shouldShowFeedbackDialog) {
                    UserFeedbackDialog feedbackDialog = new UserFeedbackDialog(Display.getCurrent().getActiveShell());
                    feedbackDialog.setBlockOnOpen(true);
                    Trackings.trackOpenTwitterDialog();
                    feedbackDialog.open();
                }

                prefStore.setValue(PreferenceConstants.GENERAL_NUMBER_OF_APP_CLOSES, numberOfAppCloses);
            }
        });
    }
}
