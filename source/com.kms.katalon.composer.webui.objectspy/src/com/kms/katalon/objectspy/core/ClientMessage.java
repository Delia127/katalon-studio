package com.kms.katalon.objectspy.core;

import java.util.regex.Pattern;

public class ClientMessage {
    public int clientId;

    public int prevRequestId;

    public String messageType;

    private static Pattern messageSeparator = Pattern.compile(MessageConstant.REQUEST_SEPARATOR, Pattern.LITERAL);

    public ClientMessage(String message) {
        message = message.trim();
        
        int idx = message.indexOf("=");
        String clientRequest = message.substring(0, idx);

        if (clientRequest.equals(RequestType.GET_CLIENT_ID)) {
            this.clientId = -1;
            this.prevRequestId = -1;
        } else {
            String[] parts = messageSeparator.split(message.substring(idx + 1));
            this.clientId = Integer.parseInt(parts[0]);
            this.prevRequestId = Integer.parseInt(parts[1]);
        }

        this.messageType = clientRequest;
    }
}
