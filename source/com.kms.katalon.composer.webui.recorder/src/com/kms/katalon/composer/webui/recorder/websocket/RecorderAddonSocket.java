package com.kms.katalon.composer.webui.recorder.websocket;

import java.io.UnsupportedEncodingException;

import javax.websocket.ClientEndpoint;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonSyntaxException;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionJsonParser;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;

@ClientEndpoint
@ServerEndpoint(value = "/")
public class RecorderAddonSocket extends AddonSocket {
	
	@Override
    protected void handleOldElementMessage(String message) {
        try {
            String key = HTMLElementUtil.decodeURIComponent(message.substring(0, message.indexOf(EQUALS)));

            switch (key) {
                case ELEMENT_ACTION_KEY:                	
                    addNewAction(message.substring(message.indexOf(EQUALS) + 1, message.length()));
                    break;
                default:
                	super.handleOldElementMessage(message);
            }
        } catch (UnsupportedEncodingException e) {
            LoggerSingleton.logError(e);
        }
    }
        
    protected void seleniumSocketResponder(){
        sendMessage(new AddonMessage(AddonCommand.START_RECORD));
        System.out.println("WS: Start recording");        
    }

	private void addNewAction(String value) {
		try {
				HTMLActionMapping actionMapping = HTMLActionJsonParser.parseJsonIntoHTMLActionMapping(value);
			EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.RECORDER_HTML_ACTION_CAPTURED,
					actionMapping);
		} catch (JsonSyntaxException | UnsupportedEncodingException e) {
			LoggerSingleton.logError(e);
		}
	}
    
    @Override
    public void sendMessage(AddonMessage message) {
        if (message.getCommand().equals(AddonCommand.HIGHLIGHT_OBJECT)) {
            super.sendMessage(new AddonMessage(AddonCommand.START_INSPECT, null));
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {}
            super.sendMessage(message);
            super.sendMessage(new AddonMessage(AddonCommand.START_RECORD, null));
        } else {
            super.sendMessage(message);
        }
    }
    
    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
    }
}
