package com.kms.katalon.core.webui.driver.safari;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.QueryStringEncoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;

/**
 * This class copy code from SafariDriverChannelHandler class to overcome it's package-protected fields
 * This class should not be re-factor
 * TODO: This class should be change when our com.kms.katalon.selenium-standalone project is updated
 *
 */
public class CSafariDriverChannelHandler extends SimpleChannelUpstreamHandler {
    private final Logger log = Logger.getLogger(CSafariDriverChannelHandler.class.getName());

    private static final String CLIENT_RESOURCE_PATH = String.format("/%s/client.js",
            "org.openqa.selenium.safari".replace('.', '/'));

    private final BlockingQueue<CWebSocketConnection> connectionQueue;

    private final int port;

    public CSafariDriverChannelHandler(int port, BlockingQueue<CWebSocketConnection> connectionQueue) {
        this.port = port;
        this.connectionQueue = connectionQueue;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) e.getMessage();
            if ("websocket".equalsIgnoreCase(request.getHeader(HttpHeaders.Names.UPGRADE))) {
                performWebSocketHandshake(ctx, request);
                return;
            }

            List<String> connectionHeaders = request.getHeaders(HttpHeaders.Names.CONNECTION);
            for (String header : connectionHeaders) {
                if ("upgrade".equalsIgnoreCase(header)) {
                    performWebSocketHandshake(ctx, request);
                    return;
                }
            }

            if (request.getMethod() != HttpMethod.GET && request.getMethod() != HttpMethod.HEAD) {
                sendNotAllowedResponse(ctx, request, HttpMethod.GET, HttpMethod.HEAD);
            } else if ("/favicon.ico".equals(request.getUri())) {
                handleFaviconRequest(ctx, request);
            } else {
                handleMainPageRequest(ctx, request);
            }

        } else {
            ctx.sendUpstream(e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.log(Level.WARNING, e.getCause().getMessage(), e.getCause());
        e.getChannel().close();
    }

    private void handleFaviconRequest(ChannelHandlerContext ctx, HttpRequest request) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, 0);
        sendResponse(ctx, request, response);
    }

    private void handleMainPageRequest(ChannelHandlerContext ctx, HttpRequest request) throws IOException {
        String url = String.format("ws://localhost:%d", port);
        QueryStringDecoder queryString = new QueryStringDecoder(request.getUri());

        HttpResponse response;
        List<String> urls = queryString.getParameters().get("url");
        if (urls == null || urls.isEmpty() || !url.equals(urls.iterator().next())) {
            response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);

            QueryStringEncoder encoder = new QueryStringEncoder("/connect.html");
            encoder.addParam("url", url);
            response.addHeader("Location", encoder.toString());

        } else {
            response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

            URL clientLibUrl = getClass().getResource(CLIENT_RESOURCE_PATH);
            String content = "<!DOCTYPE html>\n<script>" + Resources.toString(clientLibUrl, Charsets.UTF_8)
                    + "</script>";

            response.setContent(ChannelBuffers.copiedBuffer(content, Charsets.UTF_8));
            response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, response.getContent().readableBytes());
        }

        sendResponse(ctx, request, response);
    }

    private void performWebSocketHandshake(final ChannelHandlerContext ctx, HttpRequest request) {
        log.fine("Performing WebSocket handshake");

        if (request.getMethod() != HttpMethod.GET) {
            log.fine("Invalid handshake method: " + request.getMethod() + "; must be GET");
            sendNotAllowedResponse(ctx, request, HttpMethod.GET);
            return;
        }

        String websocketUrl = String.format("ws://%s%s", request.getHeader(HttpHeaders.Names.HOST), request.getUri());
        String noSubProtocols = null;
        boolean noExtensions = false;

        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(websocketUrl,
                noSubProtocols, noExtensions);
        WebSocketServerHandshaker handshaker = handshakerFactory.newHandshaker(request);

        if (handshaker == null) {
            handshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
        } else {
            handshaker.handshake(ctx.getChannel(), request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        log.warning("WebSocket handshake failed");
                        return;
                    }

                    log.info("Connection opened");

                    CWebSocketConnection webSocketConnection = new CWebSocketConnection(ctx.getChannel());
                    if (!connectionQueue.offer(webSocketConnection)) {
                        log.warning("Failed to register new WebSocket connection");
                    }
                }
            });
        }
    }

    private void sendNotAllowedResponse(ChannelHandlerContext ctx, HttpRequest request, HttpMethod... allowedMethods) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED);
        response.setHeader(HttpHeaders.Names.ALLOW, Joiner.on(",").join(allowedMethods));
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, 0);
        sendResponse(ctx, request, response);
    }

    private void sendResponse(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response) {
        ChannelFuture future = ctx.getChannel().write(response);
        if (!HttpHeaders.isKeepAlive(request) || response.getStatus().getCode() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
