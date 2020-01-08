package com.kms.katalon.composer.windows.record;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialogV2;
import com.kms.katalon.objectspy.filter.CrossOriginFilter;

public class WindowsActionsCaptureServer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected Server server;

    protected boolean isUsingDynamicPort = false;

    protected ServletContextHandler context;

    public WindowsActionsCaptureServer(int port, Class<?> socketClass) {
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
    
    public WindowsActionsCaptureServer(WindowsRecorderDialogV2 objectSpyDialog, Class<?> socketClass) {
        this(0, objectSpyDialog, socketClass);
        isUsingDynamicPort = true;
    }

    public WindowsActionsCaptureServer(int port, WindowsRecorderDialogV2 objectSpyDialog, Class<?> socketClass) {
        this(port, socketClass);
        addServlets(objectSpyDialog, context);
    }

    protected void addServlets(WindowsRecorderDialogV2 objectSpyDialog, ServletContextHandler context) {
        context.addServlet(new ServletHolder(new WindowsActionServlet(objectSpyDialog)), "/*");
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

