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
import com.kms.katalon.objectspy.websocket.AddonSocket;

@ClientEndpoint
@ServerEndpoint(value = "/")
public class RecorderAddonSocket extends AddonSocket {
    protected void handleOldElementMessage(String message) {
        try {
            String key = HTMLElementUtil.decodeURIComponent(message.substring(0, message.indexOf(EQUALS)));

            switch (key) {
                case ELEMENT_KEY:
                    addNewAction(message.substring(message.indexOf(EQUALS) + 1, message.length()));
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            LoggerSingleton.logError(e);
        }
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
}
