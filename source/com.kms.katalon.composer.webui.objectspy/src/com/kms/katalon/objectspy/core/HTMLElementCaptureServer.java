package com.kms.katalon.objectspy.core;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.kms.katalon.objectspy.dialog.ObjectSpyDialog;
import com.kms.katalon.objectspy.filter.CrossOriginFilter;

@SuppressWarnings("restriction")
public class HTMLElementCaptureServer {
    protected Server server;

    protected boolean isUsingDynamicPort = false;

    protected ServletContextHandler context;

    public HTMLElementCaptureServer(Logger logger, ObjectSpyDialog objectSpyDialog) {
        this(0, logger, objectSpyDialog);
        isUsingDynamicPort = true;
    }

    public HTMLElementCaptureServer(int port, Logger logger) {
        server = new Server(port);
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        FilterHolder filterHolder = new FilterHolder(new CrossOriginFilter());
        filterHolder.setInitParameter("allowedMethods", "GET,POST,HEAD,OPTIONS");
        context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(context);
    }

    public HTMLElementCaptureServer(int port, Logger logger, ObjectSpyDialog objectSpyDialog) {
        this(port, logger);
        addServlets(logger, objectSpyDialog, context);
    }

    protected void addServlets(Logger logger, ObjectSpyDialog objectSpyDialog, ServletContextHandler context) {
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
