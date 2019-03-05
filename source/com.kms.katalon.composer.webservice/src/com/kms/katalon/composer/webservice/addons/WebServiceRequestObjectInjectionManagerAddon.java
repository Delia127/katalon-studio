package com.kms.katalon.composer.webservice.addons;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.webservice.handlers.OpenWebServiceRequestObjectHandler;
import com.kms.katalon.composer.webservice.handlers.RequestHistoryHandler;
import com.kms.katalon.composer.webservice.handlers.WebServiceExecutionHandler;
import com.kms.katalon.core.event.EventBusSingleton;

public class WebServiceRequestObjectInjectionManagerAddon {
    
    @Inject
    IEclipseContext context;

	@PostConstruct
    public void initHandlers() {
	    OpenWebServiceRequestObjectHandler openWsHandler = ContextInjectionFactory.make(OpenWebServiceRequestObjectHandler.class, context);
	    context.set(OpenWebServiceRequestObjectHandler.class, openWsHandler);
        RequestHistoryHandler requestHistoryHandler = ContextInjectionFactory.make(RequestHistoryHandler.class, context);
        context.set(RequestHistoryHandler.class, requestHistoryHandler);
        WebServiceExecutionHandler wsExecutionHandler = ContextInjectionFactory.make(WebServiceExecutionHandler.class, context);
        context.set(WebServiceExecutionHandler.class, wsExecutionHandler);
        
        //TODO: Huy please fix this
        //EventBusSingleton.getInstance().getEventBus().register(wsExecutionHandler);
	}
}
