package com.kms.katalon.composer.update.models;

public class LastestVersionInfo {
    private String latestVersion;

    private String latestUpdateLocation;

    private boolean latestVersionIgnored;

    private String releaseNotesLink;

    private boolean newMechanism;

    private boolean quickRelease;

    public boolean isLatestVersionIgnored() {
        return latestVersionIgnored;
    }

    public void setLatestVersionIgnored(boolean latestVersionIgnored) {
        this.latestVersionIgnored = latestVersionIgnored;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isNewMechanism() {
        return newMechanism;
    }

    public void setNewMechanism(boolean newMechanism) {
        this.newMechanism = newMechanism;
    }

    public String getLatestUpdateLocation() {
        return latestUpdateLocation;
    }

    public void setLatestUpdateLocation(String latestUpdateLocation) {
        this.latestUpdateLocation = latestUpdateLocation;
    }

    public String getReleaseNotesLink() {
        return releaseNotesLink;
    }

    public void setReleaseNotesLink(String releaseNotesLink) {
        this.releaseNotesLink = releaseNotesLink;
    }

    public boolean isQuickRelease() {
        return quickRelease;
    }

    public void setQuickRelease(boolean quickRelease) {
        this.quickRelease = quickRelease;
    }

}
