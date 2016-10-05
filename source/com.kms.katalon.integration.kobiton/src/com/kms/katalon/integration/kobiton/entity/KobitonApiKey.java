package com.kms.katalon.integration.kobiton.entity;

public class KobitonApiKey {
    private String alias;

    private String key;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "KobitonApiKey [alias=" + alias + ", key=" + key + "]";
    }

}
