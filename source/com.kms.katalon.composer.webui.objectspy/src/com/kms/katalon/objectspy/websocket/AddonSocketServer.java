package com.kms.katalon.objectspy.websocket;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.ServerContainer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class AddonSocketServer {
    private static AddonSocketServer instance;

    private Server server;

    private List<AddonSocket> activeSockets = new ArrayList<>();

    private AddonSocketServer() {
        // hide constructor
    }

    private ServerConnector firefoxConnetor;

    public static AddonSocketServer getInstance() {
        if (instance == null) {
            instance = new AddonSocketServer();
        }
        return instance;
    }

    public void start(Class<?> socketClass, int port) {
        if (isRunning()) {
            return;
        }
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        firefoxConnetor = new ServerConnector(server);
        firefoxConnetor.setPort(50001);

        server.addConnector(connector);
        server.addConnector(firefoxConnetor);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        try {
            // Initialize javax.websocket layer
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);

            // Add WebSocket endpoint to javax.websocket layer
            wscontainer.addEndpoint(socketClass);
            System.out.println("port in start = " + port);
            server.start();
            server.dump(System.err);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public boolean isRunning() {
        return server != null && server.isRunning();
    }

    public synchronized void addActiveSocket(AddonSocket socket) {
        if (socket == null) {
            return;
        }
        activeSockets.add(socket);
    }

    public synchronized void removeActiveSocket(AddonSocket socket) {
        if (socket == null) {
            return;
        }
        activeSockets.remove(socket);
    }

    public AddonSocket getAddonSocketByBrowserName(String browserName) {
        if (StringUtils.isEmpty(browserName)) {
            return null;
        }
        synchronized (activeSockets) {
            for (AddonSocket addonSocket : activeSockets) {
                if (addonSocket.getBrowserType() != null
                        && browserName.equals(addonSocket.getBrowserType().toString())) {
                    return addonSocket;
                }
            }
        }
        return null;

    }
    
    public List<AddonSocket> getAllAddonSockets() {
        return activeSockets;
    }
}
