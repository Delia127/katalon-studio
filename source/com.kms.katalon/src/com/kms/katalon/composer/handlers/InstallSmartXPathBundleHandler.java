package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;

import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.util.PlatformHelper;

public class InstallSmartXPathBundleHandler {
    @PostConstruct
    private void registerEventHandler() {
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.ACTIVATION_CHECKED,
                new EventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        try {
                            if (LicenseUtil.isNotFreeLicense()) {
                                PlatformHelper.installSmartXPathBundle();
                            }
                        } catch (BundleException e) {
                            LogUtil.logError(e);
                        }
                    }
                });
        
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.ACTIVATION_DEACTIVATED,
                new EventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        try {
                            if (PlatformHelper.isSmartXPathBundleInstalled()) {
                                PlatformHelper.uninstallSmartXPathBundle();
                            }
                        } catch (BundleException e) {
                            LogUtil.logError(e);
                        }
                    }
                });
    }
}
