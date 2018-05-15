package com.kms.katalon.composer.update.models;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class LastestVersionInfo {
    private String latestVersion;

    private String latestUpdateLocation;

    private boolean latestVersionIgnored;

    private String releaseNotesLink;

    private boolean newMechanism;

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

    public static boolean isNewer(String version, String comparedVersion) {
        if (StringUtils.equals(version, comparedVersion)) {
            return false;
        }

        int[] thisVer = Arrays.stream(StringUtils.split(version, '.')).mapToInt(Integer::parseInt).toArray();

        int[] thatVer = Arrays.stream(StringUtils.split(comparedVersion, '.')).mapToInt(Integer::parseInt).toArray();
        
        int maxLength = Math.max(thisVer.length, thatVer.length);
        while (thisVer.length < maxLength) {
            thisVer = ArrayUtils.add(thisVer, 0);
        }
        
        while (thatVer.length < maxLength) {
            thatVer = ArrayUtils.add(thatVer, 0);
        }

        for (int i = 0; i < maxLength; i++) {
            if (thisVer[i] == thatVer[i]) {
                continue;
            }

            if (thisVer[i] > thatVer[i]) {
                return true;
            }
        }

        return false;
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

}
