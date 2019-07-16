package com.kms.katalon.objectspy.websocket;

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

import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;
import com.kms.katalon.objectspy.websocket.messages.BrowserInfoMessageData;
import com.kms.katalon.objectspy.websocket.messages.KatalonVersionAddOnMessage;
import com.kms.katalon.objectspy.websocket.messages.StartInspectAddonMessage;

@ClientEndpoint
@ServerEndpoint(value = "/")
public class AddonSocket {
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

	private AddonBrowserType browserType;

	private AddonSocketServer socketServer;

	private boolean isConnected = false;

	private WebDriver runningDriver;

	public AddonBrowserType getBrowserType() {
		return browserType;
	}

	@OnOpen
	public void onWebSocketConnect(Session sess) {
		session = sess;
		isConnected = true;
		setupSession();
		socketServer = AddonSocketServer.getInstance();
		socketServer.addActiveSocket(this);
		// Request browser info AND send Katalon version to addon
		sendMessage(new KatalonVersionAddOnMessage());
	}

	protected void seleniumSocketResponder() {
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
		if (message.indexOf(EQUALS) == -1) {
			handleCommandMessage(message);
			return;
		}
		handleOldElementMessage(message);
	}

	protected void handleOldElementMessage(String message) {
		try {
			String key = HTMLElementUtil.decodeURIComponent(message.substring(0, message.indexOf(EQUALS)));
			switch (key) {
			case ELEMENT_KEY:
				addNewElement(message.substring(message.indexOf(EQUALS) + 1, message.length()));
				break;
			case ELEMENT_MAP_KEY:
				updateHTMLDOM(message);
				break;
			case SELENIUM_SOCKET_KEY:
				System.out.println("Client is a Selenium Socket");
				seleniumSocketResponder();
				break;
			}
		} catch (UnsupportedEncodingException e) {
			LoggerSingleton.logError(e);
		}
	}

	private void handleCommandMessage(String message) {
		try {
			Gson gson = new Gson();
			JsonElement jsonElement = new JsonParser().parse(message);
			if (!jsonElement.isJsonObject()) {
				return;
			}
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			AddonCommand command = AddonCommand.valueOf(jsonObject.getAsJsonPrimitive(COMMAND_KEY).getAsString());
			switch (command) {
			case BROWSER_INFO:
				BrowserInfoMessageData data = gson.fromJson(jsonObject.getAsJsonObject(DATA_KEY),
						BrowserInfoMessageData.class);
				browserType = AddonBrowserType.valueOf(data.getBrowserName());
				break;
			default:
				break;
			}
		} catch (JsonSyntaxException | IllegalStateException e) {
			LoggerSingleton.logError(e);
		}
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

	private void updateHTMLDOM(String sb) {
		final String value = sb.substring(sb.indexOf(EQUALS) + 1, sb.length());
		new Thread(new Runnable() {
			@Override
			public void run() {
				Document htmlDocument = null;
				HTMLRawElement newRootElement = null;
				try {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

					// root elements
					htmlDocument = docBuilder.newDocument();
					newRootElement = HTMLElementUtil.buildHTMLRawElement(htmlDocument, value);
				} catch (Exception e) {
					LoggerSingleton.logError(e);
				}
				if (htmlDocument != null && newRootElement != null) {
					EventBrokerSingleton.getInstance().getEventBroker().post(
							EventConstants.OBJECT_SPY_HTML_DOM_CAPTURED, new Object[] { newRootElement, htmlDocument });
				}
			}
		}).run();
	}

	private void addNewElement(String value) {
		try {
			// Note that, the web socket is used for Firefox plugin only
			EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.OBJECT_SPY_HTML_ELEMENT_CAPTURED,
					WebElementUtils.buildWebElement(value));
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
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

	/**
	 * Set a {@link WebDriver} instance for this addon socket. This instance
	 * will be delegated into other controllers as long as the text message
	 * received from the add-on.
	 * 
	 * @param driver
	 */
	public void setRunningDriver(WebDriver driver) {
		if(this.runningDriver == null) {
			this.runningDriver = driver;
		}
	}

	/**
	 * Get
	 * 
	 * @return {@link WebDriver} set by
	 *         {@link AddonSocket#setRunningDriver(WebDriver)} if it was called,
	 *         otherwise return null
	 */
	public WebDriver getRunningDriver() {
		return this.runningDriver;
	}
}
