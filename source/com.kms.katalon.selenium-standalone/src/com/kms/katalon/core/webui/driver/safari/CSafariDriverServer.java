package com.kms.katalon.core.webui.driver.safari;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.openqa.selenium.net.PortProber;

/**
 * This class copy code from CSafariDriverServer class to overcome it's package-protected fields
 * This class should not be re-factor
 * TODO: This class should be change when our com.kms.katalon.selenium-standalone project is updated
 *
 */
public class CSafariDriverServer {
    private static final Logger LOG = Logger.getLogger(CSafariDriverServer.class.getName());

    private final int port;

    private final BlockingQueue<CWebSocketConnection> connections = new SynchronousQueue<CWebSocketConnection>();

    private ServerBootstrap bootstrap;

    private Channel serverChannel;

    private ChannelGroup channelGroup;

    private int serverPort;

    /**
     * @param port The port the server should be started on, or 0 to use any
     * free port.
     */
    public CSafariDriverServer(int port) {
        checkArgument(port >= 0, "Port must be >= 0: %d", port);
        this.port = port;
    }

    /**
     * Starts the server if it is not already running.
     */
    public void start() {
        start(port);
    }

    private void start(int port) {
        if (serverChannel != null) {
            return;
        }

        serverPort = port == 0 ? PortProber.findFreePort() : port;

        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        channelGroup = new DefaultChannelGroup();
        bootstrap.setPipelineFactory(new CSafariDriverPipelineFactory(serverPort, connections, channelGroup));
        serverChannel = bootstrap.bind(new InetSocketAddress(serverPort));

        LOG.info("Server started on port " + serverPort);
    }

    /**
     * Stops the server if it is running.
     */
    public void stop() {
        if (bootstrap != null) {
            LOG.info("Stopping server");

            channelGroup.close().awaitUninterruptibly();

            serverChannel.close();
            bootstrap.releaseExternalResources();

            serverChannel = null;
            bootstrap = null;
        }
    }

    /**
     * Returns whether the server is currently running.
     */
    public boolean isRunning() {
        return bootstrap != null;
    }

    public String getUri() {
        checkState(serverChannel != null, "The server is not running; call #start()!");
        return "http://localhost:" + serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    /**
     * Waits for a new SafariDriverConnection.
     *
     * @param timeout How long to wait for the new connection.
     * @param unit Unit of time for {@code timeout}.
     * @return The new connection.
     * @throws InterruptedException If the timeout expires.
     */
    public CWebSocketConnection getConnection(long timeout, TimeUnit unit) throws InterruptedException {
        return connections.poll(timeout, unit);
    }
}
