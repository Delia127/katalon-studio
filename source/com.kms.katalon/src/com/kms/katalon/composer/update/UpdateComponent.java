package com.kms.katalon.composer.update;

public interface UpdateComponent {
    default UpdateManager getUpdateManager() {
        return UpdateManager.getInstance();
    }
}
