package com.kms.katalon.composer.windows.socket;

import com.google.gson.annotations.SerializedName;

public class WindowsServerSocketMessage {
    @SerializedName("Type")
    private ServerMessageType type;

    @SerializedName("Data")
    private String data;

    public ServerMessageType getType() {
        return type;
    }

    public void setType(ServerMessageType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public enum ServerMessageType {
        START_RECORDING, STOP_RECORDING, PAUSE_RECORDING, EXIT
    }
}
