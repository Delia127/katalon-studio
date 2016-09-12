package com.kms.katalon.composer.components.tree;

public class TooltipPropertyDescription {
    private static final int DF_TEXT_WIDTH = 50;

    private final String key;

    private final String value;

    private final int maxValueLenght;

    private final boolean lengthLimited;

    private TooltipPropertyDescription(String key, String value) {
        this(key, value, 0, false);
    }

    private TooltipPropertyDescription(String key, String value, int maxSize) {
        this(key, value, maxSize, true);
    }

    private TooltipPropertyDescription(String key, String value, int maxValueWidth, boolean sizeLimited) {
        this.key = key;
        this.value = value;
        this.maxValueLenght = maxValueWidth;
        this.lengthLimited = sizeLimited;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isLengthLimited() {
        return lengthLimited;
    }

    public int getMaxValueLength() {
        return maxValueLenght;
    }

    public static TooltipPropertyDescription createWithDefaultLength(String key, String value) {
        return new TooltipPropertyDescription(key, value, DF_TEXT_WIDTH);
    }

    public static TooltipPropertyDescription create(String key, String value) {
        return new TooltipPropertyDescription(key, value);
    }

}
