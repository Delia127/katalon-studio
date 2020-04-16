package com.kms.katalon.composer.toolbar.notification;

public class PopupNotification {

    private NotificationContent content;

    private TrackedNotification tracked;

    public NotificationContent getContent() {
        return content;
    }

    public void setContent(NotificationContent content) {
        this.content = content;
    }

    public TrackedNotification getTracked() {
        return tracked;
    }

    public void setTracked(TrackedNotification tracked) {
        this.tracked = tracked;
    }
}