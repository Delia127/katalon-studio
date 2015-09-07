package com.kms.katalon.composer.webui.recorder.core;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;

@SuppressWarnings("restriction")
public class HTMLElementRecorderServer extends HTMLElementCaptureServer {

	public HTMLElementRecorderServer(Logger logger, IEventBroker eventBroker) {
		super(logger, eventBroker);
	}
	
	@Override
	protected void addServlets(Logger logger, IEventBroker eventBroker, ServletContextHandler context) {
		context.addServlet(new ServletHolder(new HTMLElementRecorderServlet(logger, eventBroker)), "/*");
	}
}
