package com.kms.katalon.composer.testcase.preferences;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class StoredKeyword {
    private final String keywordName; // Eg: setText, click...

    private final String keywordClass; // class name of keyword.

    private final boolean isCustom; // is custom keyword or not

    public StoredKeyword(String keywordClass, String keywordName, boolean isCustom) {
        this.keywordClass = keywordClass;
        this.keywordName = keywordName;
        this.isCustom = isCustom;
    }

    public String getKeywordName() {
        return keywordName;
    }

    public String getKeywordClass() {
        return keywordClass;
    }

    public boolean isCustom() {
        return isCustom;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(keywordName).append(keywordClass).append(isCustom).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StoredKeyword other = (StoredKeyword) obj;
        return new EqualsBuilder().append(keywordClass, other.keywordClass)
                .append(keywordName, other.keywordName)
                .append(isCustom, other.isCustom)
                .isEquals();
    }
}
