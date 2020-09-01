package com.kms.katalon.composer.webservice.openapi;

public class OpenApiRestParameter {

    private String name;

    private String value;

    private Style style;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static enum Style {
        HEADER, MATRIX, QUERY, TEMPLATE, PLAIN
    }
}
