package com.kms.katalon.composer.components.impl.dialogs;

public enum YesNoAllOptions {
    YES("Yes"), YES_TO_ALL("Yes to All"), NO("No"), NO_TO_ALL("No to All");

    private final String text;

    private YesNoAllOptions(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static YesNoAllOptions getOption(int value) {
        for (YesNoAllOptions option : YesNoAllOptions.values()) {
            if (value == option.ordinal()) {
                return option;
            }
        }
        return YesNoAllOptions.NO;
    }
}
