package com.kms.katalon.objectspy.websocket.messages;

import com.kms.katalon.objectspy.websocket.AddonCommand;

public class AddonMessage {
    private AddonCommand command;

    private Object data;
    
    public AddonMessage(AddonCommand command) {
        this.command = command;
    }
    
    public AddonMessage(AddonCommand command, Object data) {
        this(command);
        this.data = data;
    }

    public AddonCommand getCommand() {
        return command;
    }

    public void setCommand(AddonCommand command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
