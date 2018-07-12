package com.kms.katalon.application.usagetracking;

public class TrackingEvent {

    private UsageActionTrigger trigger;
    
    private Object data;
    
    public TrackingEvent(UsageActionTrigger trigger, Object data) {
        this.trigger = trigger;
        this.data = data;
    }

    public UsageActionTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(UsageActionTrigger trigger) {
        this.trigger = trigger;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
