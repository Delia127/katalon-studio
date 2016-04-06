package com.kms.katalon.composer.webui.recorder.action;

public class HTMLValidationAction extends HTMLAbstractAction {
    public static final String VALIDATION_ACTION_PREFIX = "verify";

    public HTMLValidationAction(String name, String mappedKeywordClassName, String mappedKeywordClassSimpleName,
            String mappedKeywordMethod, String description) {
        super(name, mappedKeywordClassName, mappedKeywordClassSimpleName, mappedKeywordMethod, description);
    }
}
