package com.kms.katalon.composer.components.impl.dialogs;

public abstract class SynchronizedConfirmationDialog implements Runnable {
    private YesNoAllOptions confirmed;
    
    public YesNoAllOptions getConfirmedValue() {
        return confirmed;
    }

    protected void setConfirmedValue(YesNoAllOptions confirmedValue) {
        this.confirmed = confirmedValue;
    }
}
