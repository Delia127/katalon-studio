package com.kms.katalon.objectspy.websocket;

import org.eclipse.swt.SWT;

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
        hotkeyData.setUseAltKey((modifiers & SWT.ALT) != 0);
        hotkeyData.setUseCtrlKey((modifiers & SWT.CTRL) != 0);
        hotkeyData.setUseMetaKey((modifiers & SWT.COMMAND) != 0);
        hotkeyData.setUseShiftKey((modifiers & SWT.SHIFT) != 0);
        return hotkeyData;
    }

    /**
     * Accept key from a..z and ~
     */
    private static int convertSWTKeyCodeToJsKeyCode(int swtKeyCode) {
        if (swtKeyCode == '`') {
            return 192;
        }
        if (swtKeyCode >= 'a' && swtKeyCode <= 'z') {
            return swtKeyCode - 32;
        }
        return swtKeyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = convertSWTKeyCodeToJsKeyCode(keyCode);
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
