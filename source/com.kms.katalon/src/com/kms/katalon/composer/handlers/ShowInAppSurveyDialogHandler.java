package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.intro.InAppSurveyDialog;
import com.kms.katalon.composer.intro.UserFeedbackDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.tracking.service.Trackings;

public class ShowInAppSurveyDialogHandler {
    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventListeners() {
        eventBroker.subscribe(EventConstants.WORKSPACE_CLOSED, new EventServiceAdapter() {

            @Override
			public void handleEvent(Event event) {
				ScopedPreferenceStore prefStore = PreferenceStoreManager
						.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
				boolean shouldShowInAppSurveyDialog = prefStore
						.getBoolean(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE);

				if (shouldShowInAppSurveyDialog) {
					InAppSurveyDialog inAppSurveyDialog = new InAppSurveyDialog(Display.getCurrent().getActiveShell());
					inAppSurveyDialog.setBlockOnOpen(true);
					inAppSurveyDialog.open();
					int numberOfStars = inAppSurveyDialog.getNumberOfStars();
					String userIdea = inAppSurveyDialog.getUserIdea();
					int status = inAppSurveyDialog.getStatus();
					switch (status) {
						case 0: // Send
							Trackings.trackInAppSurveyRatingAndIdea(numberOfStars, userIdea);
							prefStore.setValue(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE,
									false);
							break;
						case 1: // Later
							prefStore.setValue(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE,
									true);
							break;
						case 2: // No, thanks
							prefStore.setValue(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE,
									false);
							break;
					}
				}
			}
        });
    }

}
