package com.kms.katalon.console.utils;


public class VersionInfo {
    private String version;
    private int buildNumber;
    
    public static final String MINIMUM_VERSION = "3.0.5";
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VersionInfo other = (VersionInfo) obj;
        if (buildNumber != other.buildNumber) {
            return false;
        }
        if (version == null && other.version != null) {
            return false;
        } else if (version != null && !version.equals(other.version)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + buildNumber;
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }
    
    
}
