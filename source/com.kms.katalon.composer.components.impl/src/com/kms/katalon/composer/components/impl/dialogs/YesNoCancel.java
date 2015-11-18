package com.kms.katalon.composer.components.impl.dialogs;

public enum YesNoCancel {
    YES("Yes"), NO("No"), CANCEL("Cancel");

    private final String text;

    private YesNoCancel(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static YesNoCancel getOption(int value) {
        for (YesNoCancel option : YesNoCancel.values()) {
            if (value == option.ordinal()) {
                return option;
            }
        }
        return YesNoCancel.CANCEL;
    }
    
    public static String[] valuesString() {
        YesNoCancel[] values = YesNoCancel.values();
        String[] valuesString = new String[values.length];
        for (int i = 0; i< values.length; i++) {
            valuesString[i] = values[i].toString();
        }
        return valuesString;
    }
}
