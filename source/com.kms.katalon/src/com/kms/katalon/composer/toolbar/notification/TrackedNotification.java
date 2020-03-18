package com.kms.katalon.composer.toolbar.notification;

import java.util.Date;

public class TrackedNotification {

    private String id;

    private Date trackedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTrackedDate() {
        return trackedDate;
    }

    public void setTrackedDate(Date trackedDate) {
        this.trackedDate = trackedDate;
    }
}
