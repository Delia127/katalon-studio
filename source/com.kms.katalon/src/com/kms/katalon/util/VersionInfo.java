package com.kms.katalon.util;


public class VersionInfo {
    public String version;
    public int buildNumber;
    
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
}
