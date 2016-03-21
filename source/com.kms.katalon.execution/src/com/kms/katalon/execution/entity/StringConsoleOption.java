package com.kms.katalon.execution.entity;

public abstract class StringConsoleOption extends AbstractConsoleOption<String> {
    @Override
    public Class<String> getArgumentType() {
        return String.class;
    }

    @Override
    public boolean hasArgument() {
        return true;
    }
}
