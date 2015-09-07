package com.kms.katalon.objectspy.core;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.kms.katalon.objectspy.filter.CrossOriginFilter;

@SuppressWarnings("restriction")
public class HTMLElementCaptureServer {
	protected Server server;
	
	public HTMLElementCaptureServer(Logger logger, IEventBroker eventBroker) {
		server = new Server(0);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		
		FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
		filterHolder.setInitParameter("allowedMethods", "GET,POST,HEAD,OPTIONS");
		context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

		server.setHandler(context);
		addServlets(logger, eventBroker, context);
	}

	protected void addServlets(Logger logger, IEventBroker eventBroker, ServletContextHandler context) {
		context.addServlet(new ServletHolder(new HTMLElementServlet(logger, eventBroker)), "/*");
	}

	public void start() throws Exception {
		server.start();
	}

	public boolean isRunning() {
		return server.isRunning();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public String getServerUrl() {
		if (server.isStarted()) {
			return "http://localhost:" + server.getConnectors()[0].getLocalPort() + "/";
		}
		return StringUtils.EMPTY;
	}
}
