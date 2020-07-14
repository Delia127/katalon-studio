package com.kms.katalon.composer.platform.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.platform.internal.report.ReportIntegrationPlatformBuilderImpl;
import com.kms.katalon.composer.platform.internal.testcase.TestCaseIntegrationPlatformBuilderImpl;
import com.kms.katalon.composer.platform.internal.testsuite.DynamicQueryingTestSuiteProviderImpl;
import com.kms.katalon.composer.platform.internal.testsuite.TestSuiteCollectionUIViewPlatformBuilderImpl;
import com.kms.katalon.composer.platform.internal.testsuite.TestSuiteIntegrationPlatformBuilderImpl;
import com.kms.katalon.composer.platform.internal.testsuite.TestSuiteUIViewPlatformBuilderImpl;
import com.kms.katalon.composer.report.platform.PlatformReportIntegrationViewBuilder;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationPlatformBuilder;
import com.kms.katalon.composer.testsuite.collection.platform.PlatformTestSuiteCollectionUIViewBuilder;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationPlatformBuilder;
import com.kms.katalon.composer.testsuite.platform.PlatformTestSuiteUIViewBuilder;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.platform.DynamicQueryingTestSuiteExtensionProvider;

public class ComposerPlatformActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        activatePlatform(context);
    }
    
    private void activatePlatform(BundleContext context) throws BundleException {
        IEclipseContext bundleEclipseContext = EclipseContextFactory.getServiceContext(context);

        IEventBroker eventBroker = bundleEclipseContext.get(IEventBroker.class);

        DynamicQueryingTestSuiteExtensionProvider dynamicQueryingIntegrationViewBuilder = ContextInjectionFactory
                .make(DynamicQueryingTestSuiteProviderImpl.class, bundleEclipseContext);
        context.registerService(DynamicQueryingTestSuiteExtensionProvider.class, dynamicQueryingIntegrationViewBuilder, null);

        
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, new EventHandler() {
            
            @Override
            public void handleEvent(Event event) {
                PlatformUIServiceProvider platformServiceProvider = PlatformUIServiceProvider.getInstance();
                eventBroker.send("KATALON_PLUGIN/UISERVICE_MANAGER_ADDED", platformServiceProvider.getUiServiceManager());

                IEclipseContext workbenchEclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
                BundleContext bundleContext = Platform.getBundle(IdConstants.KATALON_PLATFORM_BUNDLE_ID).getBundleContext();
                bundleContext.registerService(IEclipseContext.class, workbenchEclipseContext, null);

                TestCaseIntegrationPlatformBuilder testCaseIntegrationViewBuilder = ContextInjectionFactory
                        .make(TestCaseIntegrationPlatformBuilderImpl.class, workbenchEclipseContext);
                bundleContext.registerService(TestCaseIntegrationPlatformBuilder.class, testCaseIntegrationViewBuilder,
                        null);

                TestSuiteIntegrationPlatformBuilder testSuiteIntegrationViewBuilder = ContextInjectionFactory
                        .make(TestSuiteIntegrationPlatformBuilderImpl.class, workbenchEclipseContext);
                bundleContext.registerService(TestSuiteIntegrationPlatformBuilder.class,
                        testSuiteIntegrationViewBuilder, null);

                PlatformReportIntegrationViewBuilder reportIntegrationViewBuilder = ContextInjectionFactory
                        .make(ReportIntegrationPlatformBuilderImpl.class, workbenchEclipseContext);
                bundleContext.registerService(PlatformReportIntegrationViewBuilder.class, reportIntegrationViewBuilder,
                        null);
                
                PlatformTestSuiteUIViewBuilder testSuiteUiViewBuilder = ContextInjectionFactory
                        .make(TestSuiteUIViewPlatformBuilderImpl.class, workbenchEclipseContext);
                bundleContext.registerService(PlatformTestSuiteUIViewBuilder.class, testSuiteUiViewBuilder, 
                        null);
                
                PlatformTestSuiteCollectionUIViewBuilder testSuiteCollectionUiViewBuilder = ContextInjectionFactory
                        .make(TestSuiteCollectionUIViewPlatformBuilderImpl.class, workbenchEclipseContext);
                bundleContext.registerService(PlatformTestSuiteCollectionUIViewBuilder.class, testSuiteCollectionUiViewBuilder, 
                        null);
            }
        });
       
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
