package com.kms.katalon.composer.webservice.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.webservice.handlers.OpenWebServiceRequestObjectHandler;

public class WebServiceRequestObjectInjectionManagerAddon {

	@PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenWebServiceRequestObjectHandler.class, context);
	}
}
