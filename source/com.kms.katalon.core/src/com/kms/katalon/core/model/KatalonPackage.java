package com.kms.katalon.core.model;

public enum KatalonPackage {
    KSE("KSE"),
    FLOATING_ENGINE("FLOATING_ENGINE"),
    ENGINE("ENGINE");
    
    private final String packageName;
    
    private KatalonPackage(String packageName) {
        this.packageName = packageName;
    }
    
    public String getPackageName() {
        return packageName;
    }
}
