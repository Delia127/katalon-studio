package com.kms.katalon.composer.windows.websocket;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class WindowsAddonSocketServer {
    private static WindowsAddonSocketServer instance;

    private Server server;

    private List<WindowsAddonSocket> activeSockets = new ArrayList<>();

    private WindowsAddonSocketServer() {
        // hide constructor
    }

    private ServerConnector windowInspectorConnetor;

    public static WindowsAddonSocketServer getInstance() {
        if (instance == null) {
            instance = new WindowsAddonSocketServer();
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
        windowInspectorConnetor = new ServerConnector(server);
        windowInspectorConnetor.setPort(50002);

        server.addConnector(connector);
        server.addConnector(windowInspectorConnetor);

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

    public synchronized void addActiveSocket(WindowsAddonSocket socket) {
        if (socket == null) {
            return;
        }
        activeSockets.add(socket);
    }

    public synchronized void removeActiveSocket(WindowsAddonSocket socket) {
        if (socket == null) {
            return;
        }
        activeSockets.remove(socket);
    }
}

