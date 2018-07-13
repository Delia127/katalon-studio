package com.kms.katalon.objectspy.core;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.kms.katalon.objectspy.filter.CrossOriginFilter;

@SuppressWarnings("restriction")
public class HTMLElementCaptureServer {
    protected Server server;

    protected boolean isUsingDynamicPort = false;

    protected ServletContextHandler context;

    public HTMLElementCaptureServer(Logger logger, HTMLElementCollector objectSpyDialog, Class<?> socketClass) {
        this(0, logger, objectSpyDialog, socketClass);
        isUsingDynamicPort = true;
    }

    public HTMLElementCaptureServer(int port, Logger logger, Class<?> socketClass) {
        server = new Server(port);
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter("allowedMethods", "GET,POST,HEAD,OPTIONS");
        context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(context);        
               
        // Initialize javax.websocket layer
        ServerContainer wscontainer;
		try {
			wscontainer = WebSocketServerContainerInitializer.configureContext(context);
	        // Add WebSocket endpoint to javax.websocket layer
	        wscontainer.addEndpoint(socketClass);
	        System.out.println(socketClass);
	        
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeploymentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public HTMLElementCaptureServer(int port, Logger logger, HTMLElementCollector objectSpyDialog, Class<?> socketClass) {
        this(port, logger, socketClass);
        addServlets(logger, objectSpyDialog, context);
    }

    protected void addServlets(Logger logger, HTMLElementCollector objectSpyDialog, ServletContextHandler context) {
        context.addServlet(new ServletHolder(new HTMLElementServlet(logger, objectSpyDialog)), "/*");
    }

    public void start() throws Exception {
        server.start();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public boolean isStarted() {
        return server.isStarted() && server.getConnectors().length > 0 && server.getConnectors()[0] != null;
    }

    public void stop() throws Exception {
        server.stop();
    }

    public String getServerUrl() {
        if (isStarted()) {
            return "http://localhost:" + getServerPort() + "/";
        }
        return StringUtils.EMPTY;
    }

    public int getServerPort() {
        if (isStarted()) {
            return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
        }
        return -1;
    }

    public boolean isUsingDynamicPort() {
        return isUsingDynamicPort;
    }
}
