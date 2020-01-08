package com.kms.katalon.composer.windows.websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;
import com.kms.katalon.objectspy.websocket.messages.KatalonVersionAddOnMessage;
import com.kms.katalon.objectspy.websocket.messages.StartInspectAddonMessage;

@ClientEndpoint
@ServerEndpoint(value = "/")
public class WindowsAddonSocket {
    private static final String DATA_KEY = "data";

    private static final String COMMAND_KEY = "command";

    private static final int DEFAULT_MAX_TEXT_MESSAGE_SIZE = 1024 * 1024 * 10; // 10MB

    public static final String TEXT_HTML = "text/html";

    protected static final String EQUALS = "=";

    protected static final String ELEMENT_KEY = "element";

    private static final String ELEMENT_MAP_KEY = "elementsMap";
    
    protected static final String ELEMENT_ACTION_KEY = "elementAction";
    
    private static final String SELENIUM_SOCKET_KEY = "SELENIUM_SOCKET";

    private Session session;
    
    private WindowsAddonSocketServer socketServer;
    
    private boolean isConnected = false;

    @OnOpen
    public void onWebSocketConnect(Session sess) {
        session = sess;
        isConnected = true;
        setupSession();
        socketServer = WindowsAddonSocketServer.getInstance();
        socketServer.addActiveSocket(this);
        // Request browser info AND send Katalon version to addon
        sendMessage(new KatalonVersionAddOnMessage());               
    }
    
    protected void seleniumSocketResponder(){       
        sendMessage(new StartInspectAddonMessage());
    }
    
    private void setupSession() {
        session.setMaxTextMessageBufferSize(DEFAULT_MAX_TEXT_MESSAGE_SIZE);
    }

    public void sendMessage(AddonMessage message) {
        sendText(new Gson().toJson(message));
    }

    private void sendText(String text) {
        try {
            Basic remote = session.getBasicRemote();
            remote.sendText(text);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @OnMessage
    public void onWebSocketText(String message) {
        handleOldElementMessage(message);
    }

    protected void handleOldElementMessage(String message) {
        System.out.println(message);
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        socketServer.removeActiveSocket(this);
        isConnected = false;
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        LoggerSingleton.logError(cause);
        socketServer.removeActiveSocket(this);
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
    
    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
}

