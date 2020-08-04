package com.kms.katalon.composer.windows.socket;

import org.openqa.selenium.remote.DesiredCapabilities;

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

    public static WindowsStartRecordingPayload createStartRecordingPayload(String appPath, DesiredCapabilities desiredCapabilities) {
        WindowsStartRecordingPayload message = new WindowsStartRecordingPayload();
        message.setAppPath(appPath);
        message.setDesiredCapabilities(desiredCapabilities.toJson());
        return message;
    }

    public static WindowsStopRecordingPayload createStopRecordingPayload(String appPath) {
        WindowsStopRecordingPayload message = new WindowsStopRecordingPayload();
        message.setAppPath(appPath);
        return message;
    }
}
