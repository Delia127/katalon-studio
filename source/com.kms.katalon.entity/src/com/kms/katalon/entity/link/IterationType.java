package com.kms.katalon.entity.link;

public enum IterationType {
    ALL("All"), RANGE("Range"), SPECIFIC("Specific");

    private final String text;

    private IterationType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
