package com.kms.katalon.composer.parts;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

public class ClearLogHandler {
    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void clearLog() {
        eventBroker.post("KATALON_STUDIO/EVENT_LOG/CLEAR_LOG", null);
    }
}
