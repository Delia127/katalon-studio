package com.kms.katalon.objectspy.websocket.messages;

import com.kms.katalon.objectspy.preferences.ObjectSpyPreferences;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonHotKeyData;

public class StartInspectAddonMessage extends AddonMessage {
    public StartInspectAddonMessage() {
        super(AddonCommand.START_INSPECT,
                new StartInspectAddonMessageData(
                        AddonHotKeyData.buildFrom(ObjectSpyPreferences.getCaptureObjectHotKey()),
                        AddonHotKeyData.buildFrom(ObjectSpyPreferences.getLoadDomMapHotKey())));
    }

    public static class StartInspectAddonMessageData {
        private AddonHotKeyData captureObjectHotKey;

        private AddonHotKeyData loadDomMapHotKey;

        public StartInspectAddonMessageData(AddonHotKeyData captureObjectHotKey, AddonHotKeyData loadDomMapHotKey) {
            this.captureObjectHotKey = captureObjectHotKey;
            this.loadDomMapHotKey = loadDomMapHotKey;
        }

        public AddonHotKeyData getCaptureObjectHotKey() {
            return captureObjectHotKey;
        }

        public void setCaptureObjectHotKey(AddonHotKeyData captureObjectHotKey) {
            this.captureObjectHotKey = captureObjectHotKey;
        }

        public AddonHotKeyData getLoadDomMapHotKey() {
            return loadDomMapHotKey;
        }

        public void setLoadDomMapHotKey(AddonHotKeyData loadDomMapHotKey) {
            this.loadDomMapHotKey = loadDomMapHotKey;
        }
    }
}
