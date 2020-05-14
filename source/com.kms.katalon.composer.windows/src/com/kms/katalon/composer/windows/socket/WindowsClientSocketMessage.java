package com.kms.katalon.composer.windows.socket;

import com.google.gson.annotations.SerializedName;

public class WindowsClientSocketMessage {
    @SerializedName("Type")
    private ClientMessageType type;

    @SerializedName("Data")
    private String data;

    public ClientMessageType getType() {
        return type;
    }

    public void setType(ClientMessageType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public enum ClientMessageType {
        CONNECT, APP_INFO, RECORDING_ACTION
    }
}
