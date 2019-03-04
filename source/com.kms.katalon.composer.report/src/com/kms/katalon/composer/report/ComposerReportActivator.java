package com.kms.katalon.composer.report;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.platform.PlatformReportIntegrationViewBuilder;

public class ComposerReportActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.addServiceListener(new ServiceListener() {

            @Override
            public void serviceChanged(ServiceEvent event) {
                Object service = context.getService(event.getServiceReference());
                if (service instanceof PlatformReportIntegrationViewBuilder) {
                    ReportComposerIntegrationFactory.getInstance()
                            .addPlatformViewerBuilder((PlatformReportIntegrationViewBuilder) service);
                    context.removeServiceListener(this);
                }
            }
        });
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
