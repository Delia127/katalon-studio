package com.kms.katalon.composer.windows.socket;

import com.kms.katalon.composer.windows.socket.WindowsServerSocketMessage.ServerMessageType;
import com.kms.katalon.core.util.internal.JsonUtil;

public class WindowsSocketMessageUtil {
    public static String createServerMessage(ServerMessageType type, String data) {
        WindowsServerSocketMessage message = new WindowsServerSocketMessage();
        message.setType(type);
        message.setData(data);
        return JsonUtil.toJson(message);
    }

    public static WindowsClientSocketMessage parseClientMessage(String clientRawMessage) {
        return JsonUtil.fromJson(clientRawMessage, WindowsClientSocketMessage.class);
    }

    public static WindowsStartRecordingPayload createStartRecordingPayload(String appPath) {
        WindowsStartRecordingPayload message = new WindowsStartRecordingPayload();
        message.setAppPath(appPath);
        return message;
    }
}
