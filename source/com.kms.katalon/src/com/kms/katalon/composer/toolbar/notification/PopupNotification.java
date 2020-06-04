package com.kms.katalon.composer.toolbar.notification;

public class PopupNotification {

    private INotificationContent content;

    private TrackedNotification tracked;

    public INotificationContent getContent() {
        return content;
    }

    public void setContent(INotificationContent content) {
        this.content = content;
    }

    public TrackedNotification getTracked() {
        return tracked;
    }

    public void setTracked(TrackedNotification tracked) {
        this.tracked = tracked;
    }
}