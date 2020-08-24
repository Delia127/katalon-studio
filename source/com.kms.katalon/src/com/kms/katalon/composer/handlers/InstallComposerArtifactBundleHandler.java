package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.activation.plugin.util.PlatformHelper;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.logging.LogUtil;

public class InstallComposerArtifactBundleHandler {
    
    @Inject
    IEventBroker eventBroker;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED,
                new EventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        try {
                            PlatformHelper.installComposerArtifactBundle();
                        } catch (BundleException e) {
                            LogUtil.logError(e);
                        }
                    }
                });
    }
}
