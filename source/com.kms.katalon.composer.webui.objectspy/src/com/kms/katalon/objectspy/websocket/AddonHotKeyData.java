package com.kms.katalon.objectspy.websocket;

import java.awt.event.KeyEvent;

/**
 * Data class to send to javascript addon
 *
 */
public class AddonHotKeyData {
    private int keyCode;

    private boolean useAltKey = false;

    private boolean useShiftKey = false;

    private boolean useCtrlKey = false;

    private boolean useMetaKey = false;

    public static AddonHotKeyData buildFrom(AddonHotKeyConfig hotkeyConfig) {
        AddonHotKeyData hotkeyData = new AddonHotKeyData();
        hotkeyData.setKeyCode(hotkeyConfig.getKeyCode());
        int modifiers = hotkeyConfig.getModifiers();
        hotkeyData.setUseAltKey((modifiers & KeyEvent.ALT_MASK) != 0);
        hotkeyData.setUseCtrlKey((modifiers & KeyEvent.CTRL_MASK) != 0);
        hotkeyData.setUseMetaKey((modifiers & KeyEvent.META_MASK) != 0);
        hotkeyData.setUseShiftKey((modifiers & KeyEvent.SHIFT_MASK) != 0);
        return hotkeyData;
    }
    
    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isUseAltKey() {
        return useAltKey;
    }

    public void setUseAltKey(boolean useAltKey) {
        this.useAltKey = useAltKey;
    }

    public boolean isUseShiftKey() {
        return useShiftKey;
    }

    public void setUseShiftKey(boolean useShiftKey) {
        this.useShiftKey = useShiftKey;
    }

    public boolean isUseCtrlKey() {
        return useCtrlKey;
    }

    public void setUseCtrlKey(boolean useCtrlKey) {
        this.useCtrlKey = useCtrlKey;
    }

    public boolean isUseMetaKey() {
        return useMetaKey;
    }

    public void setUseMetaKey(boolean useMetaKey) {
        this.useMetaKey = useMetaKey;
    }
}
