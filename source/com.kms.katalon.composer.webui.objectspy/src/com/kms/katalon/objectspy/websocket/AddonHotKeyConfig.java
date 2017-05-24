package com.kms.katalon.objectspy.websocket;

public class AddonHotKeyConfig {
    private int keyCode;

    private int modifiers;

    public AddonHotKeyConfig(int keyCode, int modifiers) {
        super();
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + keyCode;
        result = prime * result + modifiers;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AddonHotKeyConfig other = (AddonHotKeyConfig) obj;
        if (keyCode != other.keyCode)
            return false;
        if (modifiers != other.modifiers)
            return false;
        return true;
    }
}
