package com.kms.katalon.composer.components.impl.constants;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum TextContentType {
    TEXT("Text", "text/plain"),
    JSON("JSON", "application/json"),
    XML("XML", "application/xml"),
    HTML("HTML", "text/html"),
    JAVASCRIPT("JavaScript", "application/javascript");

    private String text;

    private String contentType;

    private TextContentType(String text, String contentType) {
        this.text = text;
        this.contentType = contentType;
    }

    public String getText() {
        return text;
    }

    public String getContentType() {
        return contentType;
    }

    public static String[] getTextValues() {
        return Arrays.asList(values()).stream().map(t -> t.getText()).toArray(String[]::new);
    }

    public static TextContentType evaluateContentType(String contentType) {
        switch (StringUtils.defaultString(contentType)) {
            case "application/json":
            case "application/ld+json":
                return TextContentType.JSON;
            case "application/javascript":
            case "application/ecmascript":
                return TextContentType.JAVASCRIPT;
            case "application/xml":
            case "application/atom+xml":
            case "application/soap+xml":
                return TextContentType.XML;
            case "text/html":
            case "application/xhtml+xml":
                return TextContentType.HTML;
            default:
                return TextContentType.TEXT;
        }
    }
}