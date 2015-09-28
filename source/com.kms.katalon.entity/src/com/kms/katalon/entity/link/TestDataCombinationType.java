package com.kms.katalon.entity.link;

public enum TestDataCombinationType {
    ONE("One"), MANY("Many");

    private final String text;

    private TestDataCombinationType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
